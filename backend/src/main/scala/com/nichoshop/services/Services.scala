package com.nichoshop.services

import com.nichoshop.services.duo.DuoService
import com.nichoshop.services.memcached.BaseService

/**
 * Created by Evgeny Zhoga on 25.06.15.
 */
object Services {
  val authService = new AuthService with memcached.AuthService
  val permissions = new PermissionsService
  val authSupportService = new AuthSupportService

  val usersService = new UsersService

  val category = new CategoryService
  val inventory = new InventoryService

  val cart = new CartService with memcached.CartService

  val purchase = new PurchaseService

  val auction = new AuctionService

  val duoService = new DuoService
}
