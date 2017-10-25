# This script reads a CSV file with the format:
#
# Configuration,Data Processed (MB),Time (s),Space Used (MB)
# 1,100,2,1
# 2,100,2.2,1.2
# 3,100,1.9,0.9
# 4,100,2.4,1.1
# .., .., .., ..
#
# FIX COMMENT
# The script then plots the average points per NoContexts.
# Error bars are plotted using the standard deviation.
#
# Author: Simone Ivan Conte (sic2)
#
# Useful links:
# https://datascienceplus.com/building-barplots-with-error-bars/

setwd("/Users/sic2/git/sos/experiments")
getwd()

# Read the CVS file
d <- read.csv("context_exp_1.csv", header=TRUE, sep=",")

d_throughput <- d[,2] / d[,3]
aggr <- aggregate(d_throughput,
                  by = list(config = d[, 1]),
                  FUN = function(x) c(mean = mean(x), sd = sd(x), n = length(x)))

d_processed <- do.call(data.frame, aggr)

# Compute standard error per group
d_processed$se <- d_processed$x.sd / sqrt(d_processed$x.n)

# Rename columns
colnames(d_processed) <- c("Config", "mean", "sd", "n", "se")
d_processed$names <- c(paste("Config", d_processed$Config))


# Estimate top limit on the y-axis
plotTop <- max(d_processed$mean) + max(d_processed$se) +
  d_processed[d_processed$mean == max(d_processed$mean), 5] * 5

par(ask=F) # Do not ask to print plot on the console
barCenters <- barplot(height = d_processed$mean,
                    names.arg = d_processed$names,
                    beside = true, las =2,
                    ylim = c(0, plotTop),
                    main = "Context throughput per different\n types of context configurations",
                    ylab = "Throughput (MB/s)",
                    border = "black",
                    axes = T)

# Add error bars

segments(barCenters, d_processed$mean - d_processed$se * 2, barCenters,
         d_processed$mean + d_processed$se * 2, lwd = 1.5)

arrows(barCenters, d_processed$mean - d_processed$se * 2, barCenters,
       d_processed$mean + d_processed$se * 2, lwd = 1.5, angle = 90,
       code = 3, length = 0.05)

