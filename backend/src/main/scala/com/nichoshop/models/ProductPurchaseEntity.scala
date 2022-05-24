package com.nichoshop.models

import com.nichoshop.Environment.driver.simple._
import com.nichoshop.models.helpers._

import scala.slick.lifted.Tag

/**
 * Created by Evgeny Zhoga on 23.09.15.
 */
case class ProductPurchaseEntity(
                            override val id: Option[Int] = None,
                            reservationId: Int,
                            creationTime: Long = System.currentTimeMillis()
//                            ,item: model.CartItem
                            ) extends IdAsPrimaryKey

class ProductPurchases(tag:Tag) extends TableWithIdAsPrimaryKey[ProductPurchaseEntity](tag, "product_purchases") {

  def reservationId = column[Int]("reservation_id")
  def creationTime = column[Long]("creation_time")

  def * = (id.?, reservationId, creationTime) <>(ProductPurchaseEntity.tupled, ProductPurchaseEntity.unapply)
}

object ProductPurchases extends QueryForTableWithIdAsPrimaryKey[ProductPurchaseEntity, ProductPurchases](TableQuery[ProductPurchases]) {

}