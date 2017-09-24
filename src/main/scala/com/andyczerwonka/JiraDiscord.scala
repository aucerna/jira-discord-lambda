package com.andyczerwonka

import java.io.{InputStream, OutputStream}

import com.amazonaws.services.lambda.runtime.{Context, RequestStreamHandler}
import com.andyczerwonka.DiscordWebhook._
import com.softwaremill.sttp._
import io.circe.Json
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._

class JiraDiscord extends RequestStreamHandler with Helpers {

  override def handleRequest(input: InputStream, output: OutputStream, context: Context): Unit = {
    val logger = context.getLogger
    val rawJson = scala.io.Source.fromInputStream(input).getLines().mkString("\n")
    logger.log(rawJson)
    val jsonDoc = parse(rawJson).getOrElse(Json.Null)
    val uri = discordUri(jsonDoc)
    implicit val backend: SttpBackend[Id, Nothing] = HttpURLConnectionBackend()
    try {
      val json = extractJiraBody(jsonDoc)
      val model = JiraParser.parse(json)
      val title = s"${model.key}: ${model.title}"
      val desc = s"**${model.event}**\n${model.description()}"
      val msg = DiscordWebhook(title, model.url, desc, model.author())
      val request = sttp
        .contentType("application/json")
        .header("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36")
        .body(msg.asJson.noSpaces)
        .post(uri)
      logger.log(s"Sending to ${uri.toString}")
      val response = request.send()
      logger.log(response.unsafeBody)
      output.write(okResponse)
    } finally {
      backend.close()
    }
  }

}
