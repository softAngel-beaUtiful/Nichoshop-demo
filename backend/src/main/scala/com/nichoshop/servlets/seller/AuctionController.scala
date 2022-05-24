package com.nichoshop.servlets.seller

import com.nichoshop.marshalling.Marshallers
import com.nichoshop.services.Services
import com.nichoshop.servlets.Json
import org.slf4j.LoggerFactory

/**
  * Created by Evgeny Zhoga on 11.10.15.
  */
class AuctionController extends SellerController with Json {
  import AuctionController._
  private val log = LoggerFactory.getLogger(getClass)

  override def name: String = "seller/auction"

   post("/list") {
     Services.authService.withUser { user =>() =>
       val a = parsedBody.extract[Auction]

       Services.auction.createAuction(a.productId, a.variantId, a.startAt, a.finishAt)

       Marshallers.ok()
     }
   }
 }

object AuctionController {
  case class Auction(productId: Int, variantId: Int, startAt: Long, finishAt: Long)
}
