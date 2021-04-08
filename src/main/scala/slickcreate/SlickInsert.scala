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
     *  Force Insert
     */

    val insertAction =
      messages += Messages.Message("HAL", "No, seriously, Dave, I can't let you in.")
    execute(insertAction)
    insertAction.statements.head
    // res2: String = "insert into \"message\" (\"sender\",\"content\") values (?,?)"

    Messages.Message("Dave", "You're off my Christmas card list.")
    // res3: Message = Message("Dave", "You're off my Christmas card list.", 0L)

    // Force inserting with 1000 ID
    val forceInsertAction = messages forceInsert
      Messages.Message(
      "HAL",
      "I'm a computer, what would I do with a Christmas card anyway?",
      1000L)

    execute(forceInsertAction)
    //executing the action

    execute(messages.filter(_.id === 1000L).result)
    // result:
    // Seq[MessageTable#TableElementType] = Vector(
    // Message(
    // "HAL",
    // "I'm a computer, what would I do with a Christmas card anyway?",
    // 1000L)
    // )

    /** Retrieving a Primary Keys on Insert */

    val insertDave : DBIO[Long] = {
      messages.returning(messages.map(_.id)) += Messages.Message("Dave", "Point taken.")
    }
    //The argument to messages returning is a Query over the same table, which is why
    //messages.map(_.id) makes sense here. The query specifies what data we’d like
    //the database to return once the insert has finished.

    val primaryKey: Long = execute(insertDave)

    execute(messages.filter(_.id === 1000L).result.headOption)
    // Option[Message] = Some(Message("Dave", "Point taken.", 1000L))

    lazy val messagesReturningId = messages returning messages.map(_.id)
    execute(messagesReturningId += Messages.Message("HAL", "Humans, eh."))
    // res9: messagesReturningId.SingleInsertResult = 1002L

    /** Retrieving a Rows on Insert; BUT H2 DON'T ALLOW US TO DO THIS */
    //NOT WORKING IN H2:
    execute(messages returning messages +=
      Messages.Message("Dave", "So... what do we do now?"))

    val messagesReturningRow =
      messages returning messages.map(_.id) into { (message, id) => message.copy(id = id) }

    val insertMessage : DBIO[Messages.Message] = {
      messagesReturningRow += Messages.Message("Dave", "You're such a jerk.")
    }

    execute(insertMessage)
    // result : Messages.Message("Dave", "You're such a jerk.", 1003L)

    /** Insert Specific Columns*/
    messages.map(_.sender).insertStatement
    // insert into message sender values(?)
    messages.map(_.sender)
    execute(messages.map(_.sender) += "HAL")
    //ERROR cause tables can't be Nullable, but theoreticaly it's ok

    val testMessages = Seq(
      Messages.Message("Dave", "Hello, HAL. Do you read me, HAL?"),
      Messages.Message("HAL", "Affirmative, Dave. I read you."),
      Messages.Message("Dave", "Open the pod bay doors, HAL."),
      Messages.Message("HAL", "I'm sorry, Dave. I'm afraid I can't do that.")
    )

    execute(messages ++= testMessages)

    /** More control over Inserts */

    val data = Query(("Stanley", "Cut!"))
    val exist =
      messages
        .filter(m => m.sender === "Stanley" && m.content === "Cut")
        .exists

    val selectExpression = data.filterNot(_ => exist)

  }
}
