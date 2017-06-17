package com.socrates.extremestartup

import java.text.SimpleDateFormat
import java.util.Date

import akka.actor.{Actor, ActorLogging, Props}
import com.socrates.extremestartup.Game.{GetPlayerHistory, NoAnswer, SuccessfulAnswer, UnsuccessfulAnswer}
import com.socrates.extremestartup.Player.{PlayerHistory, Response}
import play.api.libs.ws.ahc.StandaloneAhcWSClient

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}


class Player(id: String, name: String, baseUrl: String, wsClient: StandaloneAhcWSClient)
  extends Actor
    with PlayerGateway
    with ActorLogging {

  private var history = List.empty[Response]

  override def receive: Receive = {

    case query: Query =>

      import query._

      log.info("Question Received!")

      val game = sender()

      call(baseUrl, s"$hash $question", wsClient).onComplete {
        case Success(answer) if answer.toUpperCase == expectedAnswer.toUpperCase =>
          log.info(s"Answer Successful! Question($question) -> $answer")
          game ! SuccessfulAnswer(id)
          history = history.::(Response(formatter.format(new Date()), hash, "SUCCESSFUL"))

        case Success(answer) if answer.toUpperCase != expectedAnswer.toUpperCase =>
          log.info(s"Answer UnSuccessful! Question($question) Expected(${expectedAnswer.toUpperCase}) Got(${answer.toUpperCase})")
          game ! UnsuccessfulAnswer(id)
          history = history.::(Response(formatter.format(new Date()), hash, "UNSUCCESSFUL"))
        case Failure(e) =>
          log.info(s"Player failed to answer with exception message ${e.getMessage}")
          game ! NoAnswer(id)
          history = history.::(Response(formatter.format(new Date()), hash, "CRASH"))
      }

    case GetPlayerHistory(playerId) =>
      sender() ! PlayerHistory(history)
  }

  private val formatter = new SimpleDateFormat("hh:mm:ss")

}

object Player {

  def props(id: String, name: String, ip: String, wsClient: StandaloneAhcWSClient): Props =
    Props(new Player(id, name, ip, wsClient))


  case class PlayerHistory(responses: List[Response])

  case class Response(time: String, questionHash: String, result: String)

}
