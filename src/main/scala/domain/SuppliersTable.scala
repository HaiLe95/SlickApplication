package com.haile.app
package domain

import slick.jdbc.H2Profile.api._

object SuppliersTable {

  class SupplierTable(tag: Tag) extends Table[(Int, String, String, String, String, String)](tag, "SUPPLIERS") {
    def id:     Rep[Int]    = column[Int]("SUP_ID", O.PrimaryKey)
    def name:   Rep[String] = column[String]("SUP_NAME")
    def street: Rep[String] = column[String]("STREET")
    def city:   Rep[String] = column[String]("CITY")
    def state:  Rep[String] = column[String]("STATE")
    def zip:    Rep[String] = column[String]("ZIP")
    // * - is projection every field from our class into SQL Table field
    override def * = (id, name, street, city, state, zip)
  }
  // something very important
  val suppliers = TableQuery[SupplierTable]

}