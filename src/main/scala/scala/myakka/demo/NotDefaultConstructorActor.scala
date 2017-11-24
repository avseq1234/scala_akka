package scala.myakka.demo

import akka.actor.{Actor, ActorSystem, Props}
import akka.event.Logging

/**
  * Created by Honda on 2017/11/17.
  */
object NotDefaultConstructorActor {
  def main(args: Array[String]): Unit = {

    val system = ActorSystem("StringSystem")

    val stringActor = system.actorOf(Props(new StringActor("StringActor")) , name="sActor")

    stringActor!"Create Actor with non-default constructor"

    system.stop(stringActor)
    system.terminate()
  }


}

class StringActor(var name:String) extends Actor
{
  val log = Logging(context.system,this)

  override def receive: Receive =
  {
    case s:String => log.info("receive message\n" + s)
    case _ => log.info("receive unknow message")
  }
}
