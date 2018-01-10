package io.jacob.etl.sec

import java.io.{File, FileWriter}
import java.nio.file.Paths

/**
  * Created by xiaoy on 2018/1/10.
  */
class SDataFileStore(storeName: String) extends SDataStore(storeName) {
  val storeFile = new File(Paths.get(SDataFileStore.storePath, storeName).toString)

  if (!storeFile.getParentFile.exists())
    storeFile.getParentFile.mkdirs()

  if (storeFile.exists())
    storeFile.delete

  println("Creating %s".format(storeFile.getAbsolutePath))
  storeFile.createNewFile()

  override def append(data: List[String]): Unit = {

    val writer = new FileWriter(storeFile, true)

    data.map(_ + "\n").foreach(writer.write)

    writer.close()
  }
}

object SDataFileStore {
  var storePath: String = _
  var storeDict: Map[String, SDataFileStore] = Map()

  def apply(storeName: String): SDataFileStore = {
    storeDict.get(storeName) match {
      case Some(x) => x
      case None =>
        val newStore = new SDataFileStore(storeName)
        storeDict += (storeName -> newStore)
        newStore
    }
  }
}