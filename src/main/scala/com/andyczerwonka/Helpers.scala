package com.andyczerwonka

import com.softwaremill.sttp._
import io.circe.{HCursor, Json}

trait Helpers {

  val okResponse =
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

  def discordUri(json: Json): Uri = {
    val cursor: HCursor = json.hcursor
    val path = cursor.downField("pathParameters").downField("proxy").as[String].getOrElse("missing-id/missing-token")
    val Array(discordId, discordToken) = path.split("/")
    uri"https://discordapp.com/api/webhooks/$discordId/$discordToken"
  }

}
