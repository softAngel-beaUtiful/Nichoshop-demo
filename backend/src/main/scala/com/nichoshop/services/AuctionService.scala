package com.nichoshop.services

import com.nichoshop.Environment.driver.simple._
import com.nichoshop.model.dto._
import com.nichoshop.models
import com.nichoshop.models.helpers.DB
import com.nichoshop.services.converters._
import org.slf4j.LoggerFactory

import scala.collection.JavaConversions._
/**
 * Created by Evgeny Zhoga on 11.10.15.
 */
class AuctionService {
  val log = LoggerFactory.getLogger(getClass)

  /**
   * Reserve product variant if it is available (amount > 0)
   *
   * @param productId
   * @param variantId
   * @return
   */
  def createAuction(productId: Int, variantId: Int, startAt: Long, finishAt: Long): Option[AuctionDto] = {
    DB.write { implicit session =>
      models.Products.findById(productId).flatMap {product =>
        models.ProductVariants.findById(variantId).
          filter(_.productId == productId).
          filter(_.amount > 0).
          map { product -> _ }
      }.flatMap {case (product, variant) =>
        // here we reserve 1 item
          val updated = models.ProductVariants.query.
            filter(v =>  (v.id === variant.id) && (v.amount === variant.amount)).
            map(_.amount).
            update(variant.amount - 1)

          if (updated > 0) {
            val a = models.AuctionEntity(
              product = (product, variant),
              productId = product.id.get,
              variantId = variant.id.get,
              startTime = startAt,
              finishTime = finishAt,
              startPrice = variant.getPrice
            )
            Some( a.copy(id = Some(models.Auctions.insert(a))))
          } else None
      }
    }
  }
  def getAuction(auctionId: Int) = {
    DB.read {implicit session =>

      models.Auctions.findById(auctionId).map { auction =>
        val bids: List[models.ProductBidEntity] = models.ProductBids.query.filter(_.auctionId === auction.id).sortBy(_.creationTime).list
        auction.setAttendies(
          // get bids
          bids.map {bid =>
            // convert to model
            AuctionAttendieDto.newBuilder().
              setUserId(bid.userId).
              setTimestamp(bid.creationTime).
              setMaxBid(bid.bid).
              build()
            }.
            // group by user
            groupBy(v => v.getUserId).
            // get last bid from each user
            map(_._2.maxBy(_.getTimestamp)).
            // sort by timestamp desc
            toList.sortBy(-_.getTimestamp)
        ).build
      }
    }
  }

  def getLiveAuctions(productId: Option[Int]): List[AuctionDto] = {
    DB.read {implicit session =>
      val now = System.currentTimeMillis()

      val query = models.Auctions.
        query.filter(a => (a.startTime < now) && (a.finishTime > now ))
      val auctions: List[models.AuctionEntity] = productId.fold(query) { id =>
        query.filter(_.productId === id)
      }.run.toList

      auctions.map {auction =>
        val attendies = (
          for {
            bid <- models.ProductBids.query
            if bid.auctionId === auction.id
          } yield bid
          ).groupBy(_.userId).map {case (userId, query) => (userId, query.map(_.creationTime).max.get)}.run.map {case (userId, timestamp) =>
          (userId, timestamp, models.ProductBids.query.filter(b => (b.auctionId === auction.id) && (b.userId === userId) && (b.creationTime === timestamp)).map(_.bid).first)
        }.map {case (userId, timestamp, bid) =>
          AuctionAttendieDto.newBuilder().
            setMaxBid(bid).
            setUserId(userId).
            setTimestamp(timestamp).
            build
        }.sortBy(_.getMaxBid.getAmount.intValue())

        def getNextPrice(current: models.helpers.RichMoney) =
          current.getAmount.intValue match {
            case i if i > 0 && i <= 300 =>
              i + 10
            case i if i > 300 && i <= 500 =>
              i + 20
            case i if i > 500 && i <= 2000 =>
              i + 50
            case i if i > 2000 && i <= 5000 =>
              i + 100
            case i if i > 5000 && i <= 15000 =>
              i + 200
            case i if i > 15000 && i <= 20000 =>
              i + 500
            case i if i > 20000 && i <= 50000 =>
              i + 1000
            case i if i > 50000 && i <= 200000 =>
              i + 2000
            case i if i > 200000 && i <= 500000 =>
              i + 5000
            case i if i > 500000 =>
              i + 10000
          }


        val (newPrice, winner) = attendies.foldLeft((auction.startPrice, Option.empty[AuctionAttendieDto])) {
          case ((currentPrice, o), attendie) if attendie.getMaxBid.getAmount.intValue() < getNextPrice(currentPrice) =>
            (currentPrice, o)
          case ((currentPrice, None), attendie) =>
            val price = models.helpers.RichMoney(getNextPrice(currentPrice), currentPrice.currencyId)
            (price, Some(attendie))
          case ((currentPrice, Some(currentWinner)), attendie) if attendie.getMaxBid.getAmount.intValue() >= getNextPrice(currentWinner.getMaxBid) =>
            val price = models.helpers.RichMoney(getNextPrice(currentWinner.getMaxBid), currentPrice.currencyId)
            (price, Some(attendie))
        }

        winner.fold(auction: AuctionDto.Builder)(w => auction.setWinner( w )).
          setCurrentPrice(newPrice).
          setAttendies(attendies).
          build
      }
    }
  }

  def addBid(auctionId: Int, userId: Int, amount: Int) = {
    DB.write {implicit session =>
      val bid = models.ProductBidEntity(auctionId = auctionId, userId = userId, bid = models.helpers.RichMoney(amount = amount))
      models.ProductBids.insert(bid)
    }
  }
}
