# This script reads a CSV file with the format:
#
# Configuration,Time (s)
# Data,2
# Data,2.1
# Data,2.1
# Metadata,1.2
# Metadata,1.3
# Metadata,1.1
# Node Info,0.4
# .., .., .., ..
#
# COMMENT ABOUT WHAT THIS SCRIPT DOES
#
# Author: Simone Ivan Conte (sic2)
#
# Useful links:
# https://datascienceplus.com/building-barplots-with-error-bars/

setwd("/Users/sic2/git/sos/experiments")
getwd()

# Read the CVS file
d <- read.csv("context_exp_pr_1.csv", header=TRUE, sep=",")

aggr <- aggregate(d[, 2],
                  by = list(config = d[, 1]),
                  FUN = function(x) c(mean = mean(x), sd = sd(x), n = length(x)))

d_processed <- do.call(data.frame, aggr)

# Compute standard error per group
d_processed$se <- d_processed$x.sd / sqrt(d_processed$x.n)

# Rename columns
colnames(d_processed) <- c("Configuration", "mean", "sd", "n", "se")
d_processed$names <- d_processed$Config


# Estimate top limit on the y-axis
plotTop <- max(d_processed$mean) + max(d_processed$se) +
  d_processed[d_processed$mean == max(d_processed$mean), 5] * 5

par(ask=F) # Do not ask to print plot on the console
par(mar=c(5,6,4,2)+3,mgp=c(5,1,0)) # Add space to show all labels
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


# Using boxplot
boxplot(d[,2]~d[,1], data=d,
        las=2, # Draw x labels vertically
        main="Predicate performance against different settings",
        ylab="Time (s)")

# Save the processed data
write.csv(d_processed, "processed/context_exp_pr_1.csv")
