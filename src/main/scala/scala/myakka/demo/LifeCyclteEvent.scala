package scala.myakka.demo


import akka.event.Logging

import akka.actor.{Actor, ActorSystem, Props}

/**
  * Created by Honda on 2017/11/23.
  */
object LifeCyclteEvent {
  def main(args: Array[String]): Unit = {
    val system = ActorSystem("StrngSystem")
    val stringActor = system.actorOf(Props[StringActor],name="StringActor")
    stringActor!"Creating Actors with default constructor"
    stringActor!123
    system.terminate()

  }

  class StringActor extends Actor {
    val log = Logging(context.system, this)

    override def preStart(): Unit = {
      log.info("preStart method in StringActor")
    }

    override def postStop(): Unit = {
      log.info("postStop method in StringActor")
    }

    override def unhandled(message: Any): Unit = {
      log.info("unhandled method in StringActor")
      super.unhandled(message)
    }

    override def receive: Receive = {
      case s: String => log.info("received message:" + s)
    }
  }

}
