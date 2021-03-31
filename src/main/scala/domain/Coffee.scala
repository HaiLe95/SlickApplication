package com.haile.app
package domain

case class Coffee(name: String,
                  supId: Int,
                  price: Double,
                  sales: Int,
                  total: Int) {

  override def toString: String = {
    s"$name : {$supId, $price, $sales, $total}"
  }
}