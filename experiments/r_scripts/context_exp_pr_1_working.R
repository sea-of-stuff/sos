install.packages("PMCMR")
# install.packages("FSA")
library("PMCMR", lib.loc="/Library/Frameworks/R.framework/Versions/3.3/Resources/library")
# library("FSA", lib.loc="/Library/Frameworks/R.framework/Versions/3.3/Resources/library")
# library("fitdistrplus", lib.loc="/Library/Frameworks/R.framework/Versions/3.3/Resources/library")
# install.packages("stargazer")
# library(stargazer)
library(ggplot2)


setwd("/Users/sic2/git/sos/experiments")
getwd()

source("r_scripts/utils_stats.r")
source("r_scripts/kruskal.r")

# Read the CVS file
d <- read.csv("output/pr_1__2017_10_26T16_24_38_828Z.tsv", header=TRUE, sep="\t")
d <- d[d$StatsTYPE == 'predicate',]
d$Message <- droplevels(d$Message)
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

##################
# BOXPLOT
par(mar=c(20,4,4,2)+3) # Add space to show all labels
x <- boxplot(d$Measures~d$ContextName, data=d,
             outline=FALSE,
             las=2, # Draw x labels vertically
             main="Predicate performance against different settings",
             ylab="Time (s) - linear scale",
             col=colors)

legend("topright", legend=c("Base", "Data", "Meta and Data", "Metadata", "Manifest"),
       fill=c("red", "deepskyblue", "green", "tomato", "gray90"), cex=0.8, inset=.05)



# This plot is weird because we cannot exclude very large outliers
# PLOT PRED TIMES PER ASSET
#dd <- d[d$Subtype == 'predicate',]
#dd$Message <- droplevels(dd$Message)
#ggplot(data=dd, aes(x=dd$ContextName, y=dd$Measures)) + 
#  geom_boxplot(outlier.alpha = 0.5, outlier.color = "red") +
#  geom_point(color="grey50", position="jitter", alpha=.1) +
#  theme_bw() +
#  theme(axis.text.x=element_text(angle=90,hjust=1)) +
#  labs(title="Predicates per asset....", x="Predicate", y="Time (s)")


# PLOT TIME TO RUN PREDICATE, PRE-PHASE, POST-PRED-PHASE ETC OVER DATASET
dd <- d[d$Subtype != 'predicate',]
dd$Message <- droplevels(dd$Message)

# http://www.cookbook-r.com/Graphs/Plotting_means_and_error_bars_(ggplot2)/
dd <- summarySE(dd, measurevar="Measures", groupvars =c("ContextName", "StatsTYPE", "Subtype"))

ggplot(data=dd, aes(x=dd$ContextName, y=dd$Measures, fill=dd$Subtype)) + 
  geom_bar(stat="identity", width=.5) +
  geom_errorbar(aes(ymin=dd$Measures-dd$se, ymax=dd$Measures+dd$se),
                width=.2) +
  theme_bw() +
  theme(axis.text.x=element_text(angle=90,hjust=1), 
        axis.text=element_text(size=14),
        axis.title=element_text(size=16,face="bold")) +
  labs(title="Predicates per asset....", x="Predicate", y="Time (s)", fill="Run section")




# TIME TO RUN PREDICATE OVER DATASET
dd <- d[d$Subtype == 'predicate_dataset',]
dd$Message <- droplevels(dd$Message)

# http://www.cookbook-r.com/Graphs/Plotting_means_and_error_bars_(ggplot2)/
dd <- summarySE(dd, measurevar="Measures", groupvars =c("ContextName", "StatsTYPE"))

ggplot(data=dd, aes(x=dd$ContextName, y=dd$Measures)) + 
  geom_bar(stat="identity", width=.5) +
  geom_errorbar(aes(ymin=dd$Measures-dd$se, ymax=dd$Measures+dd$se),
                width=.2) +
  theme_bw() +
  theme(axis.text.x=element_text(angle=90,hjust=1), 
        axis.text=element_text(size=14),
        axis.title=element_text(size=16,face="bold")) +
  labs(title="Time to run all predicates on entire dataset", x="Predicate", y="Time (s)")


##################
# BARPLOT

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




## STAT ANALYSIS
kruskal(d, d$User.Measure, d$ContextName)

kruskal_dunn(d, d$User.Measure, d$ContextName)
kruskal_nemenyi(d, d$User.Measure, d$ContextName)
