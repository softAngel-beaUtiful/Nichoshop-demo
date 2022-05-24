package com.nichoshop.models

import com.nichoshop.Environment.driver.simple._
import com.nichoshop.model.dto.ProductDto
import com.nichoshop.models.helpers._

import scala.slick.lifted.Tag

/**
 * Created by Evgeny Zhoga on 10.10.15.
 */
case class AuctionEntity(
                    override val id: Option[Int] = None,
                    productId: Int,
                    variantId: Int,
                    product: ProductDto,
                    startPrice: RichMoney,
                    bidStep: Int = 0, // looks like not required
                    startTime: Long,
                    finishTime: Long
                    ) extends IdAsPrimaryKey

object AuctionImplicits {
  implicit val schema = ProductDto.getClassSchema
  implicit val avroMapper = avroBasedColumnType[ProductDto]
}

class Auctions(tag:Tag) extends TableWithIdAsPrimaryKey[AuctionEntity](tag, "auctions") {
  import AuctionImplicits._

  def productId = column[Int]("product_id")
  def variantId = column[Int]("variant_id")
  def product = column[ProductDto]("product")
  def startPriceAmount = column[Int]("start_price")
  def startPriceCurrencyId = column[Int]("currency_id")
  def bidStep = column[Int]("bid_step")
  def startTime = column[Long]("start_time")
  def finishTime = column[Long]("finish_time")

  def startPrice = (startPriceAmount, startPriceCurrencyId) <> (RichMoney.tupled, RichMoney.unapply)

  def * = ( id.?, productId, variantId, product, startPrice, bidStep, startTime, finishTime) <> ( AuctionEntity.tupled, AuctionEntity.unapply )
}

object Auctions extends QueryForTableWithIdAsPrimaryKey[AuctionEntity, Auctions](TableQuery[Auctions]) {

}
