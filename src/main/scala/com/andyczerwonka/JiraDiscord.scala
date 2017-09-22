package com.andyczerwonka

import java.io.{InputStream, OutputStream}

import com.amazonaws.services.lambda.runtime.{Context, RequestStreamHandler}

class JiraDiscord extends RequestStreamHandler {

  override def handleRequest(input: InputStream, output: OutputStream, context: Context): Unit  = {
    val result = s"Yo!"
    output.write(result.getBytes("UTF-8"))
  }

}
