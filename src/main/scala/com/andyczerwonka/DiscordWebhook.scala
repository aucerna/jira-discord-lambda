package com.andyczerwonka

case class Thumbnail(url: String)
case class Footer(text: String, icon_url: String)
case class Embeds(title: String, url: String, description: String, color: Int, thumbnail: Thumbnail, footer: Footer)
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

  def apply(title: String, url: String, description: String, author: String, color: Int, icon: String, avatar: String): DiscordWebhook = {
    val trimmedDesc = description.take(2048) // https://discordapp.com/developers/docs/resources/channel#embed-limits
    val embeds = Array(Embeds(title, url, trimmedDesc, color, Thumbnail(avatar), Footer(author, icon)))
    DiscordWebhook("JIRA", "https://a.slack-edge.com/ae7f/plugins/jira/assets/service_512.png", embeds)
  }

}
