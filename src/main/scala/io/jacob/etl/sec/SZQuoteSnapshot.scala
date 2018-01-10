package io.jacob.etl.sec

/**
  * Created by xiaoy on 2018/1/10.
  */
class SZQuoteSnapshot(val dataFilePath: String) extends SDataSnapshot {

  override def getUpdate: (List[String], String) = {
    val (snapshot, ts) = load(dataFilePath, line => line.split("\\|")(7))

    (snapshot.filter(x => {
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
    }), ts)
  }
}

object SZQuoteSnapshot {
  var latestSnapshot : Map[String, String] = Map()
}