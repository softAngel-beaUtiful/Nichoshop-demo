package com.nichoshop.servlets

import com.nichoshop.marshalling.Marshallers
import com.nichoshop.services.Services
import com.nichoshop.servlets.CustomerCartController._
import org.json4s.JsonAST.{JObject, JString, JValue}
import org.slf4j.LoggerFactory

import scala.collection.JavaConversions._

/**
 * Created by Evgeny Zhoga on 06.09.15.
 */
class CustomerCartController extends customer.CustomerController with Json {
  private val log = LoggerFactory.getLogger(getClass)
  def name = "customer/cart"

  post("/") {
    Services.authService.withUser { user =>() =>
      val product = parsedBody.extract[Product]

      Services.cart.addProductToCart(user.getId, product.productId, product.productVariantId, product.qty)


    }
  }

  post("/makeReservation") {
    Services.authService.withUser { user =>() =>
      val reservationId = Services.cart.makeReservation(userId = user.getId)

      Reservation(reservationId)
    }
  }

  get("/") {
    Services.authService.withUser { user =>() =>
      Services.cart.getCart(user.getId).fold(
        JObject(List(
          "error" -> JString("Cart not found for user")
        )): JValue)(c => Marshallers.toJson(c.getItems.head))
    }
  }

  put("/") {
    Services.authService.withUser { user =>() =>
      val options = parsedBody.extract[ProductOptions]

      log.info(s"parsed options: $options")

      Services.cart.addOptions(user.getId, options.qty)

      Marshallers.ok()
    }
  }

}

object CustomerCartController {
  case class Product(productId: Int, productVariantId: Int, qty: Int)
  case class ProductOptions(qty: Option[Int])

  case class Reservation(reservationId: Option[Int])
}