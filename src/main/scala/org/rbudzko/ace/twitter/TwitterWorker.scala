package org.rbudzko.ace.twitter

import akka.actor.Actor

class TwitterWorker extends Actor {
  override def receive: Receive = {
    case msg: Any => println(msg)
  }
}
