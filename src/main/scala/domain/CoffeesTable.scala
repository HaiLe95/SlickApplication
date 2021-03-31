package com.haile.app
package domain

import slick.jdbc.H2Profile.api._

object CoffeesTable {

  class CoffeeTable(tag: Tag) extends Table[Coffee](tag, None, "COFFEES") {
    def name:   Rep[String] = column[String]("COF_NAME", O.PrimaryKey)
    def supId:  Rep[Int]    = column[Int]("SUP_ID")
    def price:  Rep[Double] = column[Double]("PRICE")
    def sales:  Rep[Int]    = column[Int]("SALES")
    def total:  Rep[Int]    = column[Int]("TOTAL")

    //  In case that we're mapping the Table with the Coffee class, we have to make sure that function *() works fine
    //  That is why we "maps" our tuple to the Coffee class that is decomposed it into fields
    override def * = (name, supId, price, sales, total) <>  (Coffee.tupled, Coffee.unapply)

    //  Here we're making a foreign key field that is mapped to supId and TableQuery from SuppliersTable, for each id
    def supplier = foreignKey("SUP_FOREIGN_KEY", supId, SuppliersTable.suppliers)(_.id)
  }

  val coffees = TableQuery[CoffeeTable]

}