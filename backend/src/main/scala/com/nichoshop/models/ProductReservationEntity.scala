package com.nichoshop.models

import com.nichoshop.Environment.driver.simple._
import com.nichoshop.model.dto._
import com.nichoshop.models.helpers._

import scala.concurrent.duration._
import scala.language.postfixOps
import scala.slick.lifted.Tag

/**
 * Created by Evgeny Zhoga on 21.09.15.
 */
case class ProductReservationEntity(
                          override val id: Option[Int] = None,
                          productId: Int,
                          productVariantId: Int,
                          userId: Int,
                          sellerId: Int,
                          creationTime: Long = System.currentTimeMillis(),
                          reservationPeriodMillis: Long = 5 minutes,
                          qty: Int,
                          price: RichMoney,
                          item: CartItemDto
                          ) extends IdAsPrimaryKey

object ProductReservationImplicits {
  implicit val schema = CartItemDto.getClassSchema
  implicit val avroMapper = avroBasedColumnType[CartItemDto]
}
class ProductReservations(tag:Tag) extends TableWithIdAsPrimaryKey[ProductReservationEntity](tag, "product_reservation") {
  import ProductReservationImplicits._

  def productId = column[Int]("product_id")
  def productVariantId = column[Int]("product_variant_id")
  def userId = column[Int]("user_id")
  def sellerId = column[Int]("seller_id")
  def creationTime = column[Long]("creation_time")
  def reservationPeriodMillis = column[Long]("reservation_period")
  def qty = column[Int]("amount")
  def priceAmount = column[Int]("price")
  def priceCurrencyId = column[Int]("currency_id")
  def item = column[CartItemDto]("product_data")

  def price = (priceAmount, priceCurrencyId) <> (RichMoney.tupled, RichMoney.unapply)
  def * = (id.?, productId, productVariantId, userId, sellerId, creationTime, reservationPeriodMillis, qty, price, item) <>(ProductReservationEntity.tupled, ProductReservationEntity.unapply)

}

object ProductReservations extends QueryForTableWithIdAsPrimaryKey[ProductReservationEntity, ProductReservations](TableQuery[ProductReservations]) {

}