package com.socrates.extremestartup

import java.net.URLEncoder

import akka.http.scaladsl.model.StatusCodes
import play.api.libs.ws.ahc.StandaloneAhcWSClient

import scala.concurrent.{ExecutionContext, Future}

trait PlayerGateway {

  def call(baseUrl: String, question: String, client: StandaloneAhcWSClient)(implicit ec: ExecutionContext): Future[String] = {

    val url = s"$baseUrl/?q=${URLEncoder.encode(question, "UTF-8")}"

    client.url(url).get().map {
      case response if response.status == StatusCodes.OK.intValue =>
        response.body
      case response => throw new RuntimeException(s"Call failed with response status ${response.status}")
    }

  }

}
