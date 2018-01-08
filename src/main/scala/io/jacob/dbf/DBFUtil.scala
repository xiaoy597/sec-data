package io.jacob.dbf

import java.io._
import java.util.Date

import com.linuxense.javadbf.DBFReader

import scala.collection.mutable.ArrayBuffer

/**
  * Created by xiaoy on 2018/1/8.
  */
object DBFUtil {

  def dbfConvert(input : InputStream, output: OutputStream):Unit = {
    val writer = new OutputStreamWriter(output)

    try {
      val reader = new DBFReader(input)
      reader.setCharactersetName("GBK")

      val fieldsCount = reader.getFieldCount

      //      for (i <- 0 until fieldsCount) {
      //        val field = reader.getField(i)
      //        System.out.println(field.getName, field.getFieldLength)
      //      }

      var rowValues = reader.nextRecord()
      var count = 0
      val buffer = ArrayBuffer[String]()

      while (rowValues != null) {
        buffer.clear()
        for (i <- 0 until fieldsCount) {
          buffer += rowValues(i).toString
        }
        writer.write(buffer.mkString("|"))
        writer.write("\n")
        rowValues = reader.nextRecord()
        count = count + 1
      }

    } catch {
      case e: Exception => e.printStackTrace()
    } finally {
      try {
        writer.flush()
      } catch {
        case e: Exception => e.printStackTrace()
      }
    }

  }

  def dbfConvert(dbfFile: String, txtFile: String): Unit = {
    val input = new FileInputStream(dbfFile)
    val output = new FileOutputStream(new File(txtFile))

    dbfConvert(input, output)

    input.close()
    output.close()
  }

  def main(args: Array[String]): Unit = {
    println("Hello, world!")
    val startTime = new Date().getTime
    dbfConvert(args(0), args(0) + "_unl.txt")
    val elapsed: Double = (new Date().getTime - startTime) / 1000.0
    println(s"Elapsed time: $elapsed")
  }

}

