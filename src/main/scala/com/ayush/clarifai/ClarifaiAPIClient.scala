package com.ayush.clarifai

import java.util

import clarifai2.api.{ClarifaiBuilder, ClarifaiClient}
import clarifai2.dto.input.{ClarifaiImage, ClarifaiInput}
import clarifai2.dto.model.output.ClarifaiOutput
import clarifai2.dto.prediction.Concept
import scala.collection.JavaConverters._
import scala.collection.mutable.ListBuffer

/*
object Test extends App {
  println(ClarifaiGeneralModelPredictor.recognize("https://upload.wikimedia.org/wikipedia/commons/7/7b/Acer_Aspire_8920_Gemstone.jpg"))
}
*/

object ClarifaiAPIClient {

  val KEY = "e91df05310eb45219df42147fd87ef6d";
  val client : ClarifaiClient = new ClarifaiBuilder(KEY).buildSync();
}

object ClarifaiGeneralModelPredictor {

  import ClarifaiAPIClient._

  def recognize(url: String) : List[String] = {
    var resultList = ListBuffer[String]()

    if(url.isEmpty) {
      List.empty
    }
    else {
      val predictionResults : util.List[ClarifaiOutput[Concept]] = client.getDefaultModels.generalModel.predict
                    .withInputs(ClarifaiInput.forImage(ClarifaiImage.of(url))).executeSync.get;

      val predictionResultList = predictionResults.asScala.toList
      predictionResultList.map {
        predictionResult =>
          val concepts : List[Concept] = predictionResult.data().asScala.toList
          concepts.map {
            concept => resultList.+=(concept.name())
          }
      }
      resultList.toList
    }
  }

  
}
