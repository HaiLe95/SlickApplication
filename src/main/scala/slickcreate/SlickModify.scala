package com.haile.app
package slickcreate

import slickselect.Messages
import slick.jdbc.H2Profile.api._

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Await

object SlickModify {
  val database = Database.forConfig("h2mem1")
  val messages = Messages.messages

  def execute[T](action: DBIO[T]): T = {
    Await.result(database.run(action), 4.seconds)
  }

  def main(args: Array[String]): Unit = {
    /** Deleting  */
    val removeHal: DBIO[Int] =
      messages
        .filter(_.sender === "HAL")
        .delete
    // res17: Int = 9 (number of row deleted)
    messages
      .filter(_.sender === "HAL")
      .delete
      .statements
      .head
    // res18: String = "delete from \"message\" where \"message\".\"sender\" = 'HAL'"

    /** Updating single field */
    val updateQuery =
      messages
        .filter(_.sender === "HAL")
        .map(_.sender)
    // Creating a row to modify
    // Pick filter all messages with sender HAL and chose their sender
    execute(updateQuery.update("HAL 9000"))
    // update all HAL fields as HAL 9000

    updateQuery.updateStatement
    // String = "update \"message\" set \"sender\" = ? where \"message\".\"sender\" = 'HAL'"

    /** Updating multiply row */
    val query = messages
      .filter(_.id === 1016L)
      .map(message =>
        (message.sender, message.content))
    // Chose the message with id 1016, and get the query with it sender and content

    execute(query.update("HAL 9000", "Sure Dave. Come right in."))

    case class NameText(name: String, text: String)

    val newValue = NameText("Dave", "Now I totally don't trust you")

    messages
      .filter(_.id=== 1067L)
      .map(m => (m.sender, m.content).mapTo[NameText])
      .update(newValue)
    // we can use case classes as parameters to update


  }

}
