package scala.myakka.demo

import akka.event.Logging
import akka.actor._
import akka.actor.{Actor, ActorSystem, Props}

/**
  * Created by Honda on 2017/11/17.
  */
object CreateActorThroughContext {
  def main(args: Array[String]): Unit = {
    val system = ActorSystem("StringSystem")
    val contextActor = system.actorOf(Props[ContextActor],name="ContextActor")

    contextActor!"Creating Actors with implicit val context"

    system.stop(contextActor)

  }



}

class StringActor extends Actor{
  val log = Logging(context.system, this)

  override def receive: Receive =
  {
    case s:String => log.info("receive message:" + s)
    case _ => log.info("received unknown message")
  }
}

class ContextActor extends Actor
{
  val log = Logging(context.system,this)
  var stringActor = context.actorOf( Props[StringActor],name="StringActor")

  override def receive: Receive =
  {
    case s:String =>
      {
        log.info("receive message:" + s)
        stringActor!s
      }
    case _ => log.info("received unknow message")


  }
}