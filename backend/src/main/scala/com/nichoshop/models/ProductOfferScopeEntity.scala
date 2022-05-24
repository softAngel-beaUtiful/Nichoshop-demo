package com.nichoshop.models

import com.nichoshop.Environment.driver.simple._
import com.nichoshop.models.helpers._

import scala.slick.lifted.Tag

/**
 * Created by Evgeny Zhoga on 24.10.15.
 */
case class ProductOfferScopeEntity(
                             override val id: Option[Int] = None,
                             productId: Int,
                             variantId: Int,
                             startTime: Long,
                             endTime: Option[Long],
                             closed: Boolean = false
                               ) extends IdAsPrimaryKey

class ProductOfferScopes(tag:Tag) extends TableWithIdAsPrimaryKey[ProductOfferScopeEntity](tag, "product_offer_scopes") {
  def productId = column[Int]("product_id")
  def variantId = column[Int]("variant_id")
  def startTime = column[Long]("start_time")
  def endTime = column[Option[Long]]("end_time")
  def closed = column[Boolean]("closed")

  def * = ( id.?, productId, variantId, startTime, endTime, closed) <> ( ProductOfferScopeEntity.tupled, ProductOfferScopeEntity.unapply )
}

object ProductOfferScopes extends QueryForTableWithIdAsPrimaryKey[ProductOfferScopeEntity, ProductOfferScopes](TableQuery[ProductOfferScopes]) {

}
