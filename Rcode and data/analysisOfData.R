library(ggplot2)
library(reshape2)
library(dplyr)

datNoParams <- read.csv("./IndividualAll.2019.Oct.14.17_50_53.txt")
params <- read.csv("./IndividualAll.2019.Oct.14.17_50_53.batch_param_map.txt")
paramsSelected <- select(params,run,randomSeed,valueNormWeight)
datParams <- merge(datNoParams,params, by = c("run"))
datParamsSelected <- select(datParams,randomSeed,tick,valueNormWeight,demand)

datNoParams <- read.csv("./AverageAcceptLastRoundOneTime.2019.Oct.14.17_50_53.txt")
params <- read.csv("./AverageAcceptLastRoundOneTime.2019.Oct.14.17_50_53.batch_param_map.txt")
paramsSelected <- select(params,run,randomSeed,valueNormWeight)
datParams <- merge(datNoParams,params, by = c("run"))
datParamsSelected <- select(datParams,randomSeed,valueNormWeight,AverageAcceptLastRound)

group_by(datParamsSelected, valueNormWeight) %>% summarise(mean = mean(AverageAcceptLastRound))
datAccept <- aggregate(valueNormWeight ~ AverageAcceptLastRound, data= datParamsSelected, mean)

datOffersNew$hDemLC <- dataRoundOneSummaryD$demand - dataRoundOneSummaryD$ci
datOffersNew$hDemHC <- dataRoundOneSummaryD$demand + dataRoundOneSummaryD$ci
myPlotOffer <- ggplot(datOffersNew, aes(normAgentCount, LastRoundAverageDemand)) +
  geom_point() +
  geom_line() +
  geom_ribbon(aes(ymin=hDemLC, ymax=hDemHC),alpha=0.2)+
  xlab("Amount of normative agents") + ylab("Average Demand")
plot(myPlotOffer)