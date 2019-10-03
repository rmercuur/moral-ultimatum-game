library(ggplot2)
library(reshape2)


datOffersNoParam <- read.csv("./IndividualAll.2019.Oct.03.15_06_49.txt")
paramOffers <- read.csv("./IndividualAll.2019.Oct.03.15_06_49.txt")
paramOffers <- paramOffers[,c("run","randomSeed","normAgentCount")]
datOffers <- merge(datOffersNoParam,paramOffers)
datOffersNew <- aggregate(LastRoundAverageDemand ~ normAgentCount, data= datOffers, mean)
datOffersNew$hDemLC <- dataRoundOneSummaryD$demand - dataRoundOneSummaryD$ci
datOffersNew$hDemHC <- dataRoundOneSummaryD$demand + dataRoundOneSummaryD$ci
myPlotOffer <- ggplot(datOffersNew, aes(normAgentCount, LastRoundAverageDemand)) +
  geom_point() +
  geom_line() +
  geom_ribbon(aes(ymin=hDemLC, ymax=hDemHC),alpha=0.2)+
  xlab("Amount of normative agents") + ylab("Average Demand")
plot(myPlotOffer)