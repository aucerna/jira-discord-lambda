package com.andyczerwonka

import io.circe.{HCursor, Json}

import scala.util.{Success, Try}

abstract class JiraEvent(val key: String, val eventTypeLabel: String, cursor: HCursor) {
  def summary(): String
  def url(): String
  def description(): String
  def author(): String
  def color(): Int
}

class CommentEvent(key: String, event: String, cursor: HCursor) extends JiraEvent(key, event, cursor) {
  val summary = event
  val url = {
    val uriString = cursor.downField("comment").downField("self").as[String].getOrElse(throw new Exception("url not found"))
    val root = uriString.split("/").take(3).mkString("/")
    s"$root/browse/$key"
  }
  override val description = cursor
    .downField("comment")
    .downField("body").as[String].getOrElse("Missing Comment")

  override val author = cursor
    .downField("comment")
    .downField("updateAuthor")
    .downField("displayName").as[String].getOrElse("Missing Author")

  override val color = 4540783
}

class BugCreatedEvent(key: String, cursor: HCursor) extends JiraEvent(key, "Bug Created", cursor) {
  val summary = cursor
    .downField("issue")
    .downField("fields")
    .downField("summary").as[String].getOrElse(throw new Exception("title not found"))
  val url = {
    val uriString = cursor.downField("issue").downField("self").as[String].getOrElse(throw new Exception("url not found"))
    val root = uriString.split("/").take(3).mkString("/")
    s"$root/browse/$key"
  }
  override val description = cursor
    .downField("issue")
    .downField("fields")
    .downField("description").as[String].getOrElse("Missing Description")

  override val author = cursor
    .downField("issue")
    .downField("fields")
    .downField("creator")
    .downField("displayName").as[String].getOrElse("Missing Author")

  override val color = 16711680
}

object JiraParser {

  def isBug(cursor: HCursor) = {
    val issyeType = cursor
      .downField("issue")
      .downField("fields")
      .downField("issuetype")
      .downField("name").as[String].getOrElse("Not a Bug")
    issyeType == "Bug"
  }

  def parse(issueKey: String, body: Json): Try[JiraEvent] = {
    Try {
      val cursor: HCursor = body.hcursor
      cursor.downField("webhookEvent").as[String].getOrElse("") match {
        case "jira:issue_created" if isBug(cursor) => new BugCreatedEvent(issueKey, cursor)
        case "comment_created" => new CommentEvent(issueKey, "Comment Created", cursor)
        case "comment_updated" => new CommentEvent(issueKey, "Comment Updated", cursor)
        case et @ _ => throw new Exception(s"$et is an event that we don't handle")
      }
    }
  }

}


