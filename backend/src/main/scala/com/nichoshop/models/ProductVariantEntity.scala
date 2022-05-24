package com.nichoshop.models

import com.nichoshop.Environment.driver.simple._
import com.nichoshop.models.helpers._

import scala.slick.lifted.Tag

/**
 * Created by Evgeny Zhoga on 23.08.15.
 */
case class ProductVariantEntity(
  override val id: Option[Int],
  productId: Int,
  title: Option[String],
  description: Option[String],
  condition: String,
  price: RichMoney,
  amount: Int,
  creationTime: Long
  ) extends IdAsPrimaryKey


class ProductVariants(tag:Tag) extends TableWithIdAsPrimaryKey[ProductVariantEntity](tag, "product_variants") {
  def productId = column[Int]("product_id")
  def title = column[String]("title")
  def description = column[String]("description")
  def condition = column[String]("condition")
  def amount = column[Int]("amount")
  def priceAmount = column[Int]("price")
  def priceCurrencyId = column[Int]("currency_id")
  def creationTime = column[Long]("creation_time")

  def price = (priceAmount, priceCurrencyId) <> (RichMoney.tupled, RichMoney.unapply)

  def * = (id.?, productId, title.?, description.?, condition, price, amount, creationTime) <>(ProductVariantEntity.tupled, ProductVariantEntity.unapply)
}

object ProductVariants extends QueryForTableWithIdAsPrimaryKey[ProductVariantEntity, ProductVariants](TableQuery[ProductVariants]) {
  object Condition {
    val `new` = "new"
    val used = "used"
    val all = List(`new`, used)
  }

}