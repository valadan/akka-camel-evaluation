package org.rbudzko.ace

import akka.actor.ActorSystem
import akka.pattern.ask
import akka.util.Timeout
import org.rbudzko.ace.roundtrip.RoundTrip
import org.slf4j.LoggerFactory

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

object Main extends App {
  implicit val log = LoggerFactory.getLogger(classOf[App])
  implicit val system = ActorSystem.create("main-system")
  implicit val timeout = Timeout(1 second)

  RoundTrip.build ? "Welcome!" map ($ => log.info("Round trip responded with {}", $))
}