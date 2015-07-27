package org.rbudzko.ace.httptrip

import akka.actor.Actor
import akka.camel.{CamelMessage, Consumer}
import akka.event.Logging

private[httptrip] class ConsumingWorker extends Actor with Consumer {
  private val log = Logging.getLogger(context.system, this)

  override def endpointUri = "direct-vm://googled"

  override def receive: Receive = {
    case CamelMessage(body, headers) =>
      log.info("Completed http trip for {}.", body)
      sender() ! CamelMessage("Consumed!", headers)
    case _ =>
  }
}
