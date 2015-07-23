package org.rbudzko.ace

import akka.actor.{ActorSystem, Props}
import org.rbudzko.ace.twitter.TwitterWorker

object Main extends App {
  implicit val system = ActorSystem.create("main-system")

  system.actorOf(Props[TwitterWorker]) ! "Start!"
}