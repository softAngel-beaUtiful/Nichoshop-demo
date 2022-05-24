package com.nichoshop.models

import com.nichoshop.Environment.driver.simple._
import com.nichoshop.models.helpers._

import scala.slick.lifted.Tag

/**
 * Created by Evgeny Zhoga on 10.10.15.
 */
case class ProductBidEntity(
                    override val id: Option[Int] = None,
                    auctionId: Int,
                    creationTime: Long = System.currentTimeMillis(),
                     userId: Int,
                     bid: RichMoney
                    ) extends IdAsPrimaryKey

class ProductBids(tag:Tag) extends TableWithIdAsPrimaryKey[ProductBidEntity](tag, "product_bids") {
  def auctionId = column[Int]("auction_id")
  def creationTime = column[Long]("creation_time")
  def userId = column[Int]("user_id")

  def bidAmount = column[Int]("amount")
  def bidCurrencyId = column[Int]("currency_id")
  def bid = (bidAmount, bidCurrencyId) <> (RichMoney.tupled, RichMoney.unapply)

  def * = ( id.?, auctionId, creationTime, userId, bid) <> ( ProductBidEntity.tupled, ProductBidEntity.unapply )
}

object ProductBids extends QueryForTableWithIdAsPrimaryKey[ProductBidEntity, ProductBids](TableQuery[ProductBids]) {

}
