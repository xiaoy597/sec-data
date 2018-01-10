package io.jacob.etl.sec

import java.io._
import java.util.zip.{GZIPInputStream, ZipFile, ZipInputStream}

import io.jacob.dbf.DBFUtil
import org.apache.commons.io.FilenameUtils

import scala.collection.mutable.ListBuffer

/**
  * Created by xiaoy on 2018/1/10.
  */
trait SDataSnapshot {
  var updateData: List[String] = List()
  var updateTS: String = _

  def getUpdate: SDataSnapshot
  def save(date: String): Unit

  def load(dataFilePath: String):List[String] = {

    val snapshot: ListBuffer[String] = ListBuffer()

    val reader = {
      if (dataFilePath.endsWith("zip")) {
        val zipFile = new ZipFile(dataFilePath)
        val zipInputStream = new ZipInputStream(new FileInputStream(dataFilePath))
        val entry = zipInputStream.getNextEntry

        printf("Found entry %s\n", entry.getName)

        val inputStream = new BufferedInputStream(zipFile.getInputStream(entry))

        if (dataFilePath.contains("dbf")) {
          val outputStream = new ByteArrayOutputStream()

          DBFUtil.dbfConvert(inputStream, outputStream)

          inputStream.close()
          zipInputStream.close()

          new BufferedReader(
            new InputStreamReader(new ByteArrayInputStream(outputStream.toByteArray)))
        } else {
          new BufferedReader(new InputStreamReader(inputStream))
        }
      } else {
        new BufferedReader(new InputStreamReader(
          new GZIPInputStream(new FileInputStream(dataFilePath)), "GBK"))
      }
    }

    var dataLine = reader.readLine()

    while (dataLine != null) {
      snapshot += dataLine.replaceAll(" ", "")
      dataLine = reader.readLine()
    }

    reader.close()

    snapshot.toList
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