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
    val json = parseJson("create-comment.json")
    inside(JiraParser.parse(json)) { case Success(event) =>
      event.summary shouldEqual "tech spike: persistence strategy for CDM"
      event.eventTypeLabel shouldEqual "Comment Created"
      event.url shouldEqual "https://enersight.atlassian.net/browse/CDM-7"
    }
  }

  it should "generate a valid model on the updated-comment event" in {
    val json = parseJson("update-comment.json")
    inside(JiraParser.parse(json)) { case Success(event) =>
      event.summary shouldEqual "tech spike: persistence strategy for CDM"
      event.eventTypeLabel shouldEqual "Comment Updated"
      event.url shouldEqual "https://enersight.atlassian.net/browse/CDM-7"
    }
  }

  it should "generate events for newly created bugs" in {
    val json = parseJson("create-bug.json")
    inside(JiraParser.parse(json)) { case Success(event: BugCreatedEvent) =>
      event.summary shouldEqual "Test Bug Take 3"
      event.eventTypeLabel shouldEqual "Bug Created"
      event.url shouldEqual "https://jira.3esi-enersight.com/browse/MNG-2229"
      event.description shouldEqual "Generating a JIRA webhook event."
    }
  }

}
