package org.rbudzko.ace.roundtrip

import akka.actor.Actor
import akka.camel.Producer

private[roundtrip] class ProducingWorker extends Actor with Producer {
  override def endpointUri = "vm:input"
}
