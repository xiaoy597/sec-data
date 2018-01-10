/**
  * Created by xiaoy on 2018/1/10.
  */
object Test1 {
  def main(args: Array[String]): Unit = {
    val s = "abc"
    s match {
      case "abc" => println("Letter")
      case "1" => println("Number")
      case s1:String => println("String")
      case _ => println("Others")
    }

    println("134-35234".split("-")(1))

    val l = List(1,2,3,4)
    println(l.slice(1,l.size-1))
  }
}
