package com.andyczerwonka

import java.io.{InputStream, OutputStream}

import com.amazonaws.services.lambda.runtime.{Context, RequestStreamHandler}
import com.softwaremill.sttp._
import io.circe.Json
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._

class JiraDiscord extends RequestStreamHandler with Helpers {

  override def handleRequest(input: InputStream, output: OutputStream, context: Context): Unit = {
    val logger = context.getLogger
    val rawJson = scala.io.Source.fromInputStream(input).getLines().mkString("\n")
    val jsonDoc = parse(rawJson).getOrElse(Json.Null)
    val json = extractJiraBody(jsonDoc)
    logger.log(json.spaces2)
    JiraParser.parse(json) map { event =>
      implicit val backend: SttpBackend[Id, Nothing] = HttpURLConnectionBackend()
      try {
        val title = s"${event.key}: ${event.summary}"
        val desc = s"**${event.eventTypeLabel}**\n${event.description()}"
        val msg = DiscordWebhook(title, event.url, desc, event.author()).asJson.noSpaces
        val request = sttp
          .contentType("application/json")
          .header("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36")
          .body(msg)
          .post(discordUri(jsonDoc))
        request.send()
        output.write(ok)
      } finally {
        backend.close()
      }
    }

  }

}
