package com.socrates.extremestartup

import akka.actor.{Actor, ActorLogging, Props}


class User(name: String, ip: String) extends Actor with ActorLogging {

  log.info("I've been created!")

  override def receive: Receive = {
    case _ =>
      log.info("I GOt something")
  }
}

object User {

  def props(name: String, ip: String): Props =
    Props(new User(name, ip))
}
