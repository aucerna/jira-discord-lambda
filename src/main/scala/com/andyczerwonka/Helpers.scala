package com.andyczerwonka

import com.softwaremill.sttp._
import io.circe.parser._
import io.circe.{HCursor, Json}

import scala.util.Try

trait Helpers {

  val ok =
    raw"""
         |{
         |  "isBase64Encoded": false,
         |  "statusCode": 200,
         |  "headers": {
         |     "Access-Control-Allow-Origin": "*",
         |     "Content-Type": "application/json"
         |  },
         |  "body": "Webhook Delivered Ok!"
         |}
        """.stripMargin.getBytes("UTF-8")

  // the post to Discord will reject the hook unless it includes valid User-Agent
  val userAgent = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36"

  def discordUri(json: Json): Uri = {
    val cursor: HCursor = json.hcursor
    val path = cursor.downField("pathParameters").downField("proxy").as[String].getOrElse(throw new Exception("missing proxy URL"))
    val Array(discordId, discordToken) = path.split("/")
    uri"https://discordapp.com/api/webhooks/$discordId/$discordToken"
  }

  def extractJiraBody(json: Json): Try[Json] = {
    Try {
      val bodyString = json.hcursor.downField("body").as[String].getOrElse(throw new Exception("missing body"))
      parse(bodyString).getOrElse(Json.Null)
    }
  }

}
