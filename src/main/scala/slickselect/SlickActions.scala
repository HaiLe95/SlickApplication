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
     *
     *
     */

    messages.filter(_.sender === "Dave").result.statements.mkString
    // Select all from messages where sender is Dave

    messages.filter(_.sender =!= "Dave").result.statements.mkString
    // Select everything from messages where Dave is not a sender

    messages.filter(_.sender < "HAL").result.statements.mkString
    // Iterable[String] = List(/*select everything from message where sender is < "HAL"*/)

    messages.filter(m => m.sender >= m.content).result.statements.mkString
    // Iterable[String] = List(/*select everything from message where sender >= content*/)

    
  }

}
