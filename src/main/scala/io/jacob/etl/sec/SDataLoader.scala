package io.jacob.etl.sec

import java.io._
import java.nio.file.Paths

import org.apache.commons.cli.{DefaultParser, Options}
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
    val parser = new DefaultParser

    import org.apache.commons.cli.Option

    val fsOption = Option.builder("d").longOpt("directory").hasArg.build()
    val ksOption = Option.builder("k").longOpt("kafka-server-list").hasArg.build()

    val options = new Options()

    options.addOption(fsOption)
    options.addOption(ksOption)

    var argsLeft: Array[String] = null

    try {
      val commandLine = parser.parse(options, args)

      if (commandLine.hasOption("d"))
        SDataFileStore.storePath = commandLine.getOptionValue("d")
      if (commandLine.hasOption("k"))
        SDataKafkaStore.serverList = commandLine.getOptionValue("k")

      argsLeft = commandLine.getArgs
    } catch {
      case e: Exception =>
        e.printStackTrace()
        sys.exit(1)
    }

    if (SDataFileStore.storePath != null)
      println("file save directory is %s".format(SDataFileStore.storePath))
    if (SDataKafkaStore.serverList != null)
      println("kafka server list is %s".format(SDataKafkaStore.serverList))

    println("arguments is %s".format(argsLeft.mkString(",")))

    argsLeft.foreach( dir => {
      loadData(dir)
    })
  }
}
