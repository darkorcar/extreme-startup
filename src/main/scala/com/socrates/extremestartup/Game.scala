package com.socrates.extremestartup

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import com.socrates.extremestartup.Game._
import play.api.libs.ws.ahc.StandaloneAhcWSClient

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.language.postfixOps

class Game(wSClient: StandaloneAhcWSClient)
  extends Actor
    with ActorLogging
    with QueryBank {

  private var players = Map.empty[Int, ActorRef]
  private var scores = Map.empty[Int, Score]

  private var round = 0

  override def receive: Receive = {

    case RegisterPlayer(name, baseUrl) =>
      log.info(s"User Register Request $name - $baseUrl")
      val playerId = players.size
      val newPlayer = createPlayer(name, baseUrl, playerId, wSClient)

      players = players + (playerId -> newPlayer)
      scores = scores + (playerId -> Score(name, 0))

      sender() ! s"""{ "playerId" : "$playerId" }"""


    case GetScores =>
      log.info("GetScores received")
      sender() ! Scores(scores.values.toList)

    case StartGame =>
      log.info("Start game")
      context.become(runningGame)
      context.system.scheduler.scheduleOnce(10 seconds) {
        self ! GameTick
      }

  }

  def runningGame: Receive = {
    case GetScores =>
      log.info("GetScores received")
      sender() ! Scores(scores.values.toList)

    case GameTick =>
      val query = nextQuery(round)
      players.values.foreach { playerRef =>
        playerRef ! query
      }
      context.system.scheduler.scheduleOnce(10 seconds) {
        self ! GameTick
      }
      increaseRound

    case SuccessfulAnswer(playerId) =>
      scorePlayer(playerId, 1)

    case UnsuccessfulAnswer(playerId) =>
      scorePlayer(playerId, -1)

    case NoAnswer(playerId) =>
      scorePlayer(playerId, -2)

    case FinishGame =>
      log.info("Finish game")
      context.become(finished)

  }


  def finished: Receive = {
    case GetScores =>
      log.info("GetScores received")
      sender() ! Scores(scores.values.toList)
  }

  private def increaseRound: Unit = round = round + 1

  private def createPlayer(name: String, baseUrl: String, playerId: Int, wsClient: StandaloneAhcWSClient) = {
    context.actorOf(Player.props(playerId, name, baseUrl, wsClient), s"Player-$playerId")
  }

  private def scorePlayer(playerId: Int, points: Int): Unit = {
    scores
      .get(playerId)
      .foreach { score =>
        scores = scores + (playerId -> score.copy(points = score.points + points))
      }
  }
}

object Game {

  def props(wsClient: StandaloneAhcWSClient): Props = Props(new Game(wsClient))

  case class RegisterPlayer(name: String, ip: String)

  case object GetScores

  case class Score(name: String, points: Int)

  case class Scores(scores: List[Score])

  case class SuccessfulAnswer(playerId: Int)

  case class UnsuccessfulAnswer(playerId: Int)

  case class NoAnswer(playerId: Int)

  case object StartGame
  case object FinishGame
  case object GameTick

}
