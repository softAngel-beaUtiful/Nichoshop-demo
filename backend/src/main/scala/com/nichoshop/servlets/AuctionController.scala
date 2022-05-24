package com.nichoshop.servlets

import com.nichoshop.marshalling.Marshallers
import com.nichoshop.services.Services
import com.nichoshop.servlets.AuctionController._
import org.slf4j.LoggerFactory

/**
 * Created by Evgeny Zhoga on 11.10.15.
 */
class AuctionController extends customer.CustomerController with Json {
  private val log = LoggerFactory.getLogger(getClass)
  override def name: String = "customer/auction"

  get("/list") {
    Services.authService.withUser { user =>() =>
      val auctions = Services.auction.getLiveAuctions(params.get("productId").map(_.toInt))
      Marshallers.toJson(auctions, user)
    }
  }
  post("/list/:auctionId/bid") {
    val bid = parsedBody.extract[Bid]
    Services.authService.withUser { user =>() =>
      Services.auction.addBid(params("auctionId").toInt, user.getId, bid.amount)
      Marshallers.ok()
    }
  }
}

object AuctionController {
  case class Bid(amount: Int)
}
