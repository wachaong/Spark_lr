/**
 * User: hanchensu
 * Date: 2014-03-28
 * Time: 11:26
 */
package com.sohu.adrd.mllib

import scala.collection.Set

case class Weights(var features: SparseVector, var intercept: Double) {

	def dot(other:Weights): Double = (features dot other.features) + intercept*other.intercept

	def norm1():Double = features.norm1() + math.abs(intercept)

	def norm2():Double = math.sqrt((features dot features) + intercept*intercept)

	def mul(multiplier:Double): Weights = {
		val res = this.copy()
		res.features = res.features.mul(multiplier)
		res.intercept *= multiplier
		res
	}

	def + (other:Weights): Weights = {
		val res = this.copy()
		res.features += other.features
		res.intercept += other.intercept
		res
	}

	def - (other:Weights): Weights = {
		val res = this.copy()
		res.features -= other.features
		res.intercept -= other.intercept
		res
	}

	def apply(key: String): Double = features(key)

	def update(key:String, value:Double) { features(key)=value}

	def feaSet: Set[String] = features.feaSet

	def pr(name:String,x:Weights,loss:String = "noloss") {
		println(name+x.features.data)
		println(name+x.intercept)
		println(name+loss)
		println()
	}

}

object Weights {
	def apply():Weights = Weights(SparseVector(),0.0)
}


