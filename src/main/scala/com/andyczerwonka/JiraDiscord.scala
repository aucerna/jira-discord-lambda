package com.andyczerwonka

import java.io.{InputStream, OutputStream}

import com.amazonaws.services.lambda.runtime.{Context, RequestStreamHandler}

class JiraDiscord extends RequestStreamHandler {

  override def handleRequest(input: InputStream, output: OutputStream, context: Context): Unit  = {

    val jsonResponse =
      """
        {
          "isBase64Encoded": false,
          "statusCode": 200,
          "headers": { "Access-Control-Allow-Origin": "*" },
          "body": "Yo!"
        }
      """.stripMargin

    output.write(jsonResponse.getBytes("UTF-8"))
  }

}
