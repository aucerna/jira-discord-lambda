package com.andyczerwonka

import io.circe.{HCursor, Json}

import scala.util.Try

abstract class JiraEvent(val eventTypeLabel: String, cursor: HCursor) {
  val key = cursor.downField("issue").downField("key").as[String].getOrElse(throw new Exception("key not found"))
  val url = {
    val uriString = cursor.downField("issue").downField("self").as[String].getOrElse(throw new Exception("url not found"))
    val root = uriString.split("/").take(3).mkString("/")
    s"$root/browse/$key"
  }
  val summary = cursor
    .downField("issue")
    .downField("fields")
    .downField("summary").as[String].getOrElse(throw new Exception("title not found"))
  val typename = cursor
    .downField("issue")
    .downField("fields")
    .downField("issuetype")
    .downField("name").as[String].getOrElse(throw new Exception("type not found"))
  val icon = cursor
    .downField("issue")
    .downField("fields")
    .downField("issuetype")
    .downField("iconUrl").as[String].getOrElse(throw new Exception("icon not found"))
  val status = cursor
    .downField("issue")
    .downField("fields")
    .downField("status")
    .downField("name").as[String].getOrElse(throw new Exception("status not found"))

  def description(): String
  def author(): String
  def avatar(): String
  def color(): Int
}

class CommentEvent(event: String, cursor: HCursor) extends JiraEvent(event, cursor) {
  override val description = cursor
    .downField("comment")
    .downField("body").as[String].getOrElse("Missing Comment")

  override val author = cursor
    .downField("comment")
    .downField("updateAuthor")
    .downField("displayName").as[String].getOrElse("Missing Author")
  
  override val avatar = cursor
    .downField("comment")
    .downField("updateAuthor")
    .downField("avatarUrls")
    .downField("48x48").as[String].getOrElse("Missing Avatar")

  override val color = 4540783
}

class IssueCreatedEvent(cursor: HCursor) extends JiraEvent("Issue Created", cursor) {
  override val description = cursor
    .downField("issue")
    .downField("fields")
    .downField("description").as[String].getOrElse("Missing Description")

  override val author = cursor
    .downField("issue")
    .downField("fields")
    .downField("creator")
    .downField("displayName").as[String].getOrElse("Missing Author")

  override val avatar = cursor
    .downField("user")
    .downField("avatarUrls")
    .downField("48x48").as[String].getOrElse("Missing Avatar")

  override val color = 16711680
}

class IssueUpdatedEvent(cursor: HCursor) extends JiraEvent("Issue Updated", cursor) {
  override val description = cursor
    .downField("issue")
    .downField("fields")
    .downField("description").as[String].getOrElse("Missing Description")

  override val author = cursor
    .downField("user")
    .downField("displayName").as[String].getOrElse("Missing Author")

  override val avatar = cursor
    .downField("user")
    .downField("avatarUrls")
    .downField("48x48").as[String].getOrElse("Missing Avatar")

  val colorName = cursor
    .downField("issue")
    .downField("fields")
    .downField("status")
    .downField("statusCategory")
    .downField("colorName").as[String].getOrElse("Missing Status Color")

  val colors = Map((s"yellow", 2436221), (s"green", 34650), (s"medium-gray", 13421772))

  override val color = colors(colorName)
}

object JiraParser {

  private def isStatusChange(cursor: HCursor) = {
    val changeType = cursor
      .downField("changelog")
      .downField("items")
      .downArray
      .downField("fieldId").as[String].getOrElse("Not a status change")
    changeType == "status"
  }

  def parse(body: Json): Try[JiraEvent] = {
    val cursor: HCursor = body.hcursor
    val event = cursor.downField("webhookEvent").as[String] map {
      case "jira:issue_created" => new IssueCreatedEvent(cursor)
      case "jira:issue_updated" if isStatusChange(cursor) => new IssueUpdatedEvent(cursor)
      case "comment_created" => new CommentEvent("Comment Created", cursor)
      case "comment_updated" => new CommentEvent("Comment Updated", cursor)
      case et@_ => throw new Exception(s"$et is an event that we don't handle")
    }
    event.toTry
  }

}


