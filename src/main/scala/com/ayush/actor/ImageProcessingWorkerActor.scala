package com.ayush.actor

import akka.actor.{Actor, PoisonPill}
import com.ayush.clarifai.{ClarifaiGeneralModelPredictor}

class ImageProcessingWorkerActor extends Actor{

  def receive = {
    case Image(url) => workingHard(url)
  }

  def workingHard(url : String) = {
    val result = ClarifaiGeneralModelPredictor.recognize(url)
    sender ! SingleImageResult(url, result)
    //kill self when work done
    self ! PoisonPill
  }
}
