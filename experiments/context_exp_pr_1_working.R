setwd("/Users/sic2/git/sos/experiments")
getwd()

# pr_1__2017_08_21T15_35_33_894Z.TSV (on macs_500k)
# pr_1__2017_08_23T11_50_08_412Z.TSV (on text dataset) (40 iterations x configuration type)
# pr_1__2017_08_23T12_03_11_247Z.TSV (on random_1)
# pr_1__2017_08_25T13_39_53_118Z.TSV (text with manifest info)
# Read the CVS file
# pr_1__2017_08_23T11_50_08_412Z_cleaned.TSV
d <- read.csv("output/pr_1__2017_08_23T11_50_08_412Z_cleaned.TSV", header=TRUE, sep="\t")
d$ContextName <- sapply(strsplit(as.character(d$Message), '_'), '[', 1) # Split by 'SHA' if we want to look at the individual contexts
d$Measures <- d$User.Measure / 1000000000.0; # Nanoseconds to seconds

aggr <- aggregate(d$Measures ~ d$ContextName,
                  FUN = function(x) c(mean = mean(x), sd = sd(x), n = length(x)))

d_processed <- do.call(data.frame, aggr)

# Compute standard error per group
d_processed$se <- d_processed[,3] / sqrt(d_processed[,4])

# Rename columns
colnames(d_processed) <- c("Configuration", "mean", "sd", "n", "se")
d_processed$names <- d_processed$Config

# Estimate top limit on the y-axis
plotTop <- max(d_processed$mean) + max(d_processed$se) +
  d_processed[d_processed$mean == max(d_processed$mean), 5] * 4

par(ask=F) # Do not ask to print plot on the console
# mar=c(bottom, left, top, right)
# mgp=c(axis.title.position, axis.label.position, axis.line.position))
par(mar=c(12,4,4,2)+3, mgp=c(5,1,0)) # Add space to show all labels
barCenters <- barplot(height = d_processed$mean,
                      names.arg = d_processed$names,
                      beside = true, las =2,
                      ylim = c(0, plotTop),
                      main = "Predicate performance against different settings.",
                      sub = "This diagram shows the average time to run a predicate of some given type against the entire dataset",
                      ylab = "Time (s)",
                      border = "black",
                      axes = T)

# Add error bars
segments(barCenters, d_processed$mean - d_processed$se * 2, barCenters,
         d_processed$mean + d_processed$se * 2, lwd = 1.5)

arrows(barCenters, d_processed$mean - d_processed$se * 2, barCenters,
       d_processed$mean + d_processed$se * 2, lwd = 1.5, angle = 90,
       code = 3, length = 0.05)


# BOPLOT
par(mar=c(12,4,4,2)+3) # Add space to show all labels
x <- boxplot(d$User.Measure~d$ContextName, data=d,
             las=2, # Draw x labels vertically
             main="Predicate performance against different settings",
             ylab="Time (s)")


# CHECKING DISTRIBUTION OF DATA
x = subset(d, ContextName=="META")
descdist(x$Measures, discrete = FALSE)
fit.norm <- fitdist(x$Measures, "norm")
plot(fit.norm)
fit.weibull <- fitdist(x$Measures, "weibull")
plot(fit.weibull)

# P-test for two sets
x = subset(d, ContextName=="ALL")
y = subset(d, ContextName=="META")
t.test(x$Measures, y$Measures)

# linear model test
# https://www.r-bloggers.com/one-way-analysis-of-variance-anova/
model <- lm(formula = Measures ~ ContextName, data = d)
summary(model)
anova(model)

# Power test
# https://cran.r-project.org/web/packages/pwr/vignettes/pwr-vignette.html
