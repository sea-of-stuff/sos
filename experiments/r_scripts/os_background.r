setwd("/Users/sic2/git/sos/experiments")
getwd()

d <- read.csv("output/pr_1__2017_10_26T16_24_38_828Z_os.tsv", header=TRUE, sep="\t")

plot(d$User.Uptime, d$Mem.Total.Bytes, 
     col="blue",
     type="b", pch=20,
     ylim=c(min(d$Mem.Free.Bytes, d$Mem.Total.Bytes, d$Resident.Bytes, d$Total.Bytes), 
            max(d$Mem.Free.Bytes, d$Mem.Total.Bytes, d$Resident.Bytes, d$Total.Bytes)),
     xlim=c(min(d$User.Uptime), max(d$User.Uptime)),
     xlab="ms",
     ylab="Bytes")
lines(d$User.Uptime, d$Mem.Free.Bytes, col="red", type="b", pch=20)
lines(d$User.Uptime, d$Resident.Bytes, col="black", type="b", pch=20)
lines(d$User.Uptime, d$Total.Bytes, col="green", type="b", pch=20)

legend("topright", legend=c("Mem Total Bytes", "Mem Free Bytes", "Resident Bytes", "Total Bytes"),
       fill=c("blue", "red", "grey0", "green"), cex=0.8, inset=.1)


d$CPU.Process.Load <- d$CPU.Process.Load * 100
plot(d$User.Uptime, d$CPU.Process.Load, 
     col="red",
     type="b", pch=20,
     ylab="CPU (%)",
     xlab="ms")
