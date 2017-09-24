package com.andyczerwonka

import com.softwaremill.sttp.Uri
import io.circe.parser.parse
import io.circe.{HCursor, Json}

abstract class IssueUpdated(val event: String, cursor: HCursor) {
  val key = cursor.downField("issue").downField("key").as[String].getOrElse(throw new Exception("key not found"))
  val url = {
    val uriString = cursor.downField("issue").downField("self").as[String].getOrElse(throw new Exception("url not found"))
    val root = uriString.split("/").take(3).mkString("/")
    s"$root/browse/$key"
  }
  val title = cursor
    .downField("issue")
    .downField("fields")
    .downField("issuetype")
    .downField("description").as[String].getOrElse(throw new Exception("title not found"))
  def description(): String
  def author(): String
}

class CommentEvent(event: String, cursor: HCursor) extends IssueUpdated(event, cursor) {

  override val description = cursor
    .downField("comment")
    .downField("body").as[String].getOrElse("Unknown Comment")

  override val author = cursor
    .downField("comment")
    .downField("updateAuthor")
    .downField("displayName").as[String].getOrElse("Unknown Author")

}

object JiraParser {

  def parse(body: Json): IssueUpdated = {
    val cursor: HCursor = body.hcursor
    cursor.downField("issue_event_type_name").as[String].getOrElse("") match {
      case "issue_commented" => new CommentEvent("Comment Created", cursor)
      case "issue_comment_edited" => new CommentEvent("Comment Updated", cursor)
      case _ => throw new Exception("Unknown JIRA Event Type")
    }

  }

}


