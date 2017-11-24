package scala.myakka.demo

import akka.actor.{Actor, ActorSystem, Props}
import akka.event.Logging

/**
  * Created by Honda on 2017/11/17.
  */
object DefaultCreatedActor {

  def main(args: Array[String]): Unit = {

    //
    // ActorSystem is the entry to create and query Actor
    //
    val system = ActorSystem("StringSystem")

    val stringActor = system.actorOf( Props[StringActor] , name="Stringctor")

    stringActor!"Create Actor with default consturctor"
    system.stop(stringActor)
  }
}


//
// Create Actor Method 1 :Defined actor through extends Actor and implement receive method
//

class StringActor extends Actor
{
  val log = Logging(context.system,this)
  def receive =
  {
    case s:String => log.info("received message:" + s)
    case _ => log.info("received unknown message")
  }
}
