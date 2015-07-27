package org.rbudzko.ace

import akka.actor.Actor
import akka.camel.Producer

class ProducingWorker(endpoint: String) extends Actor with Producer {
  override def endpointUri = endpoint
}
