package com.andyczerwonka

import io.circe.parser.parse
import io.circe.{HCursor, Json}

abstract class EventModel(cursor: HCursor) {
  def title(): String
  def url(): String
  def description(): String
  def author(): String
}

class CommentCreatedEventModel(cursor: HCursor) extends EventModel(cursor) {
  override def title(): String = "Comment Created"

  override def url(): String = "https://example.com"

  override def description() = cursor
    .downField("comment")
    .downField("body").as[String].getOrElse("Unknown Comment")

  override def author() = cursor
    .downField("comment")
    .downField("updateAuthor")
    .downField("displayName").as[String].getOrElse("Unknown Author")
}

object JiraParser {

  def parse(body: Json): EventModel = {
    val cursor: HCursor = body.hcursor
    val eventType = cursor.downField("webhookEvent").as[String].getOrElse("")
    eventType match {
      case "comment_created" => new CommentCreatedEventModel(cursor)
      case _ => throw new Exception("Unknown JIRA Event Type")
    }
  }

}


