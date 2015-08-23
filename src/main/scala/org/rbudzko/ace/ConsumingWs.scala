package org.rbudzko.ace

import akka.actor.PoisonPill
import akka.event.Logging
import akka.http.scaladsl.model.ws.TextMessage
import akka.stream.actor.ActorPublisher
import org.rbudzko.ace.trips.SearchItem

class ConsumingWs extends ActorPublisher[SearchItem] {

  private val logger = Logging.getLogger(context.system, this)
  private var i = 0

  override def receive: Receive = {
    case x: TextMessage.Strict =>
      i = i + 1
      logger.info("New message arrived. Counter is [{}].", i)

      if (i > 5) {
        logger.info("I'm tired. Completing.")
        onComplete()
        self ! PoisonPill
        context.become(zombie)
      }
    case any: Any =>
      println(any)
  }

  def zombie: Receive = {
    case _ =>
  }
}
