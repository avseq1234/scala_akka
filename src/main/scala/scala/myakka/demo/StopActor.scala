package scala.myakka.demo

import akka.actor.Actor.Receive
import akka.event.Logging
import akka.actor.{Actor, ActorSystem, Props}

/**
  * Created by Honda on 2017/11/23.
  */
object StopActor {

  def main(args: Array[String]): Unit = {
    val system = ActorSystem("StringSystem")

    val contextActor = system.actorOf(Props[ContextActor],name="ContextActor")
    contextActor!"creating Actors with implicit val context"
  }

}

class StirngActor extends Actor {
  val log = Logging(context.system, this)

  override def receive : Receive = {
    case s: String => log.info("received message:" + s)
    case _ => log.info("received unknow message")
  }
}

class ContextActor extends Actor {
  val log = Logging(context.system, this)

  val stringActor = context.actorOf(Props[StringActor], name = "StringActor")

  override def receive: Receive = {
    case s: String => {
      log.info("receive message:" + s)
      stringActor ! s
      context.stop(stringActor)
    }
    case _ => log.info("received unknow message")
  }

  override def postStop(): Unit = {
    log.info("postStop in contextActor")
  }
}


