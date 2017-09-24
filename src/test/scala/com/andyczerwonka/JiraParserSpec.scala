package com.andyczerwonka

import org.scalatest._
import io.circe.{HCursor, Json}
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._

class JiraParserSpec extends FlatSpec with Matchers {

  import scala.io.Source
  val rawCreateEvent = Source.fromResource("root-payload.json").mkString
  val jsonCreateEvent = parse(rawCreateEvent).getOrElse(Json.Null)
  val cursor: HCursor = jsonCreateEvent.hcursor
  val bodyString = cursor.downField("body").as[String].getOrElse("")
  val bodyJson = parse(bodyString).getOrElse(Json.Null)


  "The parser" should "generate a valid webook" in {
    print(bodyJson)
    val cursor: HCursor = bodyJson.hcursor
    val eventType = cursor.downField("webhookEvent").as[String].getOrElse("")
    eventType shouldEqual "comment_created"
  }
}
