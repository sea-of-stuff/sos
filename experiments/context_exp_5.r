getwd()

d <- data.frame(read.table("context_exp_5.csv", header=FALSE, sep=","))
max_y <- max(d)
plot(d, type="p", xlab="# Nodes", ylab="Overall time cost", main="# Nodes over which a context is run vs Time", ylim=c(0, max_y + 10), yaxs="i")
lmout <- loess(d$V2 ~ d$V1)
lines(predict(lmout), col='red', lwd=1)

