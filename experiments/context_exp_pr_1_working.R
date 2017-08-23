setwd("/Users/sic2/git/sos/experiments")
getwd()

# pr_1__2017_08_21T15_35_33_894Z.TSV (on macs_500k)
# pr_1__2017_08_23T11_46_18_471Z.TSV (on text dataset)
# pr_1__2017_08_22T13_51_57_484Z.TSV (on random_1)
# Read the CVS file
d <- read.csv("output/pr_1__2017_08_23T11_46_18_471Z.TSV", header=TRUE, sep="\t")
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
  d_processed[d_processed$mean == max(d_processed$mean), 5] * 2

par(ask=F) # Do not ask to print plot on the console
par(mar=c(12,4,4,2)+3) # Add space to show all labels
barCenters <- barplot(height = d_processed$mean,
                      names.arg = d_processed$names,
                      beside = true, las =2,
                      ylim = c(0, plotTop),
                      main = "Predicate performance against different settings",
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

