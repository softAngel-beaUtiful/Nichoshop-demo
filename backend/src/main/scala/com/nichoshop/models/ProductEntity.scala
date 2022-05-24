package com.nichoshop.models

import com.nichoshop.Environment.driver.simple._
import com.nichoshop.models.helpers._

import scala.slick.lifted.Tag

/**
 * Created by Evgeny Zhoga on 27.06.15.
 */
case class ProductEntity
(
  override val id: Option[Int],
  sellerId: Int,
  catId: Int,
  title: String,
  description: String,
/*
  subtitle: String,
  itemCondition: Byte,
  conditionDesc: Option[String],
  images: String,
  fixedPrice: Boolean,
  startingPrice: Int,
  nowPrice: Option[Int] = None,
  bestOffer: Boolean = false,
  quantity: Int,
  duration: Int,
  credit: Boolean = true,
  paypal: Boolean = false,
  bitcoin: Boolean = false,
  gift: Boolean = false,
  returns: Boolean = false,
  returnsDetails: Option[String] = None,
  location: String,
  domesticService: Byte = 1,
  domesticCost: Option[Int] = None,
  domesticCollect: Boolean = false,
  internationalService: Option[Byte] = None,
  internationalCost: Option[Int] = None,
  postTo: Option[String] = None,
  state: Byte = 0,
*/
  creationTime: Long
  ) extends IdAsPrimaryKey

class Products(tag:Tag) extends TableWithIdAsPrimaryKey[ProductEntity](tag, "products_1") {
  def sellerId = column[Int]("seller_id")
  def catId = column[Int]("cat_id")
  def title = column[String]("title")
  def description = column[String]("description")
  def creationTime = column[Long]("creation_time")

  def * = (id.?, sellerId, catId, title, description, creationTime) <>(ProductEntity.tupled, ProductEntity.unapply)
}

object Products extends QueryForTableWithIdAsPrimaryKey[ProductEntity, Products](TableQuery[Products]) {
  def getByCategoryId(catId: Int): List[ProductEntity] = DB.read { implicit session =>
    query.filter(_.catId === catId).list
  }
}