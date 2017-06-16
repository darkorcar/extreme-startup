package com.socrates.extremestartup

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.socrates.extremestartup.Game.{Score, Scores}
import spray.json._

trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val scoreFormat = jsonFormat2(Score)
  implicit val scoresFormat = jsonFormat1(Scores)
}