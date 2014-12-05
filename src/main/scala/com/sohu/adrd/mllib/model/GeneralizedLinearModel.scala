/**
 * User: hanchensu
 * Date: 2014-03-27
 * Time: 13:19
 */
package com.sohu.adrd.mllib.model

import com.sohu.adrd.mllib.{Weights, LabeledInstance, SparseVector}

abstract class GeneralizedLinearModel extends Serializable {

	var weights: Weights = Weights()

	def predict(instance: SparseVector): Double

//	def calLoss(labeledInstance: LabeledInstance): Double

//	def calGradient(labeledInstance: LabeledInstance): Double

//	def calGradient(labeledInstance: LabeledInstance): Double

}

