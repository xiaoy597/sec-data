package io.jacob.etl.sec

import java.io._
import java.nio.file.Paths
import java.util.zip.{ZipFile, ZipInputStream}

import io.jacob.dbf.DBFUtil

/**
  * Created by xiaoy on 2018/1/8.
  */
object SDataLoader {

  var workDir:String = _
  var shQuote: Map[String, String] = Map()
  var szQuote: Map[String, String] = Map()
  var szSecInfo: Map[String, String] = Map()
  var szNewSecInfo: Map[String, String] = Map()

  var szQuoteUpdated: List[String] = List()

  def loadData(dataPath: String): Unit = {
    val rootPath = new File(dataPath)
    for (f <- rootPath.list().filter(x => x.endsWith(".gz") || x.endsWith(".zip")).sorted) {
      println(f)
      if (f.startsWith("sjshq")) {
        loadSZQuote(Paths.get(dataPath, f).toString)
        return
      }
    }
  }

  def loadSZQuote(dataFile: String): Unit = {

    val zipFile = new ZipFile(dataFile)
    val zipInputStream = new ZipInputStream(new FileInputStream(dataFile))
    var entry = zipInputStream.getNextEntry

    while (entry != null) {
      printf("Found entry %s\n", entry.getName)

      val inputStream = new BufferedInputStream(zipFile.getInputStream(entry))

      val outputStream = new ByteArrayOutputStream()
      //      val outputStream = new FileOutputStream(new File(dataFile + "_unl.txt"))

      DBFUtil.dbfConvert(inputStream, outputStream)

      val reader = new BufferedReader(
        new InputStreamReader(
          new ByteArrayInputStream(outputStream.toByteArray)))

      var quoteLine = reader.readLine()
      szQuoteUpdated = List()

      while (quoteLine != null) {
        //        println(line)
        quoteLine = reader.readLine()
        updateQuote(quoteLine)
      }

      inputStream.close()
      outputStream.close()

      entry = zipInputStream.getNextEntry
    }

    zipInputStream.close()
  }

  def updateQuote(quote: String): Unit = {
    val fields = quote.split("\\|")

    if (szQuote.contains(fields(0))) {
      if (szQuote(fields(0)).equals(quote))
        return
    }

    szQuote = szQuote + (fields(0) -> quote)
    szQuoteUpdated = szQuoteUpdated :+ quote
  }

  def saveQuote(quotes: List[String], dest: String): Unit = {

  }

  def main(args: Array[String]): Unit = {
    workDir = args(0)

    for (i <- 1 until args.length)
      loadData(args(i))
  }
}
