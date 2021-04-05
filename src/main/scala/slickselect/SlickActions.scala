package com.haile.app
package slickselect

import slick.jdbc.H2Profile.api._

import scala.concurrent.{Future, Await}
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

object SlickActions {

  val database = Database.forConfig("h2mem1")
  val messages = Messages.messages

  def execute[T](action: DBIO[T]): T = {
    Await.result(database.run(action), 4.seconds)
  }

  def main(args: Array[String]): Unit = {

    /** Actions
     *
     *  Difference between run() and stream()
     *  To execute an action, we pass it to one of two methods on our database object:
     *
     *  database.run() - Runs the action and returns all results in a single collection.
     *  These are know as materialized results
     *
     *  database.stream() - Runs an action and returns its result in a Stream, allowing us to process large
     *  datasets incrementally without consuming large amounts of memory
     *  Also has integration with Akka
     */

    val futureMessages = database.run(messages.result)
    // futureMessages: Future[Seq[MessageTable#TableElementType]] = Future(Success(
    //  Vector(Message(Dave,Hello, HAL. Do you read me, HAL?,1), Message(HAL,
    //  Affirmative, Dave. I read you.,2), Message(Dave,Open the pod bay doors,
    //  HAL.,3), Message(HAL,I'm sorry, Dave. I'm afraid I can't do that.,4))))

    database.stream(messages.result).foreach(println)
    // Just printing every row

    /** Expression
     *  Equality and Inequality
     *  String concat
     *  Numeric Methods
     *  Boolean Methods
     *  Date and Time methods
     */

    messages.filter(_.sender === "Dave").result.statements.mkString
    // Select all from messages where sender is Dave

    messages.filter(_.sender =!= "Dave").result.statements.mkString
    // Select everything from messages where Dave is not a sender

    messages.filter(_.sender < "HAL").result.statements.mkString
    // Iterable[String] = List(/*select everything from message where sender is < "HAL"*/)

    messages.filter(m => m.sender >= m.content).result.statements.mkString
    // Iterable[String] = List(/*select everything from message where sender >= content*/)

    messages.map(m => m.sender ++ "> " ++ m.content).result.statements.mkString
    // Select (sender and concat it with "> " to content) from messages
    // Beside concat strings we can make some algebraic actions with numeric data like plus, divide and so on
    // And Boolean expression like && || or !

    final class PersonTable(tag: Tag) extends Table[Option[String]](tag, "PERSON") {
      def nickname = column[Option[String]]("nickname")
      def * = (nickname)
    }

    // You can put an Option as an expression argument, but not None or Some
    messages.filter(_.id === Option(123L)).result.statements
    // result Iterable[String] = List("select \"sender\", \"content\", \"id\" from \"message\" where \"id\" = 123")

    messages.filter(_.id === (Some(123L): Option[Long]) )
    // Same as previous

    /** Controlling the Queries: sort, take and drop */

    execute(messages.sortBy(_.sender).result).foreach(println)
    // Message(Dave,Hello, HAL. Do you read me, HAL?,1)
    // Message(Dave,Open the pod bay doors, HAL.,3)
    // Message(HAL,Affirmative, Dave. I read you.,2)
    // Message(HAL,I'm sorry, Dave. I'm afraid I can't do that.,4)

    execute(messages.sortBy(_.sender.desc).result).foreach(println)
    // Message(HAL,Affirmative, Dave. I read you.,2)
    // Message(HAL,I'm sorry, Dave. I'm afraid I can't do that.,4)
    // Message(Dave,Hello, HAL. Do you read me, HAL?,1)
    // Message(Dave,Open the pod bay doors, HAL.,3)

    // Sorting by multiply columns:
    execute(messages.sortBy(m => (m.sender, m.content)).result)
    //  Iterable[String] = List(
    //  "select \"sender\", \"content\", \"id\" from \"message\" order by \"sender
    //  \", \"content\""

    // Now we want to take only 5 in the row form the sorted result
    execute(messages.sortBy(_.sender).take(5).result)

    // Or we have to show next page, in case it's 6 to 10 rows
    execute(messages.sortBy(_.sender).drop(5).take(5).result)
    //  select "sender", "content", "id"
    //  from "message"
    //  order by "sender"
    //  limit 5 offset 5


  }

}
