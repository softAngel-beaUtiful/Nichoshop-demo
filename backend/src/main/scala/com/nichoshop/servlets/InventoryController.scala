package com.nichoshop.servlets

import com.nichoshop.marshalling.Marshallers
import com.nichoshop.services.Services
import com.nichoshop.servlets.InventoryController._
import org.json4s.JsonAST.{JBool, JObject}
import org.slf4j.LoggerFactory

/**
 * Created by Evgeny Zhoga on 31.08.15.
 */
class InventoryController extends customer.CustomerController with Json {
  private val log = LoggerFactory.getLogger(getClass)
  def name = "customer/inventory"

  get( "/offers/:productId/:variantId" ) {
    val productId = params("productId").toInt
    val variantId = params("variantId").toInt

    log.info(s"get /offers/$productId was called")
    Services.inventory.getScope(productId, variantId).fold(Marshallers.empty())(Marshallers.toJson)

  }
  post( "/offers/:productId/:variantId" ) {
    Services.authService.withUser { user =>() =>
      val productId = params("productId").toInt
      val variantId = params("variantId").toInt
      val offerData = parsedBody.extract[Offer]

      val accepted = Services.inventory.createOffer(
        offerData.id,
        productId,
        variantId,
        user.getId,
        offerData.amount,
        offerData.qty,
        offerData.message
      )
      JObject(List(
        "accepted" -> JBool(accepted)
      ))
    }
  }
  get( "/list/:catId" ) {
    log.info(s"get /list was called")

    Services.authService.withUser { user =>() =>
      Marshallers.toJson(Services.inventory.getProducts(params("catId").toInt))
    }
  }
  get( "/product/:productId" ) {
    log.info(s"get /product was called")
    Services.authService.withUser { user =>() =>
      val productId: Int = params("productId").toInt
      Services.inventory.getProduct(productId).map {product =>
        Marshallers.toJson(product)
      }.getOrElse(halt(Marshallers.notFound(s"Product with id $productId not found")))

    }
  }

}

object InventoryController {
  case class Offer(id: Int, amount: Int, qty: Int, message: Option[String])
}
