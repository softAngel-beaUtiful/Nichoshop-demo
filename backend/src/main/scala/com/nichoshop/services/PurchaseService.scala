package com.nichoshop.services

import com.nichoshop.Environment.driver.simple._
import com.nichoshop.models._
import com.nichoshop.models.helpers.DB
import org.slf4j.LoggerFactory

import scala.collection.JavaConversions._

class PurchaseService {
  private val log = LoggerFactory.getLogger(getClass)
  // TODO remove this
/*
  def findAll(userId: Int, count: Int, page: Int): List[PurchaseRow] =
    purchaseDAO.findAll(userId, (page - 1) * count, count)
*/
  // TODO end of remove


  /**
   * Purchase product according reservation
   * Check if reservation does not expire
   * @param reservationId
   * @return
   */
  def purchase(userId: Int, reservationId: Int): Boolean = {
    log.debug(s" ========> purchase was called")
    DB.read {implicit session =>
      ProductReservations.query.filter(r => (r.id === reservationId) && (r.userId === userId) ).firstOption
    }.filter {
      case r if r.creationTime + r.reservationPeriodMillis > System.currentTimeMillis() =>
        true
      case r if r.creationTime + r.reservationPeriodMillis > System.currentTimeMillis() =>
        val now = System.currentTimeMillis
        log.debug(s" =============> reservation creationTime: ${r.creationTime}")
        log.debug(s" =============> reservation reservationPeriodMillis: ${r.reservationPeriodMillis}")
        log.debug(s" =============> currentTimeMillis: $now")
        log.debug(s" =============> diff: ${r.creationTime + r.reservationPeriodMillis} <= $now ??")
        false
    }.fold(false) { reservation =>
      DB.transaction {implicit session =>
        // ProductVariants.update()
        ProductPurchases.insert(
          ProductPurchaseEntity(id = None, reservationId = reservationId)
        )
        true
      }
    }
  }
  def purchaseOld(userId: Int, reservationId: Int): Boolean = {
    DB.read {implicit session =>
      ProductReservations.query.filter(r => (r.id === reservationId) && (r.userId === userId) && ((r.creationTime + r.reservationPeriodMillis) <= System.currentTimeMillis()) ).firstOption
    }.fold(false) { reservation =>
      DB.transaction {implicit session =>
        // update amount of variants of product
        reservation.item.getProduct.getVariants.headOption.flatMap { variant =>
          ProductVariants.query.filter(_.id === variant.getId.intValue).map(_.amount).filter(_ >= reservation.qty).firstOption.
            map { currentAmount =>
            ProductVariants.query.filter(_.id === variant.getId.intValue).map(_.amount).update(currentAmount - reservation.qty)
            true
          }
        }.fold(false) { _ =>
          // ProductVariants.update()
          ProductPurchases.insert(
            ProductPurchaseEntity(id = None, reservationId = reservationId)
          )
          true
        }
      }
    }
  }
}
