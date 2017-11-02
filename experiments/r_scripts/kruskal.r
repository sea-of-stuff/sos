# install.packages("PMCMR")
library("PMCMR", lib.loc="/Library/Frameworks/R.framework/Versions/3.3/Resources/library")

kruskal <- function(input_data, values, types) {
    ## STAT ANALYSIS
    # Kruskal-Wallis Test
    # http://www.r-tutor.com/elementary-statistics/non-parametric-methods/kruskal-wallis-test
    d$Kruskal <- as.factor(types)
    kruskal.test(values ~ d$Kruskal, data=input_data)
}

kruskal_dunn <- function(input_data, values, types) {
  ## STAT ANALYSIS
  # Kruskal-Wallis Test
  # http://www.r-tutor.com/elementary-statistics/non-parametric-methods/kruskal-wallis-test
  d$Kruskal <- as.factor(types)
  # POSTHOC TESTS
  # adjustments to the p-value can be made. See ?p.adjust for allowed methods
  #
  # - Zar (2010) states that the Dunn test is appropriate for groups with unequal numbers of observations.
  # - Zar (2010) suggests that the Nemenyi test is not appropriate for groups with unequal numbers of observations.
  #
  # Zar, J.H. 2010. Biostatistical Analysis, 5th ed.  Pearson Prentice Hall: Upper Saddle River, NJ.
  #
  # DUNN
  # https://www.rdocumentation.org/packages/PMCMR/versions/4.1/topics/posthoc.kruskal.dunn.test
  posthoc.kruskal.dunn.test(values ~ d$Kruskal, data=input_data)
}

kruskal_nemenyi <- function(input_data, values, types) {
  ## STAT ANALYSIS
  # Kruskal-Wallis Test
  # http://www.r-tutor.com/elementary-statistics/non-parametric-methods/kruskal-wallis-test
  d$Kruskal <- as.factor(types)
  #
  # NEMENYI (not suitable for this dataset)
  # https://www.rdocumentation.org/packages/PMCMR/versions/4.1/topics/posthoc.kruskal.nemenyi.test
  posthoc.kruskal.nemenyi.test(values ~ d$Kruskal, data=input_data)
}