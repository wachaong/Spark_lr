/**
 * User: hanchensu
 * Date: 2014-03-26
 * Time: 20:28
 */
package com.sohu.adrd.mllib.algorithm

import org.apache.spark.Logging
import com.sohu.adrd.mllib.model.GeneralizedLinearModel
import org.apache.spark.rdd.RDD
import com.sohu.adrd.mllib.{Weights, LabeledInstance}


abstract class GeneralizedLinearAlgorithm[M <: GeneralizedLinearModel] extends Logging with Serializable {
}

abstract class IterativeAlgorithm[M <: GeneralizedLinearModel] extends GeneralizedLinearAlgorithm[M] with Serializable{

	def train(trainSet: RDD[LabeledInstance], initialWeights:Weights): M

}




