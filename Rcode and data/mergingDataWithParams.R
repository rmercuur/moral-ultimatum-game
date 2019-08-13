dat <- read.csv("./IndividualAll.2019.Aug.13.14_32_15.txt")
params <- read.csv("./IndividualAll.2019.Aug.13.14_32_15.batch_param_map.txt")
datParams <- merge(dat,params, by = c("run"))
