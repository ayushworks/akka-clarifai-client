package com.ayush

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.ayush.actor.{InputImageList, OutputGrouping}
import spray.json._

case class Error(message: String)

trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {

  implicit val imageFormat = jsonFormat1(InputImageList) // contains Set[String]
  implicit val resultFormat = jsonFormat1(OutputGrouping) // contains Map[String,String]

}
