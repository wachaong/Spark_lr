package com.sohu.adrd.mllib.leastsquare

/**
 * Created by liuyq on 2014/5/12.
 */

import com.sohu.adrd.mllib.{Weights, LabeledInstance, SparseVector}
import scala.math
import com.sohu.adrd.mllib.model.GeneralizedLinearModel
import com.sohu.adrd.mllib.algorithm.OWLQN
import org.apache.spark.rdd.RDD
import scala.collection.mutable.Queue

class LeastSquareModel extends GeneralizedLinearModel with Serializable {

  def this(weights: Weights) {
    this()
    this.weights = weights
  }

  override def predict(instance: SparseVector): Double = {
    val margin = (instance dot weights.features) + weights.intercept
    1.0 / (1.0 + math.exp(margin * -1))
  }

}


object LeastSquare {

  def calGradientLossInstance(labeledInstance: LabeledInstance, weights: Weights):(Weights, Double) = {
    val label = labeledInstance.label
    val instance = labeledInstance.features

    val score_sqrt = label - instance.dot(weights.features)
    val loss = score_sqrt *score_sqrt


    val mult = -2.0*score_sqrt

    val gradient = Weights(instance.mul(mult), 0.0)

    (gradient,loss)

  }
}


class LeastSquareWithOWLQN (
                                    override val l1RegParam: Double,
                                    override val l2RegParam: Double,
                                    override val memParam: Int,
                                    terminateCondition: Int => Boolean) extends OWLQN[LeastSquareModel](l1RegParam, l2RegParam, memParam, terminateCondition) with Serializable {

  //	override def calGradient(instance: LabeledInstance, weights: Weights) = LogisticRegression.calGradient(instance, weights)
  //	override def calLoss(instance: LabeledInstance, weights: Weights) = LogisticRegression.calLoss(instance, weights)

  override def calGradientLossInstance(instance: LabeledInstance, weights: Weights) = LeastSquare.calGradientLossInstance(instance, weights)

  override def createModel(weights: Weights) = {
    new LeastSquareModel(weights)
  }
}

class LeastSquareTest (
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

