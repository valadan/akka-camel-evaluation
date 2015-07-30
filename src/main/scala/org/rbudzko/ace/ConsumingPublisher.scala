package org.rbudzko.ace

import akka.actor.PoisonPill
import akka.camel.{CamelMessage, Consumer}
import akka.event.Logging
import akka.stream.actor.ActorPublisher
import akka.stream.actor.ActorPublisherMessage.{Cancel, Request}

import scala.annotation.tailrec
import scala.collection.JavaConversions._

class ConsumingPublisher(private val url: String) extends ActorPublisher[SearchItem] with Consumer {

  private val logger = Logging.getLogger(context.system, this)
  private var items = List.empty[SearchItem]

  override def endpointUri: String = url

  override def receive: Receive = {
    case CamelMessage(body: SearchResult, headers) =>
      logger.info("New chunk of items arrived.")
      items = items ++ body.items
      logger.info("Current pending item list {}.", items)

      sender() ! CamelMessage("Queued!", headers)

      if (totalDemand > 0) {
        items = passOnNext(items, totalDemand)
        logger.info("Demand handled. Items still pending {}.", items)
      }
    case Request(demand: Long) =>
      logger.info("Demand request of size {}. Items pending {}.", demand, items)
      items = passOnNext(items, demand)
      logger.info("Demand handled. Items still pending {}.", items)
    case Cancel =>
      logger.error("Subscriber cancelled processing remaining {} elements will not be processed.", items.size)
      self ! PoisonPill
    case _ =>
      logger.error("Unknown message. Don't know what to do - PANIC PANIC!")
  }

  @tailrec private def passOnNext(items: List[SearchItem], remainingDemand: Long): List[SearchItem] = {
    if (remainingDemand > 0L && items.nonEmpty) {
      onNext(items.head)
      passOnNext(items.tail, remainingDemand - 1)
    } else {
      items
    }
  }
}
