library(dplyr)
dat <- read.csv("./IndividualAll.2019.Oct.14.18_08_35.txt")
params <- read.csv("./IndividualAll.2019.Oct.14.18_08_35.batch_param_map.txt")

datParams <- merge(dat,params, by = c("run"))
write.csv(datParams,"mergedData14Oct.csv")
