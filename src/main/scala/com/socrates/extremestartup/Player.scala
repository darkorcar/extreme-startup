package com.socrates.extremestartup

import akka.actor.{Actor, ActorLogging, Props}
import com.socrates.extremestartup.Game.{NoAnswer, Query, SuccessfulAnswer, UnsuccessfulAnswer}

import scala.util.Random


class Player(id: Int, name: String, ip: String) extends Actor with ActorLogging {

  log.info("I've been created!")

  override def receive: Receive = {

    case Query(question,expectedAnswer) =>
      log.info("Question Received!")
      Random.nextInt(3) match {
        case 0 => sender() ! SuccessfulAnswer(id)
        case 1 => sender() ! UnsuccessfulAnswer(id)
        case 2 => sender() ! NoAnswer(id)
      }

  }
}

object Player {

  def props(id: Int, name: String, ip: String): Props =
    Props(new Player(id, name, ip))

}
