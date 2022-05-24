package com.nichoshop

import com.nichoshop.legacy.dao.slick._
import com.nichoshop.services._
import com.nichoshop.utils.db.DataSource

import javax.servlet.ServletContext


object GlobalContext {
  var webApp: WebApp = null
  var applicationContext: ServletContext = null
}

class WebApp {
/*
  val properties = new Properties
  properties.load(Thread.currentThread.getContextClassLoader.getResourceAsStream("nichoshop.properties"))

  val dataSource = DataSource.create(properties)
  val db = Database.forDataSource(dataSource)
*/
  val db = Environment.db

  val userDAO = new SlickUserDAO(db)
  val messageDAO = new SlickMessageDAO(db)
  val messageFolderDAO = new SlickMessageFolderDAO(db)
  val sessionDAO = new SlickSessionDAO(db)
  val tokenDAO = new SlickTokenDAO(db)
  val categoryDAO = new SlickCategoryDAO(db)
  val productDAO = new SlickProductDAO(db)
  val purchaseDAO = new SlickPurchaseDAO(db)
  val sellDAO = new SlickSellDAO(db)
  val trackingDAO = new SlickTrackingDAO(db)

  val userService = new UserService(userDAO)
  val messageService = new MessageService(messageDAO, messageFolderDAO)
  val categoryService = new CategoryService
  val productService = new ProductService(productDAO)
  val purchaseService = new PurchaseService //(purchaseDAO)
  val sellService = new SellService(sellDAO, trackingDAO)


  def shutdown() = {
    DataSource.shutdown()
  }

}
