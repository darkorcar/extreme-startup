package com.socrates.extremestartup

import akka.actor.{ActorRef, ActorSystem}
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.pattern.ask
import akka.stream.ActorMaterializer
import akka.util.Timeout
import com.socrates.extremestartup.Game.{GetScores, RegisterPlayer, Scores}

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.language.postfixOps
import scala.util.{Failure, Success}

object WebServer extends App with JsonSupport {

  implicit val system = ActorSystem("actor-system")

  implicit private val materializer = ActorMaterializer()

  implicit private val executionContext = system.dispatcher

  private val log = Logging(system, getClass.getName)

  val game: ActorRef = system.actorOf(Game.props(), "GameActor")

  val routes: Route =

    path("register") {
      post {
        formFields('name, 'ip) { (name, ip) =>
          log.info(s"Register Request: $name -> $ip")
          implicit val timeout = Timeout(5.seconds)
          val playerId: Future[String] = (game ? RegisterPlayer(name, ip)).mapTo[String]
          complete(StatusCodes.Created, playerId)
        }
      }
    } ~
      path("scores") {
        get {
          pathEndOrSingleSlash {
            log.info("Get scores")
            implicit val timeout = Timeout(5.seconds)
            val scores: Future[Scores] = (game ? GetScores).mapTo[Scores]
            complete(StatusCodes.OK, scores)
          }
        }
      }


  Http().bindAndHandle(routes, "localhost", 1234).onComplete {
    case Success(_) =>
      def bold_green(text: String): String = Console.BOLD + Console.GREEN + text + Console.RESET

      log.info(s"Listening on HTTP Port: ${bold_green(1234.toString)}")
    case Failure(ex) =>
      log.error(ex.getMessage)
      System.exit(1)
  }

}
