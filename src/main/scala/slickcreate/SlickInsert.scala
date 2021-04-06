package com.haile.app
package slickcreate

import slickselect.Messages
import slick.jdbc.H2Profile.api._

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

object SlickInsert {

  val database = Database.forConfig("h2mem1")
  val messages = Messages.messages

  def execute[T](action: DBIO[T]): T = {
    Await.result(database.run(action), 4.seconds)
  }

  def main(args: Array[String]): Unit = {

    /** Inserting a rows
     *  A single one
     *
     */

    val insertAction =
      messages += Messages.Message("HAL", "No, seriously, Dave, I can't let you in.")
    execute(insertAction)
    insertAction.statements.head
    // res2: String = "insert into \"message\" (\"sender\",\"content\") values (?,?)"

    Messages.Message("Dave", "You're off my Christmas card list.")
    // res3: Message = Message("Dave", "You're off my Christmas card list.", 0L)


  }
}
