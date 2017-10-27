install.packages("PMCMR")
install.packages("FSA")
library("PMCMR", lib.loc="/Library/Frameworks/R.framework/Versions/3.3/Resources/library")
library("FSA", lib.loc="/Library/Frameworks/R.framework/Versions/3.3/Resources/library")

library("fitdistrplus", lib.loc="/Library/Frameworks/R.framework/Versions/3.3/Resources/library")

install.packages("stargazer")
library(stargazer)

setwd("/Users/sic2/git/sos/experiments")
getwd()

# Read the CVS file
d <- read.csv("output/pr_1__2017_10_26T15_45_06_944Z.tsv", header=TRUE, sep="\t")
d$ContextName <- d$Message # sapply(strsplit(as.character(d$Message), '_'), '[', 1) # Split by 'SHA' if we want to look at the individual contexts

# https://jpwendler.wordpress.com/2013/05/21/reordering-the-factor-levels-in-r-boxplots-and-making-them-look-pretty-with-base-graphics/
d$ContextName<-factor(d$ContextName, levels=c("base", 
                                              "search_common_word", "search_uncommon_word", "common_word_occurs_at_least_10_times",
                                              "meta_and_search_common_word", "meta_and_search_uncommon_word", "meta_and_common_word_occurs_at_least_10_times",
                                              "metadata", "multi_metadata",
                                              "manifest"
                                              ))

d$Measures <- d$User.Measure / 1000000000.0; # Nanoseconds to seconds

aggr <- aggregate(d$Measures ~ d$ContextName,
                  FUN = function(x) c(mean = mean(x), sd = sd(x), n = length(x)))

d_processed <- do.call(data.frame, aggr)

# Compute standard error per group
d_processed$se <- d_processed[,3] / sqrt(d_processed[,4])

# Rename columns
colnames(d_processed) <- c("Configuration", "mean", "sd", "n", "se")
d_processed$names <- d_processed$Config

# http://www.stat.columbia.edu/~tzheng/files/Rcolor.pdf
# https://stackoverflow.com/questions/15212884/colouring-different-group-data-in-boxplot-using-r
colors = c(rep("red",1),rep("deepskyblue",3),rep("green",3), rep("tomato", 2), rep("gray90", 1))

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
                      main = "TODO (this is the time for all assets!)",
                      ylab = "Time (s)",
                      border = "black",
                      col=colors,
                      axes = T)

legend("topright", legend=c("ALL", "Meta", "Data", "Manifest", "Meta and Data"),
       fill=c("red", "deepskyblue", "green", "tomato", "gray90"), cex=0.8, inset=.05)

# Add error bars
segments(barCenters, d_processed$mean - d_processed$se * 2, barCenters,
         d_processed$mean + d_processed$se * 2, lwd = 1.5)

arrows(barCenters, d_processed$mean - d_processed$se * 2, barCenters,
       d_processed$mean + d_processed$se * 2, lwd = 1.5, angle = 90,
       code = 3, length = 0.05)


##################
# BOXPLOT
par(mar=c(15,4,4,2)+3) # Add space to show all labels
x <- boxplot(d$Measures~d$ContextName, data=d,
             las=2, # Draw x labels vertically
             main="Predicate performance against different settings",
             ylab="Time (s) - log scale", 
             log = "y",
             col=colors)

legend("topright", legend=c("Base", "Data", "Meta and Data", "Metadata", "Manifest"),
       fill=c("red", "deepskyblue", "green", "tomato", "gray90"), cex=0.8, inset=.05)


# Kruskal-Wallis Test
# http://www.r-tutor.com/elementary-statistics/non-parametric-methods/kruskal-wallis-test
d$Kruskal <- as.factor(d$ContextName)
kruskal.test(d$User.Measure ~ d$Kruskal, data=d)
# POSTHOC TESTS
# adjustments to the p-value can be made. See ?p.adjust for allowed methods
#
# - Zar (2010) states that the Dunn test is appropriate for groups with unequal numbers of observations.
# - Zar (2010) suggests that the Nemenyi test is not appropriate for groups with unequal numbers of observations.
#
# Zar, J.H. 2010. Biostatistical Analysis, 5th ed.  Pearson Prentice Hall: Upper Saddle River, NJ.
#
# DUNN
# https://www.rdocumentation.org/packages/PMCMR/versions/4.1/topics/posthoc.kruskal.dunn.test
posthoc.kruskal.dunn.test(d$User.Measure ~ d$Kruskal, data=d)
#
# NEMENYI (not suitable for this dataset)
# https://www.rdocumentation.org/packages/PMCMR/versions/4.1/topics/posthoc.kruskal.nemenyi.test
posthoc.kruskal.nemenyi.test(d$User.Measure ~ d$Kruskal, data=d)
# 
# Resources:
# https://rcompanion.org/rcompanion/d_06.html

# WRONG 
# Testing pairs of sets using the t-test will lead to the Type1 error. 
# Instead we need to do:
# - a non-parametric test if the sets do not have a normal distribution
# - a posthoc test to check which sets are significantly different with which one
# 
# I am keeping the code below just to remember myself that this type of stats is wrong
# P-test for two sets
# x = subset(d, ContextName=="ALL")
# y = subset(d, ContextName=="META")
# t.test(x$Measures, y$Measures)

# Power test
# https://cran.r-project.org/web/packages/pwr/vignettes/pwr-vignette.html
