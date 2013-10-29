### takes swc file from the current folder
### extracts bifurcation points
### saves as a new $.bif.swc
### script has to be in the same dir as input swc

rm(list=ls())

fileName <- readline("SWC file? ")

# check if it exists
if (! file.exists(fileName)) {
  print(cat(fileName, "does not exist"))
  return
}

# read swc
columnNames<-c("1","2","3","4","5","6","7")
dat<-read.table(fileName, header=FALSE, col.names=columnNames, comment.char = "#")

# allocate output
datAll <- c()

# bifurcation counter
bifCount = 0

# extract locations with bifurcations
for (i in 1:nrow(dat) ) {

  # branch counter
  count = 0

  # check the rest to see whether it was referred twice at least
  for (j in 1:nrow(dat)) {
    if ( (j!=i) && (dat[j,7]==i) ) {
      count = count + 1
    }
  }

  if (count>=2) {
    #print(sprintf("bifurcation at idx. %d -> splits in %d ", i, count))
    bifRow <- dat[i,] # take the row and convert it

    bifCount = bifCount + 1

    bifRow[,1] <- bifCount
    bifRow[,7] <- -1
    bifRow[,2] <- 7             ## output point type
    bifRow[,6] <- bifRow[,6]*1  ## output point size
    #print(bifRow)
    datAll<-rbind(datAll, bifRow)
  }

}

print(sprintf("found %d bifurcations ", bifCount))

# export as fileName.bifs.swc
baseName = substr(basename(fileName), 1, nchar(basename(fileName)) - 4)
exportName <- sprintf("%s.bif.swc", baseName)
write.table(datAll, file = exportName, row.names = FALSE, col.names = FALSE)
print(sprintf("%s exported", exportName))
