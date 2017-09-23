package com.andyczerwonka

object DiscordProtocol {

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

  case class Embeds(title: String, url: String, description: String, color: Int)
  case class DiscordWebhook(username: String, avatar_url: String, embeds: Array[Embeds])

  def discordWebhook(title: String, url: String, description: String): DiscordWebhook = {
    val embeds = Array(Embeds(title, url, description, 1681177))
    DiscordWebhook("JIRA", "https://wiki.jenkins-ci.org/download/attachments/2916393/headshot.png", embeds)
  }

}
