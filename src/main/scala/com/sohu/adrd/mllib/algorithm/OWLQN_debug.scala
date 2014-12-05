///**
// * User: hanchensu
// * Date: 2014-03-27
// * Time: 14:01
// */
//package com.sohu.adrd.mllib.algorithm
//
//import com.sohu.adrd.mllib.model.GeneralizedLinearModel
//import com.sohu.adrd.mllib.{SparseVector, Weights, LabeledInstance}
//import org.apache.spark.SparkContext._
//import org.apache.spark.rdd.RDD
//import scala.collection.mutable.Queue
//import scala.collection.mutable.Map
//
//
//abstract class OWLQN[M <: GeneralizedLinearModel](
//												   val l1RegParam: Double,
//												   val l2RegParam: Double,
//												   val memParam: Int,
//												   terminateCondition: Int => Boolean) extends IterativeAlgorithm[M] with Serializable {
//
//	//	def calGradient(instance:LabeledInstance, weights:Weights): Weights
//
//	//	def calLoss(instance:LabeledInstance, weights:Weights): Double
//
//	def calGradientLossInstance(instance: LabeledInstance, weights: Weights): (Weights, Double)
//
//	//	def pr(name:String,x:Weights,loss:String = "noloss") {
//	//		println(name+x.features.data)
//	//		println(name+x.intercept)
//	//		println(name+loss)
//	//		println()
//	//	}
//
//	def train(trainSet: RDD[LabeledInstance], initialWeights: Weights): M = {
//
//		var iter: Int = 0
//
//		val sList = new Queue[Weights]()
//		val yList = new Queue[Weights]()
//		val rhoList = new Queue[Double]()
//
//		var weights = initialWeights
//
//		var (gradient, loss) = calGradientLoss(trainSet, weights)
//
//
//		while (!terminateCondition(iter)) {
//
//			iter += 1
//
//			val pseudoGradient = calPseudoGradient(weights, gradient)
//			println("___________pseudoGradient norm2: " + pseudoGradient.norm2())
//			println("___________")
//
//			val descDirection = pseudoGradient.mul(-1)
//
//			val direction = calDirection(sList, yList, rhoList, descDirection)
//			fixDirection(direction, descDirection)
//
//			val (weightsNew, gradientNew, lossNew) = lineSearch(trainSet, weights, direction, pseudoGradient, loss, iter)
//
//			updateSYRho(sList, yList, rhoList, weights, weightsNew, gradient, gradientNew, memParam)
//
//			weights = weightsNew
//			gradient = gradientNew
//			loss = lossNew
//
//
//
//			println("___________loss: " + loss)
//			println("___________=========iter: " + iter + " complete\n")
//
//		}
//
//		createModel(weights)
//	}
//
//
//	def calGradientLoss(
//						 trainSet: RDD[LabeledInstance],
//						 weights: Weights): (Weights, Double) = {
//		//		val featureMap:Map[String,Double] = Map(trainSet.map {
//		//			x => calGradientLossInstance(x, weights)
//		//		}.map(x => {
//		//			val weights = x._1
//		//			val map = weights.features.data
//		//			map("intercept") = weights.intercept
//		//			map("loss") = x._2
//		//			map
//		//		}).flatMap(x => x).reduceByKey(_+_).filter(x => {
//		//			val key = x._1
//		//			if(key.equals("intercept") || key.equals("loss") || x._2 != 0.0) true
//		//			else false
//		//		}).collectAsMap().toSeq:_*)
//
//		val featureMap: Map[String, Double] = Map(trainSet.map(x => {
//			val (grad, loss) = calGradientLossInstance(x, weights)
//			val map = grad.features.data
//			map("intercept") = grad.intercept
//			map("loss") = loss
//			map
//		}).flatMap(x => x).reduceByKey(_ + _).filter(x => {
//			val key = x._1
//			if (key.equals("intercept") || key.equals("loss") || x._2 != 0.0) true
//			else false
//		}).collectAsMap().toSeq: _*)
//
//		val intercept = featureMap("intercept")
//		var loss = featureMap("loss")
//
//		featureMap -= "intercept"
//		featureMap -= "loss"
//
//		var gradient = Weights(SparseVector(featureMap), intercept)
//
//		gradient += weights.mul(l2RegParam)
//		loss += 0.5 * l2RegParam * (weights dot weights) + l1RegParam * weights.norm1()
//
//		(gradient, loss)
//
//	}
//
//	def calPseudoGradient(point: Weights, gradient: Weights): Weights = {
//		val pseudoGradient = Weights()
//		for (key <- gradient.feaSet union point.feaSet) {
//			//			println("wori")
//			val weight = point(key)
//			if (weight != 0) {
//				pseudoGradient(key) = gradient(key) + l1RegParam * math.signum(weight)
//			} else {
//				if (gradient(key) > l1RegParam) pseudoGradient(key) = gradient(key) - l1RegParam
//				else if (gradient(key) < -l1RegParam) pseudoGradient(key) = gradient(key) + l1RegParam
//				else pseudoGradient(key) = 0
//			}
//		}
//		val intercept = point.intercept
//		if (intercept != 0) {
//			pseudoGradient.intercept = gradient.intercept + l1RegParam * math.signum(intercept)
//		} else {
//			if (gradient.intercept > l1RegParam) pseudoGradient.intercept = gradient.intercept - l1RegParam
//			else if (gradient.intercept < -l1RegParam) pseudoGradient.intercept = gradient.intercept + l1RegParam
//			else pseudoGradient.intercept = 0
//		}
//
//		pseudoGradient
//
//	}
//
//	def calDirection(
//					  sList: Queue[Weights],
//					  yList: Queue[Weights],
//					  rhoList: Queue[Double],
//					  descDirection: Weights): Weights = {
//
//		val listSize = sList.size
//		val alphas = new Array[Double](listSize)
//		var direction = descDirection
//
//		if (listSize > 0) {
//			for (i <- listSize - 1 to 0 by -1) {
//				alphas(i) = (sList(i) dot direction) / rhoList(i)
//				direction = direction - yList(i).mul(alphas(i))
//			}
//			val lastY = yList(listSize - 1)
//
//			val gama = rhoList(listSize - 1) / (lastY dot lastY)
//			direction = direction.mul(gama)
//
//			for (i <- 0 to listSize - 1) {
//				val beta = (yList(i) dot direction) / rhoList(i)
//				direction += sList(i).mul(alphas(i) - beta)
//			}
//		}
//		direction
//	}
//
//
//	def fixDirection(direction: Weights, gradientDesc: Weights) {
//		if (l1RegParam > 0) {
//			for (key <- direction.feaSet) {
//				if (direction(key) * gradientDesc(key) <= 0) {
//					direction(key) = 0
//				}
//			}
//			if (direction.intercept * gradientDesc.intercept <= 0) {
//				direction.intercept = 0
//			}
//		}
//	}
//
//
//	def calLineSearchParam(direction: Weights, loss: Double, iter: Int): (Double, Double, Double, Double) = {
//		var (alpha, rho, c1, c2) = (1.0, 0.5, 1e-4, 0.0)
//		if (iter == 1) {
//			val norm = direction.norm2()
//			alpha = 1.0 / norm
//			rho = 0.1
//		}
//		(alpha, rho, c1, c2)
//	}
//
//	def lineSearch(
//					trainSet: RDD[(LabeledInstance)],
//					point: Weights,
//					direction: Weights,
//					gradient: Weights,
//					loss: Double,
//					iter: Int): (Weights, Weights, Double) = {
//
//		def constrainedSearch(
//							   point: Weights,
//							   direction: Weights,
//							   alpha: Double): Weights = {
//
//			val pointNew = point + direction.mul(alpha)
//
//			if (l1RegParam > 0) {
//				val feaInterSect = point.feaSet & pointNew.feaSet
//				for (key <- feaInterSect) {
//					if (point(key) * pointNew(key) < 0) pointNew(key) = 0
//				}
//				if (point.intercept * pointNew.intercept < 0) pointNew.intercept = 0
//			}
//			pointNew
//		}
//
//		var (alpha, rho, c1, c2) = calLineSearchParam(direction, loss, iter)
//
//		var pointNew = Weights()
//		var lossNew = 0.0
//		var gradientNew = Weights()
//
//		val dDotG = direction dot gradient
//
//		println("___________dDotG: " + dDotG)
//
//		var innerIter = 0
//		do {
//
//			innerIter += 1
//
//			pointNew = constrainedSearch(point, direction, alpha)
//
//			val pair = calGradientLoss(trainSet, pointNew)
//			gradientNew = pair._1
//			lossNew = pair._2
//
//			alpha = alpha * rho
//			println("	___________innerIter: " + innerIter)
//			val pseudoGradient = calPseudoGradient(pointNew, gradientNew)
//			println("___________pseudoGradient norm2: " + pseudoGradient.norm2())
//			println("	___________loss: " + lossNew)
//		} while (!(lossNew <= loss + alpha * c1 * dDotG))
//
//		println("___________innerIterNum: " + innerIter)
//
//		(pointNew, gradientNew, lossNew)
//	}
//
//
//	def updateSYRho(
//					 sList: Queue[Weights],
//					 yList: Queue[Weights],
//					 rhoList: Queue[Double],
//					 weight: Weights,
//					 weightNew: Weights,
//					 gradient: Weights,
//					 gradientNew: Weights,
//					 m: Int) {
//
//		val listSize = sList.length
//
//		if (listSize == m) {
//			sList.dequeue()
//			yList.dequeue()
//			rhoList.dequeue()
//		}
//
//		val s = weightNew - weight
//		val y = gradientNew - gradient
//		val rho = s dot y
//		sList.enqueue(s)
//		yList.enqueue(y)
//	}
//
//	def createModel(weights: Weights): M
//}
