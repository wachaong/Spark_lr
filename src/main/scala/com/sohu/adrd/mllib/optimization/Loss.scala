/**
 * User: hanchensu
 * Date: 2014-03-26
 * Time: 20:56
 */
package com.sohu.adrd.mllib.optimization

import com.sohu.adrd.mllib.SparseVector

abstract class Loss extends Serializable {
	def compute(data: SparseVector, label: Double, weights: SparseVector):
	Double
}


