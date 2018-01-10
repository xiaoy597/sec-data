package io.jacob.etl.sec

import java.io._
import java.util.zip.{ZipFile, ZipInputStream}

import io.jacob.dbf.DBFUtil
import org.apache.commons.io.FilenameUtils

import scala.collection.mutable.ListBuffer

/**
  * Created by xiaoy on 2018/1/10.
  */
trait SDataSnapshot {
  def getUpdate: (List[String], String)

  def load(dataFilePath: String, tsfunc: String => String): (List[String], String) = {
    val zipFile = new ZipFile(dataFilePath)
    val zipInputStream = new ZipInputStream(new FileInputStream(dataFilePath))
    val entry = zipInputStream.getNextEntry

    val snapshot:ListBuffer[String] = ListBuffer()

    printf("Found entry %s\n", entry.getName)

    val inputStream = new BufferedInputStream(zipFile.getInputStream(entry))

    val reader = if (dataFilePath.contains("dbf")) {
      val outputStream = new ByteArrayOutputStream()

      DBFUtil.dbfConvert(inputStream, outputStream)

      new BufferedReader(
        new InputStreamReader(new ByteArrayInputStream(outputStream.toByteArray)))
    }else{
      new BufferedReader(new InputStreamReader(inputStream))
    }

    var quoteLine = reader.readLine()

    val ts = if (quoteLine != null){
      tsfunc(quoteLine)
    }else{
      ""
    }

    while (quoteLine != null) {
      quoteLine = reader.readLine()
      snapshot += quoteLine
    }

    inputStream.close()
    reader.close()

    (snapshot.toList, ts)
  }
}

object SDataSnapshot {
  def apply(dataFilePath: String): SDataSnapshot = {
    val dataFileName = FilenameUtils.getBaseName(dataFilePath)

    dataFileName.split("\\.")(0) match {
      case "mktdt00" => new SHQuoteSnapshot(dataFilePath)
      case "sjshq" => new SZQuoteSnapshot(dataFilePath)
      case "SJSXX" => new SZSecInfoSnapshot(dataFilePath)
      case "sjsxxn" => new SZNewSecInfoSnapshot(dataFilePath)
    }
  }
}