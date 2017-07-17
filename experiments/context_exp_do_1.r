# This script reads a CSV file with the format:
#
# Cardinality, Time to process the Domain
# 1, 10
# 1, 10.4
# 1, 9.88
# 2, 15.3
# 2, 16.8
# .., ..
#
#
# Author: Simone Ivan Conte (sic2)

setwd("/Users/sic2/git/sos/experiments")
getwd()

# Read the CVS file
d <- read.csv("context_exp_do_1.csv", header=TRUE, sep=",")

# Do some calculations on the data (d[,2])
c_mean <- aggregate(d[,2], list(d[,1]), mean);
c_mean <- setNames(c_mean, c(colnames(d[1]), 'mean'))

c_sd <- aggregate(d[,2], list(d[,1]), sd);
c_sd <- setNames(c_sd, c(colnames(d[1]), 'sd'))

c <- merge(c_mean, c_sd)

x <- c[,1]
avg <- c$mean
sdev <- c$sd

phc_symbol <- 19

plot(x, avg, 
     type="p", pch=phc_symbol, # The phc param specified the symbol used for the data point
     xlab="Cardinality",
     ylab="Time (seconds)", 
     ylim=range(c(0, avg+sdev)), # yAxis starts from 0. Alternatively c(avg-sdev, avg+sdev)
     main="Cardinality over Time")

# Connect the avg points
lines(x[order(x)], avg[order(x)], xlim=range(x), ylim=range(avg), pch=16)

# Error bars using standard deviation
arrows(x, avg-sdev, x, avg+sdev, length=0.05, angle=90, code=3)

#
# The text below will be used as a caption when adding the plot to a document.
# Caption: 
# CAPTION_TEXT


# STATISTICAL TESTS
summary(d)