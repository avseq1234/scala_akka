package scala.myakka.demo

import akka.actor.SupervisorStrategy._
import akka.actor.{Actor, ActorLogging, ActorSystem, OneForOneStrategy, Props}
import akka.util.Timeout
import akka.pattern._

import scala.concurrent.Await
import scala.concurrent.duration._

/**
  * Created by Honda on 2017/11/23.
  */

case class NormalMessage()

class ChildActor extends Actor with ActorLogging{
  var state: Int = 0

  override def preStart(): Unit =
  {
    log.info("Start ChildActor,hashcode=" + this.hashCode())
  }

  override def postStop(): Unit =
  {
    log.info("Stop ChildActor,hashcode=" + this.hashCode())
  }

  override def receive: Receive =
  {
    case value:Int =>
      {
        if( value <= 0 )
          throw new ArithmeticException("less than zero")
        else
          state = value
      }

    case result:NormalMessage =>
      {
        sender! state
      }
    case ex:NullPointerException =>
      {
        throw new NullPointerException("Null Point")
      }
    case _ => throw new IllegalArgumentException("Illegal Parameters")
  }

}

class SupervisorActor extends Actor with ActorLogging
{
  val childActor = context.actorOf(Props[ChildActor],name="ChildActor")
  override val supervisorStrategy = OneForOneStrategy(10,10 seconds)
  {
    case _ : ArithmeticException => Resume
    case _ : NullPointerException => Restart
    case _ : IllegalArgumentException => Stop
    case _: Exception =>Escalate
  }

  override def receive: Receive =
  {
    case msg:NormalMessage =>
      {
        childActor.tell(msg,sender)
      }

    case msg:Object =>
      {
        childActor!msg
      }
  }
}

object OneForOneStragegyDemo {
  def main(args: Array[String]): Unit = {
    val system = ActorSystem("FaultToLeranceSystem")
    val log = system.log

    val supervisor = system.actorOf(Props[SupervisorActor],name="SupervisorActor")

    supervisor! -5
    implicit val timeout = Timeout(10 seconds)
    var future = (supervisor ? new NormalMessage).mapTo[Int]
    var resultMsg = Await.result(future,timeout.duration)
    log.info("Result:" + resultMsg)

    supervisor! new NullPointerException
    future = (supervisor ? new NormalMessage).mapTo[Int]
    resultMsg = Await.result(future,timeout.duration)
    log.info("Result:" + resultMsg)

    supervisor ? "String"

    system.terminate()

  }
}
