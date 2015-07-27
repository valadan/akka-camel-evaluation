package org.rbudzko.ace

import akka.actor.Actor
import akka.camel.{CamelMessage, Consumer}
import akka.event.Logging

class ConsumingWorker(endpoint: String) extends Actor with Consumer {
  private val log = Logging.getLogger(context.system, this)

  override def endpointUri = endpoint

  override def receive: Receive = {
    case CamelMessage(body, headers) =>
      log.info("Completed round for {} with {}.", endpoint, body)
      sender() ! CamelMessage("Consumed!", headers)
    case _ =>
  }
}
