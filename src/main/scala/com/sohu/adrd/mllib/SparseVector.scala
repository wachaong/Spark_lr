/**
 * User: hanchensu
 * Date: 2014-03-26
 * Time: 17:00
 */
package com.sohu.adrd.mllib
import scala.collection.mutable.Map
import scala.collection.Set

class SparseVector extends Serializable {

	val data: Map[String, Double] = Map()

	def this(data: Map[String, Double]) {
		this()
		this.data ++= data
	}

	def + (other:SparseVector): SparseVector = {
		val res = SparseVector()
		for(key <- data.keySet union other.data.keySet)  {
			res(key) = data.getOrElse(key,0.0) + other.data.getOrElse(key, 0.0)
		}
		res
	}

//	def + (other:SparseVector): SparseVector = {
//		val res = SparseVector()
//		val set:scala.collection.mutable.Set[String] = scala.collection.mutable.Set()
//		for(key <- data.keySet union other.data.keySet)  {
//			set += key
////			res.data(key) = data(key)
////			res.data(key) = 1.0
//		}
//		for(key <- set)
//			res.data(key) = 1.0
////		for(key <- other.data.keySet)  {
////			res.data(key) = other.data(key)
////			res.data(key) = 1.0
////		}
////		for(key <- other.data.keySet) {
//////			if(data.keySet.contains(key)) {
//////				res(key) += other.data(key)
//////			}
//////			else {
////				res(key) = other.data(key)
//////			}
//////			res(key) = res.data.getOrElse(key, 0.0) + other.data(key)
////		}
////		for(key <- data.keySet) res(key) = data.getOrElse(key,0.0)
//		res
//	}

	def - (other:SparseVector): SparseVector = {
		val res = SparseVector()
		for(key <- data.keySet union other.data.keySet) res(key) = data.getOrElse(key,0.0) - other.data.getOrElse(key,0.0)
		res
	}

	def dot(other:SparseVector): Double = {
/*	val interSect = this.data.keySet & other.data.keySet

 var sum: Double = 0.0
 for(key <- interSect) {
   sum += this.data(key) * other.data(key)
 }
 */
 var sum: Double = 0.0
 for(key <- this.data.keySet) {
   sum += this.data(key) * other.data.getOrElse(key,0.0)
 }
 sum
}

def mul(multiplier:Double):SparseVector = {
 val res = SparseVector()
 for(key <- data.keySet) res.data(key) = data(key) * multiplier
 res
}

def norm1():Double = if (this.data.isEmpty) 0.0 else this.data.values.reduce(math.abs(_)+math.abs(_))

def norm2():Double = math.sqrt(this dot this)

def feaSet: Set[String] = this.data.keySet

def apply(key: String): Double = this.data.get(key) match {
 case None => 0.0
 case Some(value) => value
}

def update(key:String, value:Double) {
 if(value == 0) this.data -= key
 else this.data(key) = value
}

//	def sum: Double = if (this.data.isEmpty) 0.0 else this.data.values.reduce(_ + _)

}

object SparseVector {

def apply():SparseVector = new SparseVector()
def apply(data: Map[String, Double]):SparseVector = new SparseVector(data)

def main(args: Array[String]) {
 //		val x:Map[String, Double] = Map("bcd"->2)
 //		val y:Map[String, Double] = Map("bcd"->3,"abc"->4)
 val a: SparseVector = SparseVector(Map())
 val b: SparseVector = new SparseVector(Map("bcd"->3,"abc"->4))
 //		x++=y
 val c = b.mul(2)
 for(item <- c.data) {
   print(item)
 }

 //		println(x.size)
 //		val c = b.mul(2)

 //		for(item <- c.data) {
 //			println(c.data.size)
 //		}
 //		for(item <- b.data) {
 //			println(item)
 //		}
 //		println(b)

 //		val a: Map[String,Double] = Map("abc"->1,"bcd"->2)
 //		val b: Map[String,Double] = Map("bcd"->3,"cde"->4)
 //



}
}
