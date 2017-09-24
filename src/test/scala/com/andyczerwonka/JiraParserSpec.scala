package com.andyczerwonka

import com.softwaremill.sttp.Uri
import org.scalatest._
import io.circe.{HCursor, Json}
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._

class JiraParserSpec extends FlatSpec with Matchers {

  def extractBody(resourceName: String) = {
    import scala.io.Source
    val rawPayload = Source.fromResource(resourceName).mkString
    val rawJson = parse(rawPayload).getOrElse(Json.Null)
    val bodyString = rawJson.hcursor.downField("body").as[String].getOrElse("")
    parse(bodyString).getOrElse(Json.Null)
  }

  "The JIRA parser" should "generate a valid model on the created-comment event" in {
    val json = extractBody("create-comment.json")
    println(json)
    val model = JiraParser.parse(json)
    model.title shouldEqual "A problem which impairs or prevents the functions of the product."
    model.event shouldEqual "Comment Created"
    model.url shouldEqual "https://jira.3esi-enersight.com/browse/MNG-1234"
  }

  it should "generate a valid model on the updated-comment event" in {
    val json = extractBody("update-comment.json")
    val model = JiraParser.parse(json)
    model.title shouldEqual "A problem which impairs or prevents the functions of the product."
    model.event shouldEqual "Comment Updated"
    model.url shouldEqual "https://jira.3esi-enersight.com/browse/MNG-1234"
  }

}
