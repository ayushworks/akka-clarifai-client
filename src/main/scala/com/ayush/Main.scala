package com.ayush

import scala.concurrent.Future

import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.stream.ActorMaterializer
import akka.util.Timeout

import com.typesafe.config.{ Config, ConfigFactory }

object Main extends App with ReqTimeout {

  val config = ConfigFactory.load()

  //retrieve the host and a port from config

  val host = config.getString("http.host")
  val port = config.getInt("http.port")

  implicit val actorSystem = ActorSystem() //create the actor system

  implicit val executionContext = actorSystem.dispatcher

  val api = new RestApi(actorSystem, timeout(config)).routes

  implicit val materializer = ActorMaterializer()
  val bindingFuture: Future[ServerBinding] =
    Http().bindAndHandle(api, host, port) //Starts the HTTP server

  val log =  Logging(actorSystem.eventStream, "clarifai-api")
  bindingFuture.map { serverBinding =>
    log.info(s"RestApi bound to ${serverBinding.localAddress} ")
  }.onFailure {
    case ex: Exception =>
      log.error(ex, "Failed to bind to {}:{}!", host, port)
      actorSystem.terminate()
  }

}

trait ReqTimeout {

  import scala.concurrent.duration._

  def timeout(config: Config): Timeout = {
    val t = config.getString("akka.http.server.request-timeout")
    val d = Duration(t)
    FiniteDuration(d.length, d.unit)
  }
}