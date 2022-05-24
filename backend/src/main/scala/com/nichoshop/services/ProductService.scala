package com.nichoshop.services

import com.nichoshop.legacy.dao.ProductDAO
import com.nichoshop.legacy.models.ProductsRow


class ProductService(val productDAO: ProductDAO) extends CommonService[ProductsRow] {

  val dailyDealsId = 21324

  def dao = productDAO

  def search(s: String): List[ProductsRow] = {
    productDAO.search(s)
  }

  def searchWithCategoryId(catId: Int, str: String): List[ProductsRow] = {
    productDAO.searchWithCategoryId(catId, str)
  }

  def filterByCategory(catId: Int): List[ProductsRow] = {
    productDAO.filterByCategory(catId)
  }

  def bestProducts(): List[ProductsRow] = {
    productDAO.randomWithCategory(12, dailyDealsId)
  }

  def watchlist(userId: Int)(implicit paging: (Int, Int)): List[ProductsRow] = {
    productDAO.watchlist(userId, (paging._2 - 1) * paging._1, paging._1)
  }

  import ProductState._

  def active(sellerId: Int, filter: Int)(implicit paging: (Int, Int)): List[ProductsRow] = {
    productDAO.filterByState(sellerId, ACTIVE, filter, (paging._2 - 1) * paging._1, paging._1)
  }

  def sold(sellerId: Int, filter: Int)(implicit paging: (Int, Int)): List[ProductsRow] = {
    productDAO.filterByState(sellerId, SOLD, filter, (paging._2 - 1) * paging._1, paging._1)
  }

  def unsold(sellerId: Int)(implicit paging: (Int, Int)): List[ProductsRow] = {
    productDAO.filterByState(sellerId, UNSOLD, 0, (paging._2 - 1) * paging._1, paging._1)
  }

  def archive(sellerId: Int, filter: Int)(implicit paging: (Int, Int)): List[ProductsRow] = {
    productDAO.filterByState(sellerId, ARCHIVED, filter, (paging._2 - 1) * paging._1, paging._1)
  }

  def deleteMarked(sellerId: Int, ids: List[Int]) = productDAO.deleteMarked(sellerId, ids)

}

object ProductState {
  val ACTIVE: Byte = 0
  val SOLD: Byte = 1
  val UNSOLD: Byte = 2
  //expired
  val ARCHIVED: Byte = 3
}
