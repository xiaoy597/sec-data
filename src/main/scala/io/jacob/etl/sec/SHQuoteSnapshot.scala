package io.jacob.etl.sec

import org.apache.commons.io.FilenameUtils

/**
  * Created by xiaoy on 2018/1/10.
  */
class SHQuoteSnapshot(val dataFilePath: String) extends SDataSnapshot {
  override def getUpdate: SDataSnapshot = {
    val snapshot = load(dataFilePath)

    updateTS = if (snapshot.nonEmpty) {
      snapshot.head.split("\\|")(6).split("-")(1)
    } else ""

    updateData = snapshot.slice(1, snapshot.size - 1).filter(x => {
      val id = x.split("\\|")(1)

      SHQuoteSnapshot.latestSnapshot.get(id) match {
        case Some(v) => if (x == v) false else {
          SHQuoteSnapshot.latestSnapshot += (id -> x)
          true
        }
        case None =>
          SHQuoteSnapshot.latestSnapshot += (id -> x)
          true
      }
    })

    this
  }

  override def save(date: String): Unit = {
    var quoteUpdate: List[String] = List()
    var idxUpdate: List[String] = List()

    updateData.foreach(r => {
      var fields = r.split("\\|").toList

      if (fields.head == "MD002" || fields.head == "MD003")
        fields = fields.slice(0, 31) ::: List("0.0", "0.0") ::: fields.slice(31, fields.size)

      if (fields.head == "MD001")
        idxUpdate = idxUpdate :+ (fields.slice(0, fields.size - 1) :+ updateTS).mkString("|")
      else
        quoteUpdate = quoteUpdate :+ (fields.slice(0, fields.size - 1) :+ updateTS).mkString("|")
    })

    val quoteDataStore =
      FilenameUtils.getBaseName(dataFilePath).split("\\.")(0) + "-quote-%s.txt".format(date)

    SDataFileStore(quoteDataStore).append(quoteUpdate)

    val idxDataStore =
      FilenameUtils.getBaseName(dataFilePath).split("\\.")(0) + "-idx-%s.txt".format(date)

    SDataFileStore(idxDataStore).append(idxUpdate)
  }

}

object SHQuoteSnapshot {
  var latestSnapshot: Map[String, String] = Map()
}