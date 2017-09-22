package com.andyczerwonka

import java.io.{InputStream, OutputStream}

import com.amazonaws.services.lambda.runtime.{Context, RequestStreamHandler}
import io.circe.{HCursor, Json}
import io.circe.parser._

class JiraDiscord extends RequestStreamHandler {

  override def handleRequest(input: InputStream, output: OutputStream, context: Context): Unit  = {

    val rawJson = scala.io.Source.fromInputStream(input).getLines().mkString("\n")
    val doc = parse(rawJson).getOrElse(Json.Null)

    val cursor: HCursor = doc.hcursor
    val path = cursor.downField("pathParameters").downField("proxy").as[String].getOrElse("missing-id/missing-token")
    var Array(discordId, discordToken) = path.split("/")

    val jsonResponse =
      raw"""
        {
          "isBase64Encoded": false,
          "statusCode": 200,
          "headers": {
            "Access-Control-Allow-Origin": "*",
            "Content-Type": "application/json"
          },
          "body": "Yo!"
        }
      """.stripMargin

    output.write(jsonResponse.getBytes("UTF-8"))
  }

}
