/**
 * User: hanchensu
 * Date: 2014-03-26
 * Time: 20:54
 */
package com.sohu.adrd.mllib.optimization

import com.sohu.adrd.mllib.SparseVector

abstract class Gradient extends Serializable {

	def compute(data: SparseVector, label: Double, weights: SparseVector):
	SparseVector
}

