package org.rbudzko.ace.httptrip

import akka.actor.Actor
import akka.camel.Producer

private[httptrip] class ProducingWorker extends Actor with Producer {
  override def endpointUri = "vm:googler"
}
