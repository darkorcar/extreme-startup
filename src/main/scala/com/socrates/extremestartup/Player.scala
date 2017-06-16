package com.socrates.extremestartup

import akka.actor.{Actor, ActorLogging, Props}


class Player(id: Int, name: String, ip: String) extends Actor with ActorLogging {

  log.info("I've been created!")

  override def receive: Receive = {
    case _ =>
      log.info("I GOt something")
  }
}

object Player {

  def props(id: Int, name: String, ip: String): Props =
    Props(new Player(id, name, ip))
}
