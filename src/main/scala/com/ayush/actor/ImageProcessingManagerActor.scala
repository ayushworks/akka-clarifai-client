package com.ayush.actor

import akka.actor.SupervisorStrategy.Stop
import akka.actor.{Actor, ActorRef, OneForOneStrategy, PoisonPill, Props}

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global


case class Image(url: String)
case class Result(map : Map[String,String])
case class SingleImageResult(url: String, list: List[String])
case object TimedOut


class ImageProcessingManagerActor(images: Set[String] ,father: ActorRef) extends Actor{

  var remaningWork = images.size
  var results = scala.collection.mutable.Map[String,Set[String]]()

  val computerKey = "computer"
  val alcoholKey = "alcohol"
  val documentsKey = "documents"

  val alcoholList = Seq("wine", "bar", "alcohol", "winery")
  val computerList = Seq("computer", "laptop", "monitor", "screen")
  val documentList = Seq("file", "document")

  results(computerKey) = Set.empty
  results(alcoholKey) = Set.empty
  results(documentsKey) = Set.empty



  override def preStart(): Unit = {
    countdown
    images.foreach(url => worker ! Image(url))

    def worker() : ActorRef = {
      context.actorOf(Props(new ImageProcessingWorkerActor))
    }
  }

  override val supervisorStrategy  = OneForOneStrategy() {
    case _ => {
      remaningWork = remaningWork-1
      if(remaningWork < 1) {
        father ! OutputGrouping(results.toMap)
      }
      Stop
    }
  }

  def countdown() = {
    context.system.scheduler.scheduleOnce((images.size*20) second, self, TimedOut)
  }

  def receive = {
    case SingleImageResult(url, singleImageResultList) => oneFinished(url, singleImageResultList)
    case TimedOut => self ! PoisonPill
  }

  def oneFinished(url: String, list: List[String]) = {
    remaningWork = remaningWork-1
    list.map {
      element =>
        if(alcoholList.contains(element)){
          results += (alcoholKey -> results(alcoholKey).+(url))
        }
        if(computerList.contains(element)){
          results += (computerKey -> results(computerKey).+(url))
        }
        if(documentList.contains(element)){
          results += (documentsKey -> results(documentsKey).+(url))
        }
    }
    if(remaningWork < 1) {
      father ! OutputGrouping(results.toMap)
    }
  }
}
