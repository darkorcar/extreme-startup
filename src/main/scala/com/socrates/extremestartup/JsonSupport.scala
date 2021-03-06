package com.socrates.extremestartup

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.socrates.extremestartup.Game.{Score, Scores}
import com.socrates.extremestartup.Player.{PlayerHistory, Response}
import spray.json._

trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val scoreFormat = jsonFormat3(Score)
  implicit val scoresFormat = jsonFormat1(Scores)

  implicit val responseFormat = jsonFormat3(Response)
  implicit val playerHistoryFormat = jsonFormat1(PlayerHistory)

}