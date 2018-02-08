package io.jacob.etl.sec

import java.util.Properties

import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}

/**
  * Created by xiaoy on 2018/2/8.
  */
class SDataKafkaStore(storeName: String) extends SDataStore(storeName) {
  override def append(data: List[String]): Unit = {
    data.foreach(x => {
      SDataKafkaStore.producer.send(new ProducerRecord[String, String](storeName, null, x))
    })
  }
}

object SDataKafkaStore {
  var serverList: String = _
  var producer: KafkaProducer[String, String] = _
  var storeDict: Map[String, SDataKafkaStore] = Map()

  def apply(storeName: String): SDataKafkaStore = {
    if (producer == null) {
      val props = new Properties()

      props.put("bootstrap.servers", serverList)
      props.put("acks", "1")
      props.put("retries", "3")
      props.put("batch.size", "16384")
      props.put("linger.ms", "1")
      props.put("buffer.memory", "33554432")
      props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
      props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")
      props.put("compression-codec", "snappy")

      producer = new KafkaProducer[String, String](props)
    }

    storeDict.get(storeName) match {
      case Some(x) => x
      case None =>
        val newStore = new SDataKafkaStore(storeName)
        storeDict += (storeName -> newStore)
        newStore
    }
  }
}