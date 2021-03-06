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
import com.socrates.extremestartup.Game._
import com.socrates.extremestartup.Player.PlayerHistory
import play.api.libs.ws.WSClientConfig
import play.api.libs.ws.ahc.{AhcWSClientConfig, StandaloneAhcWSClient}

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.language.postfixOps
import scala.util.{Failure, Success}

object WebServer extends App with JsonSupport {

  implicit val system = ActorSystem("game-system")

  implicit private val materializer = ActorMaterializer()

  implicit private val executionContext = system.dispatcher

  private val log = Logging(system, getClass.getName)


  val wsClient = createWsClient()

  val game: ActorRef = system.actorOf(Game.props(wsClient), "GameActor")

  val routes: Route =

    path("register") {
      post {
        formFields('name, 'baseUrl) { (name, baseUrl) =>
          log.info(s"Register Request: $name -> $baseUrl")
          implicit val timeout = Timeout(5.seconds)
          val playerId: Future[String] = (game ? RegisterPlayer(name, baseUrl)).mapTo[String]
          complete(StatusCodes.Created, playerId)
        }
      }
    } ~
      path("scores") {
        get {
          pathEndOrSingleSlash {
            implicit val timeout = Timeout(5.seconds)
            val scores: Future[Scores] = (game ? GetScores).mapTo[Scores]
            complete(StatusCodes.OK, scores)
          }
        }
      } ~
      path("scores" / Segment) { playerId:String =>
        get {
          pathEndOrSingleSlash {
            implicit val timeout = Timeout(5.seconds)
            val history: Future[PlayerHistory] = (game ? GetPlayerHistory(playerId)).mapTo[PlayerHistory]
            complete(StatusCodes.OK, history)
          }
        }
      }~
      path("admin" / "start") {
        put {
          pathEndOrSingleSlash {
            log.info("Start game")
            game ! StartGame
            complete(StatusCodes.OK)
          }
        }
      } ~
      path("admin" / "finish") {
        put {
          pathEndOrSingleSlash {
            log.info("Finish game")
            game ! StartGame
            complete(StatusCodes.OK)
          }
        }
      } ~
      path("home") {
        getFromResource("view/index.html")
      } ~ pathPrefix("home") {
      getFromResourceDirectory("view")
    }


  Http().bindAndHandle(routes, "localhost", 1234).onComplete {
    case Success(_) =>
      def bold_green(text: String): String = Console.BOLD + Console.GREEN + text + Console.RESET

      log.info(s"Listening on HTTP Port: ${bold_green(1234.toString)}")
    case Failure(ex) =>
      log.error(ex.getMessage)
      System.exit(1)
  }


  private def createWsClient() = {
    val config = AhcWSClientConfig(
      wsClientConfig = WSClientConfig(
        connectionTimeout = 2 seconds,
        requestTimeout = 2 seconds
      )
    )

    StandaloneAhcWSClient(config)
  }
}
