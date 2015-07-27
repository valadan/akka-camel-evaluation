package org.rbudzko.ace.roundtrip

import akka.actor.Actor
import akka.camel.{CamelMessage, Consumer}
import akka.event.Logging

private[roundtrip] class ConsumingWorker extends Actor with Consumer {
  private val log = Logging.getLogger(context.system, this)

  override def endpointUri = "direct-vm://output"

  override def receive: Receive = {
    case CamelMessage(body, headers) =>
      log.info("Completed round trip for {}.", body)
      sender() ! CamelMessage("Consumed!", headers)
    case _ =>
  }
}
