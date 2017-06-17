package com.socrates.extremestartup

import akka.actor.{Actor, ActorLogging, Props}
import com.socrates.extremestartup.Game.{NoAnswer, SuccessfulAnswer, UnsuccessfulAnswer}
import play.api.libs.ws.ahc.StandaloneAhcWSClient

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}


class Player(id: Int, name: String, baseUrl: String, wsClient: StandaloneAhcWSClient)
  extends Actor
    with PlayerGateway
    with ActorLogging {

  override def receive: Receive = {

    case query: Query =>

      import query._

      log.info("Question Received!")

      val game = sender()

      call(baseUrl, question, wsClient).onComplete {
        case Success(answer) if answer.toUpperCase == expectedAnswer.toUpperCase =>
          log.info(s"Answer Successful! -> $answer")
          game ! SuccessfulAnswer(id)
        case Success(answer) if answer.toUpperCase != expectedAnswer.toUpperCase =>
          log.info(s"Answer UnSuccessful! Expected(${expectedAnswer.toUpperCase}) Got(${answer.toUpperCase})")
          game ! UnsuccessfulAnswer(id)
        case Failure(e) =>
          log.info(s"Player failed to answer with exception message ${e.getMessage}")
          game ! NoAnswer(id)
      }
  }
}

object Player {

  def props(id: Int, name: String, ip: String, wsClient: StandaloneAhcWSClient): Props =
    Props(new Player(id, name, ip, wsClient))

}
