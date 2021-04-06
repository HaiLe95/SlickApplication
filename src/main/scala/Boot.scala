package com.haile.app

import domain.{Coffee, CoffeesTable, SuppliersTable}

import com.typesafe.scalalogging.Logger

import slick.jdbc.H2Profile.api._

import scala.concurrent.ExecutionContext.Implicits.global

object Boot {

  def main(args: Array[String]): Unit = {

    val logger    = Logger(classOf[Boot.type])
    logger.info("Logger is created")

    val database  = Database.forConfig("h2mem1")

    val coffees   = CoffeesTable.coffees
    val suppliers = SuppliersTable.suppliers
    logger.info("CoffeesTable and SuppliersTable is initialized")

    val setup = DBIO.seq(
      // Creating tables with all KEYS ready to work
      (suppliers.schema ++ coffees.schema).create,

      suppliers ++= Seq(
        (101, "Acme, Inc.",   "99 Market Street",       "Groundsville",   "Florida",    "95199"),
        (49,  "Kenya Gold",   "6 Rustmoore Street",     "Clownhills",     "Califorina", "95460"),
        (150, "Noire",        "55 Broadway Street",     "New York",       "New York",   "92002"),
        (601, "Candle Spire", "202 Silverfoot Street",  "Austin",         "Texas",      "98544"),
        (2,   "First Gen",    "27 Elite-Miko",          "Elite-English",  "FAQ",        "90002")
      ),
      // Equivalent SQL code:
      // insert into SUPPLIERS(SUP_ID, SUP_NAME, STREET, CITY, STATE, ZIP) values (?,?,?,?,?,?)

      // Insert some coffees (using JDBC's batch insert feature, if supported by the DB)
      coffees ++= Seq(
        Coffee("Colombian",         101, 7.99, 0, 0),
        Coffee("French_Roast",       49, 8.99, 0, 0),
        Coffee("Espresso",          150, 9.99, 0, 0),
        Coffee("Colombian_Decaf",   101, 8.99, 0, 0),
        Coffee("French_Roast_Decaf", 49, 9.99, 0, 0),
        Coffee("Pekoland",            2, 9.99, 0, 0),
        Coffee("Ahoy",                2, 9.99, 0, 0),
        Coffee("Good Morning MF",     2, 9.99, 0, 0)
      ),
      // Equivalent SQL code:
      // insert into COFFEES(COF_NAME, SUP_ID, PRICE, SALES, TOTAL) values (?,?,?,?,?)
      // But here we're putting not just a tuple but a whole class
    )
    val setupFuture = database.run(setup)
    logger.info("Supps down below")

    database.run(suppliers.result).map(_.foreach {
      case (id, name, street, city, state, zip) =>
        println(s"SUPPLIES: $id, $name, $street, $city, $state, $zip")
    })

    logger.info("suppliers query is done, successfully or not")


    // Why not let the database do the string conversion and concatenation?
    val q1 = for(c <- coffees)
      yield {
        LiteralColumn("  ") ++ c.name ++ "\t" ++ c.supId.asColumnOf[String] ++
        "\t" ++ c.price.asColumnOf[String] ++ "\t" ++ c.sales.asColumnOf[String] ++
        "\t" ++ c.total.asColumnOf[String]
      }

    // The first string constant needs to be lifted manually to a LiteralColumn
    // so that the proper ++ operator is found

    // Equivalent SQL code:
    // select '  ' || COF_NAME || '\t' || SUP_ID || '\t' || PRICE || '\t' SALES || '\t' TOTAL from COFFEES

    logger.info("STREAMING STARTS")

    database.stream(q1.result).foreach(println)

    logger.info("Done")

    database.close()

  }

}
