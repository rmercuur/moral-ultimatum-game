dat <- read.csv("./IndividualAll.2019.Aug.13.14_17_22.txt")
params <- read.csv("./IndividualAll.2019.Aug.13.14_17_22.batch_param_map.txt")
datParams <- merge(lastAction,valueDifference, by = c("run","tick","ID"))


complete <- read.csv("./Data For Luciano/IndividualResponseAndValueDifference.2019.Jul.03.10_24_48.txt")


dat2 <- read.csv("./Data For Luciano/IndividualAll.2019.Jul.24.17_27_42.txt")
