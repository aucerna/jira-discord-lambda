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
      event.author shouldEqual "Karl Martens"
      event.eventTypeLabel shouldEqual "Comment Created"
      event.url shouldEqual "https://jira/browse/MNG-2206"
      event.description shouldEqual "Hi fred"
    }
  }

  // see https://getsupport.atlassian.com/servicedesk/customer/portal/23/JST-348034
  it should "also work for for the new JIRA cloud version" in {
    val json = parseJson("jira-cloud-update.json")
    inside(JiraParser.parse(json)) { case Success(event) =>
      event.summary shouldEqual "Import should validate selection constraints values to be non-negative numbers"
      event.author shouldEqual "Shivalee Kaul"
      event.eventTypeLabel shouldEqual "Comment Created"
      event.url shouldEqual "https://enersight.atlassian.net/browse/SP-1522"
      event.description shouldEqual "Hi fred"
    }
  }

  it should "generate events for newly created bugs" in {
    val json = parseJson("create-bug.json")
    inside(JiraParser.parse(json)) { case Success(event: BugCreatedEvent) =>
      event.summary shouldEqual "Test Bug Take 3"
      event.author shouldEqual "Andy Czerwonka"
      event.eventTypeLabel shouldEqual "Bug Created"
      event.url shouldEqual "https://jira.3esi-enersight.com/browse/MNG-2229"
      event.description shouldEqual "Generating a JIRA webhook event."
    }
  }

}
