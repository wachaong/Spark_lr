/**
 * User: hanchensu
 * Date: 2014-03-26
 * Time: 19:36
 */
package com.sohu.adrd.mllib.regression

import com.sohu.adrd.mllib.{Weights, LabeledInstance, SparseVector}
import scala.math
import com.sohu.adrd.mllib.model.GeneralizedLinearModel
import com.sohu.adrd.mllib.algorithm.OWLQN
import org.apache.spark.rdd.RDD
import scala.collection.mutable.Queue

class LogisticRegressionModel extends GeneralizedLinearModel with Serializable {

	def this(weights: Weights) {
		this()
		this.weights = weights
	}

	override def predict(instance: SparseVector): Double = {
		val margin = (instance dot weights.features) + weights.intercept
		1.0 / (1.0 + math.exp(margin * -1))
	}

}


object LogisticRegression {
//	def calGradient(labeledInstance: LabeledInstance, weights: Weights): Weights = Weights()
//	def calLoss(labeledInstance: LabeledInstance, weights: Weights): Double = 0.0

	def calGradientLossInstance(labeledInstance: LabeledInstance, weights: Weights):(Weights, Double) = {
		val label = labeledInstance.label
		val instance = labeledInstance.features

		val score = label * (instance.dot(weights.features) + weights.intercept)

		val (loss:Double,insProb:Double) =
			if (score < -30) {
				(-score,0.0)
			} else if(score>30) {
				(0.0,1.0)
			} else {
				val tmp=1+math.exp(-score)
				(math.log(tmp),1.0/tmp)
			}

		val mult = label *(insProb - 1)

		val gradient = Weights(instance.mul(mult), mult)

		(gradient,loss)

	}
}


class LogisticRegressionWithOWLQN (
  override val l1RegParam: Double,
  override val l2RegParam: Double,
  override val memParam: Int,
  terminateCondition: Int => Boolean) extends OWLQN[LogisticRegressionModel](l1RegParam, l2RegParam, memParam, terminateCondition) with Serializable {

//	override def calGradient(instance: LabeledInstance, weights: Weights) = LogisticRegression.calGradient(instance, weights)
//	override def calLoss(instance: LabeledInstance, weights: Weights) = LogisticRegression.calLoss(instance, weights)

	override def calGradientLossInstance(instance: LabeledInstance, weights: Weights) = LogisticRegression.calGradientLossInstance(instance, weights)

	override def createModel(weights: Weights) = {
		new LogisticRegressionModel(weights)
	}
}

class LogisticRegressionTest (
			val l1RegParam: Double,
			val l2RegParam: Double,
			val memParam: Int,
			terminateCondition: Int => Boolean) {

	def train() = {

		var iter: Int = 0

		while(!terminateCondition(iter)) {

			iter += 1
			println("====iter: "+iter)
		}

	}

}
