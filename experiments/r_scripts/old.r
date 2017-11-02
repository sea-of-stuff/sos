# 
# Resources:
# https://rcompanion.org/rcompanion/d_06.html

# WRONG 
# Testing pairs of sets using the t-test will lead to the Type1 error. 
# Instead we need to do:
# - a non-parametric test if the sets do not have a normal distribution
# - a posthoc test to check which sets are significantly different with which one
# 
# I am keeping the code below just to remember myself that this type of stats is wrong
# P-test for two sets
# x = subset(d, ContextName=="ALL")
# y = subset(d, ContextName=="META")
# t.test(x$Measures, y$Measures)

# Power test
# https://cran.r-project.org/web/packages/pwr/vignettes/pwr-vignette.html