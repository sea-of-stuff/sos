setwd("/Users/sic2/git/sos/experiments")
getwd()

d <- read.csv("output/pr_1__2017_11_07T14_09_39_316Z_dataset_files.tsv", header=TRUE, sep="\t")
d$Size <- d$Size / 1000 # byte to kb
hist(d$Size, 
     breaks=50,
     freq=FALSE,
     main="Dataset density over size",
     ylab="Density",
     xlab="File size (KB)")


# Density
bp <- barplot(prop.table(table(d$Filetype[order(d$Filetype, decreasing = TRUE)])),
        ylab="Density",
        xlab="Filetype",
        xaxt="n")

labs <- paste(names(table(d$Filetype)))
text(bp, par("usr")[3], labels = labs, srt = 90, adj = c(1.1,1.1), xpd = TRUE, cex=.9)

# Frequency
barplot(table(d$Filetype),
        ylab="Density",
        xlab="Filetype",
        xaxt="n")

labs <- paste(names(table(d$Filetype)))
text(bp, par("usr")[3], labels = labs, srt = 90, adj = c(1.1,1.1), xpd = TRUE, cex=.9)
