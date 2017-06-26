getwd()

d <- data.frame(read.table("context_exp_3.csv", header=FALSE, sep=","))
overall <- d$V2 + d$V3

plot(d$V1, d$V2, type="p", xlab="Domain size", ylab="Time cost (seconds)", main="Effects of domain size over performance")
lines(d$V1, d$V3, type="p", col="red")
axis(side = 4)
mtext(side = 4, line = 0, 'Space (mb)', col="red")
lines(d$V1, overall, type="p", col="blue") # Plot the sum of time and storage costs

legend("topleft", c("Time", "Space", "Overall"), lty=1, col=c("black", "red", "blue"))

