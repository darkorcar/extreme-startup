package com.socrates.extremestartup

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import com.socrates.extremestartup.Game._

import scala.concurrent.Future


class Game extends Actor with ActorLogging {

  private var players = Map.empty[Int, ActorRef]

  private var scores = Map.empty[Int, Score]


  override def receive: Receive = {

    case RegisterPlayer(name, ip)  =>
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

  }

  def runningGame: Receive = {
    case GetScores =>
      log.info("GetScores received")
      sender() ! Scores(scores.values.toList)

    case _ =>
      sender() ! Future.failed(new RuntimeException)
  }
}

object Game {

  def props(): Props = Props(new Game())

  case class RegisterPlayer(name: String, ip: String)

  case object GetScores

  case class Score(name: String, points: Int)

  case class Scores(scores: List[Score])

  case object StartGame

}
