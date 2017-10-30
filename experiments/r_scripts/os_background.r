setwd("/Users/sic2/git/sos/experiments")
getwd()

d <- read.csv("output/pr_1__2017_10_27T13_03_39_007Z_os.tsv", header=TRUE, sep="\t")

# Convert to MB
d$Mem.Used.Bytes <- d$Mem.Used.Bytes / 1000000
d$Mem.Total.Bytes <- d$Mem.Total.Bytes / 1000000
d$Resident.Bytes <- d$Resident.Bytes / 1000000
d$Total.Bytes <- d$Total.Bytes / 1000000
d$Physical.Mem.Total.Bytes <- d$Physical.Mem.Total.Bytes / 1000000
d$Physical.Mem.Used.Bytes <- d$Physical.Mem.Used.Bytes / 1000000

d$User.Uptime <- d$User.Uptime / 1000

plot(d$User.Uptime, d$Mem.Total.Bytes, 
     col="blue",
     type="p", pch=20, cex = .4,
     ylim=c(min(d$Mem.Used.Bytes, d$Mem.Total.Bytes, d$Resident.Bytes, d$Total.Bytes, d$Physical.Mem.Total.Bytes, d$Physical.Mem.Used.Bytes), 
            max(d$Mem.Used.Bytes, d$Mem.Total.Bytes, d$Resident.Bytes, d$Total.Bytes, d$Physical.Mem.Total.Bytes, d$Physical.Mem.Used.Bytes)),
     xlim=c(min(d$User.Uptime), max(d$User.Uptime)),
     xlab="Time (s)",
     ylab="MB")
lines(d$User.Uptime, d$Mem.Used.Bytes, col="red", type="p", pch=20, cex = .4)
lines(d$User.Uptime, d$Resident.Bytes, col="black", type="p", pch=20, cex = .4)
lines(d$User.Uptime, d$Total.Bytes, col="green", type="p", pch=20, cex = .4)
lines(d$User.Uptime, d$Physical.Mem.Total.Bytes, col="deeppink", type="p", pch=20, cex = .4)
lines(d$User.Uptime, d$Physical.Mem.Used.Bytes, col="goldenrod3", type="p", pch=20, cex = .4)

legend("topright", 
       legend=c("Swap Mem Total Bytes", "Swap Mem Free Bytes", 
                "Process Resident Bytes", "Process Total Memory Bytes", 
                "Physical Mem Total Bytes", "Physical Mem Used Bytes"),
       fill=c("blue", "red", "grey0", "green", "deeppink", "goldenrod3"), 
       cex=0.8, inset=.1)



#####################################################
d$CPU.Process.Load <- d$CPU.Process.Load * 100
plot(d$User.Uptime, d$CPU.Process.Load, 
     col="red",
     type="b", pch=20,
     ylim=c(0, 100),
     ylab="CPU (%)",
     xlab="Time (s)")

