package com.nichoshop.services

import com.nichoshop.legacy.dao.{SellDAO, TrackingDAO}
import com.nichoshop.legacy.models.TrackingRow

class SellService(val sellDAO: SellDAO, trackingDAO: TrackingDAO) {
  def markAsDispatched(sellerId: Int, sellId: Int) = sellDAO.markAsDispatched(sellerId, sellId)

  def addTracking(sellerId: Int, sellId: Int, number: String, courier: String) = {
    sellDAO.findById(sellId).filter(_.sellerId == sellerId)
      .foreach(_ => trackingDAO.create(TrackingRow(0, sellId, number, courier)))
  }

}
