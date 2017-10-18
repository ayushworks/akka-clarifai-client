package com.ayush

import akka.actor.{ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit}
import com.ayush.actor.{ImageProcessingFactoryActor, InputImageList, OutputGrouping}
import org.scalatest.{BeforeAndAfterAll, FlatSpecLike, Matchers}

class WorkerActorTest(system: ActorSystem)
    extends TestKit(system)
    with ImplicitSender
    with Matchers
    with FlatSpecLike
    with BeforeAndAfterAll {

    def this()= this(ActorSystem("Aggregator"))


    "A FactoryActor" should "return an empty list for each label" in {

      val factoryActor = system.actorOf(Props[ImageProcessingFactoryActor],"factoryActor1")
      factoryActor ! InputImageList(Set[String]("http://www.dream-wallpaper.com/free-wallpaper/nature-wallpaper/dream-homes-1-wallpaper/1280x800/free-wallpaper-9.jpg"))
      expectMsgType[OutputGrouping] should be (OutputGrouping(Map("computer" -> Set(), "alcohol" -> Set(), "documents" -> Set())))
    }

    "A FactoryActor" should "should return an non-empty list" in {

      val factoryActor = system.actorOf(Props[ImageProcessingFactoryActor],"factoryActor2")
      factoryActor ! InputImageList(Set[String]("https://upload.wikimedia.org/wikipedia/commons/7/7b/Acer_Aspire_8920_Gemstone.jpg"))
      expectMsgType[OutputGrouping] should be (OutputGrouping(Map("computer" -> Set("https://upload.wikimedia.org/wikipedia/commons/7/7b/Acer_Aspire_8920_Gemstone.jpg"), "alcohol" -> Set(), "documents" -> Set())))
    }

    "A FactoryActor" should "should handle worker failures" in {

      val factoryActor = system.actorOf(Props[ImageProcessingFactoryActor],"factoryActor3")
      factoryActor ! InputImageList(Set[String]("invalid_url"))
      expectMsgType[OutputGrouping] should be (OutputGrouping(Map("computer" -> Set(), "alcohol" -> Set(), "documents" -> Set())))

    }
}
