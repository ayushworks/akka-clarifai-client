package com.ayush.actor

import akka.actor.{Actor, ActorRef, Props}


case class InputImageList(images : Set[String]) {
  require(images.size>0)
}
case class OutputGrouping(groupedImages : Map[String,Set[String]])

class ImageProcessingFactoryActor extends Actor{

  var mySender : ActorRef = null;

  def ProcessImages(images : Set[String], ref: ActorRef) : Unit = {
    mySender = ref;
    //aggregator here
    context.actorOf(Props(new ImageProcessingManagerActor(images, self)))
  }
  def receive = {
    case InputImageList(images) => ProcessImages(images,sender)
    case OutputGrouping(map) => mySender ! OutputGrouping(map)
  }


}
