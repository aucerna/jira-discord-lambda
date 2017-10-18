package com.andyczerwonka

case class Footer(text: String)
case class Embeds(title: String, url: String, description: String, color: Int, footer: Footer)
case class DiscordWebhook(username: String, avatar_url: String, embeds: Array[Embeds])

object DiscordWebhook {

  /**
    * {
    *   "username": "JIRA",
    *   "avatar_url": "https://wiki.jenkins-ci.org/download/attachments/2916393/headshot.png",
    *   "embeds": [{
    *     "title": "This is the Title",
    *     "url": "https://example.com",
    *     "description": "This is a description that supports markdown txt",
    *     "color": 1681177,
    *     "footer": {
    *       "text": "This is a footer"
    *      }
    *    }]
    * }
    */

  def apply(title: String, url: String, description: String, author: String, color: Int): DiscordWebhook = {
    val trimmedDesc = description.take(2048) // https://discordapp.com/developers/docs/resources/channel#embed-limits
    val embeds = Array(Embeds(title, url, trimmedDesc, color, Footer(author)))
    DiscordWebhook("JIRA", "https://a.slack-edge.com/ae7f/plugins/jira/assets/service_512.png", embeds)
  }

}
