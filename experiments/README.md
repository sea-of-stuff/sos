# R Notes and Useful links:

## Generic tutorials

http://www.cyclismo.org/tutorial/R/

Aggregate function:
http://www.cookbook-r.com/Manipulating_data/Summarizing_data/#using-aggregate

## Plotting

http://geog.uoregon.edu/GeogR/topics/
    http://geog.uoregon.edu/GeogR/topics/scatterplots.html

http://www.harding.edu/fmccown/r/

How can I plot with 2 different y-axes?
https://www.r-bloggers.com/r-single-plot-with-two-different-y-axes/
https://stackoverflow.com/a/6143251/2467938

## Statistical test

The t-test should be used when comparing two things (e.g. config1 vs config2)
Can also use Wilcoxon test (if do not assume a normal distribution of the input data)

shapiro.test(numericVector) - Used to test if input has normal distribution (for p < 0.05, the input is not normally distributed)

Kolmogorov And Smirnov Test to check if two sets have the same distribution

Fisher’s F-Test to check if two samples have same variance
Alternatively: fligner.test() and bartlett.test()

Chi Squared Test to test if two categorical variables are dependent, by means of a contingency table.

### Stats Resources:

- http://r-statistics.co/Statistical-Tests-in-R.html
- http://www.gardenersown.co.uk/education/lectures/r/basics.htm

### Distribution info

Filtering data by label: `subdataset = subset(dataset, ContextName=="DATA")`

https://stats.stackexchange.com/questions/132652/how-to-determine-which-distribution-fits-my-data-best




### About boxplots in R

- https://flowingdata.com/2008/02/15/how-to-read-and-use-a-box-and-whisker-plot/
- http://informationandvisualization.de/blog/box-plot

- http://www.cookbook-r.com/Graphs/Plotting_means_and_error_bars_(ggplot2)/
- http://rstudio-pubs-static.s3.amazonaws.com/3256_bb10db1440724dac8fa40da5e658ada5.html
