package com.andyczerwonka

import java.io.{InputStream, OutputStream}

import com.amazonaws.services.lambda.runtime.{Context, RequestStreamHandler}
import play.api.libs.json.{JsString, JsValue, Json}

class JiraDiscord extends RequestStreamHandler {

  override def handleRequest(input: InputStream, output: OutputStream, context: Context): Unit  = {

    val json: JsValue = Json.parse(input)

    val path = (json \ "pathParameters" \ "proxy").getOrElse(JsString("missing-id/missing-token"))
    var Array(discordId, discordToken) = Json.stringify(path).split("/")

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
