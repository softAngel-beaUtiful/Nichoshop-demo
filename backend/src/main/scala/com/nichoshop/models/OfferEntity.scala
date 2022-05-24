package com.nichoshop.models

import com.nichoshop.Environment.driver.simple._
import com.nichoshop.models.helpers._

import scala.slick.lifted.Tag

/**
 * Created by Evgeny Zhoga on 24.10.15.
 */
case class OfferEntity(
                  override val id: Option[Int] = None,
                  productOfferScopeId: Int,
                  userId: Int,
                  offerPrice: RichMoney,
                  offerQty: Int = 1,
                  message: Option[String],
                  timestamp: Long = System.currentTimeMillis(),
                  state: String
                  ) extends IdAsPrimaryKey

class Offers(tag:Tag) extends TableWithIdAsPrimaryKey[OfferEntity](tag, "offers") {
  def productOfferScopeId = column[Int]("product_offer_scope_id")
  def userId = column[Int]("user_id")
  def offerPriceAmount = column[Int]("offer_price")
  def offerPriceCurrencyId = column[Int]("currency_id")
  def offerQty = column[Int]("offer_qty")
  def message = column[Option[String]]("message")
  def timestamp = column[Long]("timestamp")
  def state = column[String]("state")

  def offerPrice = (offerPriceAmount, offerPriceCurrencyId) <> (RichMoney.tupled, RichMoney.unapply)

  def * = ( id.?, productOfferScopeId, userId, offerPrice, offerQty, message, timestamp, state) <> ( OfferEntity.tupled, OfferEntity.unapply )
}

object Offers extends QueryForTableWithIdAsPrimaryKey[OfferEntity, Offers](TableQuery[Offers]) {
  object States {
    val open = "open"
    val accepted = "accepted"
    val rejected = "rejected"
  }
}
