package com.ayush

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.util.Timeout
import scala.concurrent.{ExecutionContext, Future}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import akka.pattern.ask
import akka.util.Timeout
import com.ayush.actor.{ImageProcessingFactoryActor,InputImageList, OutputGrouping}

import scala.concurrent.ExecutionContext

class RestApi(system: ActorSystem, timeout: Timeout)
    extends RestRoutes {

  implicit val requestTimeout = timeout
  implicit def executionContext = system.dispatcher

  def createImageProcessorFactoryActor = system.actorOf(Props[ImageProcessingFactoryActor],"ImageProcessingFactoryActor")
}

trait RestRoutes extends ImageProcessorApi
  with JsonSupport {

  import StatusCodes._

  def routes: Route = route

  val route =
    pathPrefix("images") {
      pathEndOrSingleSlash {
        post {
          entity(as[InputImageList]) { imageSet => // will unmarshal JSON to ImageSet
            onSuccess(processImages(imageSet.images)) { result =>
              complete(OK, result)
            }
          }
        }
      }
    }
}

trait ImageProcessorApi {

  def createImageProcessorFactoryActor() : ActorRef

  implicit def executionContext: ExecutionContext
  implicit def requestTimeout: Timeout

  lazy val factoryActor = createImageProcessorFactoryActor()

  def processImages(images: Set[String]) =
    factoryActor.ask(InputImageList(images)).asInstanceOf[Future[OutputGrouping]]
}