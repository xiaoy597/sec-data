package io.jacob.etl.sec

/**
  * Created by xiaoy on 2018/1/10.
  */
class SZSecInfoSnapshot(val dataFilePath: String) extends SDataSnapshot{
  override def getUpdate: (List[String], String) = {
    (List(), "")
  }
}
