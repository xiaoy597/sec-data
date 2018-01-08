package io.jacob.etl.sec

import java.io._
import java.nio.file.Paths
import java.util.zip.{ZipFile, ZipInputStream}

import io.jacob.dbf.DBFUtil

/**
  * Created by xiaoy on 2018/1/8.
  */
object SDataLoader {

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

  def loadSZQuote(dataFile: String) = {

    val zipFile = new ZipFile(dataFile)
    val zipInputStream = new ZipInputStream(new FileInputStream(dataFile))
    var entry = zipInputStream.getNextEntry

    while (entry != null) {
      printf("Found entry %s\n", entry.getName)

      val inputStream = new BufferedInputStream(zipFile.getInputStream(entry))

      val outputStream = new ByteArrayOutputStream()
      //      val outputStream = new FileOutputStream(new File(dataFile + "_unl.txt"))

      DBFUtil.dbfConvert(inputStream, outputStream)


      //      val outFile = new File(entry.getName)
      //      if (!outFile.getParentFile.exists())
      //        outFile.getParentFile.mkdirs()
      //
      //      if (!outFile.exists())
      //        outFile.createNewFile()
      //
      //      val outputStream = new FileOutputStream(outFile)
      //      var ch: Int = inputStream.read()
      //      while (ch != -1) {
      //        outputStream.write(ch)
      //        ch = inputStream.read()
      //      }
      //

      val reader = new BufferedReader(
                    new InputStreamReader(
                      new ByteArrayInputStream(outputStream.toByteArray)))

      var line = reader.readLine()
      var count = 0
      while(line != null && count < 100) {
        println(line)
        line = reader.readLine()
        count = count+1
      }

      inputStream.close()
      outputStream.close()

      entry = zipInputStream.getNextEntry
    }

    zipInputStream.close()
  }

  def main(args: Array[String]): Unit = {
    for (path <- args)
      loadData(path)
  }
}
