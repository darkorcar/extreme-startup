package com.socrates.extremestartup

import akka.actor.{ActorRef, ActorSystem}
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import com.socrates.extremestartup.Game.{GetScores, RegisterPlayer}
import akka.pattern.ask

import scala.util.{Failure, Success}

object WebServer extends App {

  implicit val system = ActorSystem("actor-system")

  implicit private val materializer = ActorMaterializer()

  implicit private val executionContext = system.dispatcher

  private val log = Logging(system, getClass.getName)

  val game: ActorRef = system.actorOf(Game.props(),"GameActor")

  val routes: Route =

    path("register") {
      post {
        formFields('name, 'ip) { (name, ip) =>
          log.info(s"Register Request: $name -> $ip")
          game ! RegisterPlayer(name,ip)
          complete(StatusCodes.Created, "something")
        }
      }
    } ~
  path("scores") {
    get {
      pathEndOrSingleSlash {
        val scores = game ? GetScores()


        complete(StatusCodes.OK,scores)
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
