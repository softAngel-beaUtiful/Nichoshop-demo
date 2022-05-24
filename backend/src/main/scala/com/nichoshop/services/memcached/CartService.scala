package com.nichoshop.services.memcached

import com.nichoshop.model.dto.CartDto
import com.nichoshop.utils.Memcached._

/**
 * Created by Evgeny Zhoga on 06.09.15.
 */
trait CartService extends com.nichoshop.services.CartService with BaseService {
  private def cartKey(userId: Int) = s"cart:user:$userId"

  override def addProductToCartInternal(userId: Int, cart: CartDto): Boolean = {
    set(cartKey(userId), cart)
    true
  }

  override def getCart(userId: Int): Option[CartDto] = get[CartDto](cartKey(userId))
}
