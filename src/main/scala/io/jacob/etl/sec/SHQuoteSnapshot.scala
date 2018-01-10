package io.jacob.etl.sec

/**
  * Created by xiaoy on 2018/1/10.
  */
class SHQuoteSnapshot(val dataFilePath: String)  extends SDataSnapshot{
  override def getUpdate: (List[String], String) = {
    val (snapshot, ts) = load(dataFilePath, line => line.split("\\|")(6).split("-")(1))

    (snapshot.filter(x => {
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
    }), ts)
  }

}

object SHQuoteSnapshot {
  var latestSnapshot : Map[String, String] = Map()
}