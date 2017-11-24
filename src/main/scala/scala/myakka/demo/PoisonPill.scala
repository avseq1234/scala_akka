package scala.myakka.demo

import akka.actor.{Actor, ActorSystem, Props}
import akka.actor.Actor.Receive
import akka.event.Logging

/**
  * Created by Honda on 2017/11/23.
  */
object PoisonPill {
  def main(args: Array[String]): Unit = {
    val system = ActorSystem("StringSystem")
    val contextActor = system.actorOf(Props[ContextActor],name="ContextAcotr")
    contextActor!"Creating actos with imp;licit val context"

    contextActor!PoisonPill
    system.terminate()
  }

}

class StringActor extends Actor {
  val log = Logging(context.system, this)

  override def receive: Receive = {
    case s: String => log.info("received message:" + s)
    case _ => log.info("received unknown message")
  }

  override def postStop(): Unit = {
    log.info("postStop in StringActor")
  }
}

class ContextActor extends Actor {
  val log = Logging(context.system, this)

  val stringActor = context.actorOf(Props[StringActor], name = "StringActor")

  def receive = {
    case s: String => {
      log.info("received message:" + s)
      stringActor ! s
    }
    case _ => log.info("received unknown messsage")
  }

  override def postStop(): Unit = {
    log.info("postStop in ContextActor")
  }
}
