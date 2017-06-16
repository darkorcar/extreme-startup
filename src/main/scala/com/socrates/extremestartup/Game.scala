package com.socrates.extremestartup

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import com.socrates.extremestartup.Game._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.language.postfixOps

class Game extends Actor with ActorLogging {

  private var players = Map.empty[Int, ActorRef]

  private var scores = Map.empty[Int, Score]


  override def receive: Receive = {

    case RegisterPlayer(name, ip) =>
      log.info(s"User Register Request $name - $ip")
      val playerId = players.size
      val newPlayer = context.actorOf(Player.props(playerId, name, ip), s"Player-$playerId")
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
      players.values.foreach { playerRef =>
        playerRef ! Query("question", "expected answer")
      }
      context.system.scheduler.scheduleOnce(10 seconds) {
        self ! GameTick
      }

    case SuccessfulAnswer(playerId) =>
      scorePlayer(playerId, 1)

    case UnsuccessfulAnswer(playerId) =>
      scorePlayer(playerId, -1)

    case NoAnswer(playerId) =>
      scorePlayer(playerId, -2)

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

  def props(): Props = Props(new Game())

  case class RegisterPlayer(name: String, ip: String)

  case object GetScores

  case class Score(name: String, points: Int)

  case class Scores(scores: List[Score])

  case class Query(question: String, expectedAnswer: String)

  case class SuccessfulAnswer(playerId: Int)

  case class UnsuccessfulAnswer(playerId: Int)

  case class NoAnswer(playerId: Int)

  case object StartGame

  case object GameTick

}
