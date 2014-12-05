/**
 * User: hanchensu
 * Date: 2014-04-09
 * Time: 14:53
 */
package com.sohu.adrd.mllib
import scala.collection.mutable.Map
import org.apache.hadoop.io.Text

object Util {
        def line2LabeledInstance1(line:String, labelSep:String = "\\p{Blank}+", labelIdx:Int = 0, fieldSep:String = "\\p{Blank}+", kvSep:String = ":", negLabel:String="-1"): LabeledInstance = {
                val labelContent = line.split(labelSep,3)
                var label = labelContent(labelIdx)
    if("0".equals(label) || "0.0".equals(label)) label = negLabel

    val content = labelContent(2)
                val features = SparseVector()
                for(field <- content.split(fieldSep)) {
                        val kv = field.split(kvSep)
                        features(kv(0)) = kv(1).toDouble
                }

                LabeledInstance(label.toDouble, features)
        }	

//	def line2LabeledInstance2(line:String, labelSep:String = "\\p{Blank}+", labelIdx:Int = 0, fieldSep:String = "\\p{Blank}+", kvSep:String = ":", negLabel:String="-1"): LabeledInstance = {
//		val labelContent = line.split(labelSep,2)
//		var label = labelContent(labelIdx)
//		if("0".equals(label)) label = negLabel
//
//		val content = labelContent(1-labelIdx)
//		val features = SparseVector()
//		for(field <- content.split(fieldSep)) {
//			val kv = field.split(kvSep)
//			features(kv(0)+"&"+kv(1)) = 1.0
//		}
//
//		LabeledInstance(label.toDouble, features)
//	}


		def line2LabeledInstance2(labelConent:(Text,Text), fieldSep:String = "\\p{Blank}+", kvSep:String = ":", negLabel:String="-1"): LabeledInstance = {
			var label = labelConent._1.toString

			if("0".equals(label)) label = negLabel

			val content = labelConent._2.toString

			val features = SparseVector()
			for(field <- content.split(fieldSep)) {
				if(field.length > 0) {
					val kv = field.split(kvSep)
					if(kv.length == 2) {
						features(kv(0) + "&" + kv(1)) = 1.0
					}
				}
			}

			LabeledInstance(label.toDouble, features)
		}

  def line2LabeledInstance3(line:String, labelSep:String = "\\p{Blank}+", labelIdx:Int = 0, fieldSep:String = ",", kvSep:String = ":", negLabel:String="-1"): LabeledInstance = {
    val labelContent = line.split(labelSep,2)
    val label = labelContent(labelIdx)

    val content = labelContent(1-labelIdx)
    val features = SparseVector()
    for(field <- content.split(fieldSep)) {
      val kv = field.split(kvSep)
      features(kv(0)) = kv(1).toDouble
    }

    println(label)
    println(content)
    LabeledInstance(label.toDouble, features)
  }


}
