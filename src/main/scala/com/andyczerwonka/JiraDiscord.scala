package com.andyczerwonka

import java.io.{InputStream, OutputStream}

import com.amazonaws.services.lambda.runtime.{Context, RequestStreamHandler}
import com.amazonaws.services.lambda.runtime.LambdaLogger

import com.softwaremill.sttp._
import io.circe.parser._
import io.circe.{HCursor, Json}

class JiraDiscord extends RequestStreamHandler {

  override def handleRequest(input: InputStream, output: OutputStream, context: Context): Unit = {

    val logger = context.getLogger

    val rawJson = scala.io.Source.fromInputStream(input).getLines().mkString("\n")
    val jsonDoc = parse(rawJson).getOrElse(Json.Null)

    val cursor: HCursor = jsonDoc.hcursor
    val path = cursor.downField("pathParameters").downField("proxy").as[String].getOrElse("missing-id/missing-token")
    var Array(discordId, discordToken) = path.split("/")

    implicit val backend = HttpURLConnectionBackend()
    val testMsg =
      raw"""
           |{
           |  "avatar_url": "https://s3-us-west-2.amazonaws.com/slack-files2/avatars/2016-10-24/95495989216_cfd49d5cc47d4209fff6_512.png",
           |  "embeds": [{
           |    "color": 1681177,
           |    "footer": {
           |      "text": "This is a footer"
           |    },
           |
           |    "description": "This is a description that supports mardown txt",
           |     "title": "This is a message from AWS Lambda",
           |     "url": "https://example.com"
           |  }],
           |  "username": "JIRA"
           |}""".stripMargin

    val uri = uri"https://discordapp.com/api/webhooks/$discordId/$discordToken"
    logger.log(s"Sending to ${uri.toString}")
    try {
      val request = sttp
        .contentType("application/json")
        .header("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36")
        .body(testMsg)
        .post(uri)
      val response = request.send()
      logger.log(response.unsafeBody)
      val jsonResponse =
        raw"""
             |{
             |  "isBase64Encoded": false,
             |  "statusCode": 200,
             |  "headers": {
             |     "Access-Control-Allow-Origin": "*",
             |     "Content-Type": "application/json"
             |  },
             |  "body": "Yo!"
             |}
        """.stripMargin

      output.write(jsonResponse.getBytes("UTF-8"))
    } finally {
      backend.close()
    }



  }

}
