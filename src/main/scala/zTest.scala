import com.sohu.adrd.mllib.regression.{LogisticRegression, LogisticRegressionTest, LogisticRegressionWithOWLQN}
import com.sohu.adrd.mllib.{Weights, Util}
import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import scala.collection.mutable.Map
import org.apache.spark.SparkConf

import scala.collection.immutable.SortedMap

import org.apache.log4j._

/**
 * User: hanchensu
 * Date: 2014-04-09
 * Time: 14:52
 */
object zTest {
	def main(args: Array[String]) {
	        val conf = new SparkConf().setAppName("zTest")
        	val sc = new SparkContext(conf)

		val input = args(0)
		val out = args(1)
		val iterNum = args(2).toInt
		val rawText = sc.textFile(input)
		val trainSet = rawText.map(Util.line2LabeledInstance1(_))
		trainSet.cache()

		def terminate(iter: Int):Boolean = {
			if(iter > iterNum) true
			else false
		}

		val initWeights:Weights = Weights()

		val lrtrain = new LogisticRegressionWithOWLQN(2.0, 0, 10, terminate)

		val model = lrtrain.train(trainSet, initWeights)
	
		var map = SortedMap[Int,Double]()

		model.weights.features.data.map{
			case(k,v)=>map += k.toInt -> v
        	}
	        val arr = map.toArray
        	val t = sc.parallelize(arr,8)
	        t.saveAsTextFile(args(1))	
	}
}
