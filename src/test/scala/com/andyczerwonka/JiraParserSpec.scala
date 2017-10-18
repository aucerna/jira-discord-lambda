package com.andyczerwonka

import org.scalatest._
import io.circe.{HCursor, Json}
import io.circe.parser._


import scala.util.Success

class JiraParserSpec extends FlatSpec with Matchers with Inside {

  def extractBody(resourceName: String) = {
    import scala.io.Source
    val rawPayload = Source.fromResource(resourceName).mkString
    val rawJson = parse(rawPayload).getOrElse(Json.Null)
    val bodyString = rawJson.hcursor.downField("body").as[String].getOrElse("")
    parse(bodyString).getOrElse(Json.Null)
  }

  def parseJson(resourceName: String) = {
    import scala.io.Source
    val rawPayload = Source.fromResource(resourceName).mkString
    parse(rawPayload).getOrElse(Json.Null)
  }

  "The JIRA parser" should "generate a valid model on the created-comment event" in {
    val json = extractBody("create-comment.json")
    inside(JiraParser.parse(json)) { case Success(event) =>
      event.summary shouldEqual "Tax Import Not Making Sense"
      event.eventTypeLabel shouldEqual "Comment Created"
      event.url shouldEqual "https://jira.3esi-enersight.com/browse/MNG-1234"
    }
  }

  it should "generate a valid model on the updated-comment event" in {
    val json = extractBody("update-comment.json")
    inside(JiraParser.parse(json)) { case Success(event) =>
      event.summary shouldEqual "Tax Import Not Making Sense"
      event.eventTypeLabel shouldEqual "Comment Updated"
      event.url shouldEqual "https://jira.3esi-enersight.com/browse/MNG-1234"
    }
  }

  it should "also work for Karl" in {
    val json = parseJson("create-comment-karl.json")
    inside(JiraParser.parse(json)) { case Success(event) =>
      event.summary shouldEqual "Analysis Settings Dialog does not preserve user settings in successive runs"
      event.eventTypeLabel shouldEqual "Comment Created"
      event.url shouldEqual "https://jira/browse/MNG-2206"
      event.description shouldEqual "Hi fred"
    }
  }

}
