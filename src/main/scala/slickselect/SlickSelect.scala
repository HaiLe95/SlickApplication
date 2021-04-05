package com.haile.app
package slickselect

import slick.jdbc.H2Profile.api._

import scala.concurrent.Await
import scala.concurrent.duration._

object SlickSelect {

  val database = Database.forConfig("h2mem1")
  val messages = Messages.messages

  def execute[T](action: DBIO[T]): T = {
    Await.result(database.run(action), 4.seconds)
   }


  def main(args: Array[String]): Unit = {

    /** SELECTING ALL >> How it looks like as string */
    messages.result.statements.mkString
    //  res0: String = "select \"sender\", \"content\", \"id\" from \"message\""

    /** Filtering */
    messages.filter(_.sender === "HAL")
    // result: Query[[MessageTable, MessageTable#TableElementType, Seq] = Rep(Filter @1690734410)
    messages.filter(_.sender === "HAL").result.statements.mkString
    // result String = "select \"sender\", \"content\", \"id\" from \"message\"where \"sender\" = 'HAL'"

    /** Transforming results */
    Query(1).result.statements.mkString
    // result String = "select 1"

    def freshTestData = Seq(
      Messages.Message("Dave", "Hello, HAL. Do you read me, HAL?"),
      Messages.Message("HAL", "Affirmative, Dave. I read you."),
      Messages.Message("Dave", "Open the pod bay doors, HAL."),
      Messages.Message("HAL", "I'm sorry, Dave. I'm afraid I can't do that.")
    )
    execute(messages.schema.create andThen(messages++= freshTestData))
    // result Option[Int] = Some(4)

    /** Using map() */


    val content = messages.map(_.content)
    // result Query[Rep[String], String, Seq] = Rep(Bind)
    // Selecting specific column from the Table
    execute(content.result)
    // Basically query will look like this:
    // [ select "content" from "message" ]

    messages.map(_.content).result.statements.mkString
    // result String = "select \"content\" from \"message\""

    val containsPods = messages.map(_.content).filter{content: Rep[String] => content like "%pod%"}
    // What's mean select everything from messages content that has "pod" in it
    // containsPods: Query[Rep[String], String, Seq] = Rep(Filter @172230316)

    execute(containsPods.result)
    // result Seq[String] = Vector("Open the pod bay doors, HAL.")

    messages.map(t => (t.id, t.content))
    // selecting id and content tables of messages

    /** We can map sets of columns to Scala data structures: */
    case class TextOnly(id: Long, content: String)

    val contentQuery = messages.map(t => (t.id, t.content).mapTo[TextOnly])
    execute(contentQuery.result)
    // result Seq[TextOnly] = Vector(
    // TextOnly(1L, "Hello, HAL. Do you read me, HAL?"),
    // TextOnly(2L, "Affirmative, Dave. I read you."),
    // TextOnly(3L, "Open the pod bay doors, HAL."),
    // TextOnly(4L, "I'm sorry, Dave. I'm afraid I can't do that.")
    // )

    // We also can select column expression as well as single columns
    messages.map(t => t.id * 1000L).result.statements.mkString
    // Select ID * 1000 form messages

    /** Using exist() */

    val containsBay = for {
      m <- messages
      if m.content like "%bay%"
    } yield m

    val bayMentioned: DBIO[Boolean] = containsBay.exists.result
    //The containsBay query returns all messages that mention “bay”. We can then use
    //this query in the bayMentioned expression to determine what to execute.



  }

}
