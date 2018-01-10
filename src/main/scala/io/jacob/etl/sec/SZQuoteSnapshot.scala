package io.jacob.etl.sec

/**
  * Created by xiaoy on 2018/1/10.
  */
class SZQuoteSnapshot(val dataFilePath: String) extends SDataSnapshot {

  override def getUpdate: SDataSnapshot = {
    val snapshot = load(dataFilePath)

    val ts = if (snapshot.nonEmpty)
      snapshot.head.split("\\|")(7)
    else ""

    updateData = snapshot.slice(1, snapshot.size - 1).filter(x => {
      val id = x.split("\\|")(0)

      SZQuoteSnapshot.latestSnapshot.get(id) match {
        case Some(v) => if (x == v) false else {
          SZQuoteSnapshot.latestSnapshot += (id -> x)
          true
        }
        case None =>
          SZQuoteSnapshot.latestSnapshot += (id -> x)
          true
      }
    })

    this
  }

  override def save(date:String): Unit = {

  }
}

object SZQuoteSnapshot {
  var latestSnapshot: Map[String, String] = Map()
}