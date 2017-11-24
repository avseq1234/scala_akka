package scala.myakka.demo

import akka.actor.{Actor, ActorSystem, Props}
import akka.event.Logging
import akka.util.Timeout
import akka.pattern.pipe
import akka.pattern.ask

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._

/**
  * Created by Honda on 2017/11/17.
  */

case class BasicInfo(id:Int,name:String,age:Int)
case class InterestInfo(id:Int,interest:String)
case class Person(baseInfo:BasicInfo , interestInfo:InterestInfo)
object SendAndReceive {
  def main(args: Array[String]): Unit = {
    val system = ActorSystem("Send-And-Receive")
    val combineActor = system.actorOf(Props[CombineActor],name="CombineActor")
    combineActor!12345

    Thread.sleep(2000)
    system.stop(combineActor)
    system.terminate()
  }
}

class BasicInfoActor extends Actor
{
  val log = Logging(context.system, this)

  override def receive: Receive =
  {
    case id:Int =>
      {
        log.info("id=" + id)
        sender!new BasicInfo(id,"John",19)
      }
    case _ => log.info("receive unknow message")
  }
}

class InterestInfoActor extends Actor
{
  val log = Logging(context.system,this)

  override def receive: Receive =
  {
    case id:Int =>
      {
        log.info("id=" + id)
        sender! new InterestInfo(id,"Basketball")
      }
    case _ => log.info("received unknow message")
  }
}

class PersonActor extends Actor
{
  val log = Logging(context.system,this)

  override def receive: Receive =
  {
    case person:Person => log.info("Person=" + person)
    case _ => log.info("received unknow message")

  }
}

class CombineActor extends Actor
{
  implicit val timeout = Timeout(5 seconds)
  val basicInfoActor = context.actorOf(Props[BasicInfoActor],name="BasicInfoActor")
  val interestInfoActor = context.actorOf(Props[InterestInfoActor],name="InterestInfoActor")
  val personActor = context.actorOf(Props[PersonActor],name="PersonActor")

  override def receive: Receive =
  {
    case id:Int =>
      {
        val combineResult:Future[Person] =
          for {
            basicInfo <- ask(basicInfoActor,id).mapTo[BasicInfo]
            interestInfo <- ask(interestInfoActor,id).mapTo[InterestInfo]

          } yield Person(basicInfo,interestInfo)
        pipe(combineResult).to(personActor)
      }

  }
}
