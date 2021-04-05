package com.haile.app
package slickselect

import slick.jdbc.H2Profile.api._

object Messages {

  case class Message(sender:  String,
                     content: String,
                     id:      Long = 0L)


  class MessageTable(tag: Tag) extends Table[Message](tag, "MESSAGE") {
    def id:       Rep[Long]   = column[Long]("ID", O.PrimaryKey, O.AutoInc)
    def sender:   Rep[String] = column[String]("SENDER")
    def content:  Rep[String] = column[String]("CONTENT")

    def * = (sender, content, id).mapTo[Message]
  }

  /** MessageTable is the type of Query that slick uses to represent all actions */
  lazy val messages = TableQuery[MessageTable]

}
