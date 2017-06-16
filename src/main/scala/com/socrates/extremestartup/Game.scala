package com.socrates.extremestartup

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import com.socrates.extremestartup.Game.{GetScores, RegisterPlayer, Score}


class Game extends Actor with ActorLogging {

  private var users: Seq[ActorRef] = List.empty[ActorRef]

  override def receive: Receive = {

    case RegisterPlayer(name, ip) =>
      log.info(s"User Register Request $name - $ip")
      val newUser = context.actorOf(User.props(name, ip), s"Player-${users.size}")
      users = users :+ newUser

    case GetScores =>
      log.info("GetScores received")
      sender() ! List(Score("blah", 3))


    case _ => log.info("I got a msg")
  }
}

object Game {

  def props(): Props = Props(new Game())

  case class RegisterPlayer(name: String, ip: String)

  case class GetScores()

  case class Score(name: String, points: Int)

}
