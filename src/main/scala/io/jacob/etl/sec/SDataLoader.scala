package io.jacob.etl.sec

import java.io._
import java.nio.file.Paths

import org.apache.commons.io.FilenameUtils

/**
  * Created by xiaoy on 2018/1/8.
  */
object SDataLoader {

  def loadData(dataPath: String): Unit = {
    // The dataPath should end with a date string like 20180108
    val workDate = FilenameUtils.getBaseName(dataPath)

    val rootPath = new File(dataPath)

    rootPath.list().
      filter(x => x.endsWith(".gz") || x.endsWith(".zip")).
      sorted.
      foreach(f => {
        println("Processing snapshot file %s".format(f))
        SDataSnapshot(Paths.get(dataPath, f).toString).getUpdate.save(workDate)
      })
  }


  def main(args: Array[String]): Unit = {
    SDataFileStore.storePath = args(0)

    for (i <- 1 until args.length)
      loadData(args(i))
  }
}
