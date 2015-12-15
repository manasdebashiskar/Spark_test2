package ttt

/**
 * Created by hadoop on 12/15/15.
 */
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
object test1 {
    def main(args: Array[String]) {
      val conf = new SparkConf()
      conf.setAppName("mytest")
        .setMaster("spark://Master:7077")
        .setSparkHome("/usr/local/spark")
        .setJars(Array("/home/hadoop/spark-assembly-1.4.0-hadoop2.4.0.jar"))
//spark-assembly-1.4.0-hadoop2.4.0.jar is from /usr/local/spark/lib
      val sc = new SparkContext(conf)
      val rawData = sc.parallelize(Seq(1,2,3))
      println(rawData.collect().length)
      sc.stop()
    }
}
