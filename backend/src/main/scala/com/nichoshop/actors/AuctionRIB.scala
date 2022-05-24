package com.nichoshop.actors

import akka.actor.Actor

/**
 * Created by Evgeny Zhoga on 24.10.15.
 *
 * Auction real-time-bidding stateful actor.
 * States:
 * 1. Inactive -> ask others what last bid timestamp.
 * 2. Inactive -> spawn loading actor from DB, accepting broadcast timestamp updates
 *      -> active after loading finish
 * 3. Active
 *
 * States:
 */
class AuctionRIB(auctionId: Int) extends Actor {
  def receive = Actor.emptyBehavior
}

object AuctionRIB {
  class LoadingAuctionRIB(auctionId: Int) extends Actor {
    def receive = Actor.emptyBehavior
  }

  object messages {
    case object Load
    case class Loaded(auction: Int, data: Any)
  }
}
