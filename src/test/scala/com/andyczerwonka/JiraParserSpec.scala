package com.andyczerwonka

import org.scalatest._
import io.circe.{HCursor, Json}
import io.circe.parser._


import scala.util.Success

class JiraParserSpec extends FlatSpec with Matchers with Inside {

  private def extractBody(resourceName: String) = {
    import scala.io.Source
    val rawPayload = Source.fromResource(resourceName).mkString
    val rawJson = parse(rawPayload).getOrElse(Json.Null)
    val bodyString = rawJson.hcursor.downField("body").as[String].getOrElse("")
    parse(bodyString).getOrElse(Json.Null)
  }

  private def parseJson(resourceName: String) = {
    import scala.io.Source
    val rawPayload = Source.fromResource(resourceName).mkString
    parse(rawPayload).getOrElse(Json.Null)
  }

  "The JIRA parser" should "generate a valid model on the created-comment event" in {
    val json = parseJson("create-comment.json")
    inside(JiraParser.parse("MNG-1234", json)) { case Success(event) =>
      event.summary shouldEqual "Comment Created"
      event.eventTypeLabel shouldEqual "Comment Created"
      event.url shouldEqual "https://enersight.atlassian.net/browse/MNG-1234"
    }
  }

  it should "generate a valid model on the updated-comment event" in {
    val json = parseJson("update-comment.json")
    inside(JiraParser.parse("MNG-1234", json)) { case Success(event) =>
      event.summary shouldEqual "Comment Updated"
      event.eventTypeLabel shouldEqual "Comment Updated"
      event.url shouldEqual "https://enersight.atlassian.net/browse/MNG-1234"
    }
  }

  it should "generate events for newly created bugs" in {
    val json = parseJson("create-bug.json")
    inside(JiraParser.parse("MNG-2229", json)) { case Success(event: BugCreatedEvent) =>
      event.summary shouldEqual "Test Bug Take 3"
      event.author shouldEqual "Andy Czerwonka"
      event.eventTypeLabel shouldEqual "Bug Created"
      event.url shouldEqual "https://jira.3esi-enersight.com/browse/MNG-2229"
      event.description shouldEqual "Generating a JIRA webhook event."
    }
  }

}
