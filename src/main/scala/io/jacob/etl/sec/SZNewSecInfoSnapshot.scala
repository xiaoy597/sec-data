package io.jacob.etl.sec

/**
  * Created by xiaoy on 2018/1/10.
  */
class SZNewSecInfoSnapshot(val dataFilePath: String) extends SDataSnapshot{
  override def getUpdate: SDataSnapshot = {
    this
  }

  override def save(date:String): Unit = {

  }
}
