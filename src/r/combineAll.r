### will concatenate swc reconstructions into one file: combineAll.swc
### will do reindexing so that concatenation sequence is ok
### fixes radius at each point (but that can be commented out)

rm(list = ls())

# set working directory to current source directory
this.dir <- dirname(parent.frame(2)$ofile)
setwd(this.dir)

print (paste("current dir: ", this.dir))

# totla files to combine
nrFiles = 34 #34

# storage for all
datAll <- c()

columnNames<-c("1","2","3","4","5","6","7")

# counter to correcto for indexing
countIdx = 0

# generate filenames, pattern: NC_xx.swc
filenames <- array("", dim=c(1,nrFiles)) #initialize
for (i in 1:nrFiles ) {

  idx <- sprintf("%02d", i) # as.numeric("104")

  filenames[i] <- paste("NC_",idx,".swc", sep="")

  pathin = paste(this.dir,"/",filenames[i], sep="")



  #read and set 6th column to 2
  dat<-read.table(pathin, header=FALSE, col.names=columnNames, comment.char = "#")

  # fix radiuses (if they are zero)
  dat[,6]<-2

  # reindexing rows in dat
  for (k in 1:nrow(dat)) {
    if (k==1) {
      # first column change
      dat[k,1] = dat[k,1] + countIdx
    }
    else {
      # first and last column
      dat[k,1] = dat[k,1] + countIdx
      dat[k,7] = dat[k,7] + countIdx
    }
  }

  countIdx = countIdx + nrow(dat)

  # export modified dat
  pathout = paste(this.dir,"/","modified_",filenames[i], sep="")
  #write.table(dat, file = pathout, row.names = FALSE, col.names = FALSE)

  # append dat to the one that contains all
  datAll<-rbind(datAll, dat)

}

path = paste(this.dir,"/","combineAll.swc", sep="")
write.table(datAll, file = path, row.names = FALSE, col.names = FALSE)
print(sprintf("%s exported", path))