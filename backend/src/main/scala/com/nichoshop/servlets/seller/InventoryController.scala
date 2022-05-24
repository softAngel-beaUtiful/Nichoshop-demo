package com.nichoshop.servlets.seller

import com.nichoshop.marshalling.Marshallers
import com.nichoshop.model.dto._
import com.nichoshop.models.helpers.Currencies
import com.nichoshop.services.Services
import com.nichoshop.servlets.Json
import com.nichoshop.servlets.seller.InventoryController._
import org.slf4j.LoggerFactory

import scala.collection.JavaConversions._
import scala.util.control.NonFatal

/**
 * Created by Evgeny Zhoga on 16.08.15.
 */
class InventoryController extends SellerController with Json {
  private val log = LoggerFactory.getLogger(getClass)
  def name = "seller/inventory"

  get( "/list" ) {
    log.info(s"get /list was called")
    try {
      Services.authService.withUser { user =>() =>
        val condition = params.get("condition").getOrElse("all")
        log.info(s"condition asked: $condition")
        val products = Services.inventory.getUserProducts(user.getId, None, condition)

        val keys = products.flatMap { p =>
          p.getVariants.map(p.getId.intValue -> _.getId.intValue)
        }

        val scopes = Services.inventory.getScopes(user.getId.intValue(), keys)
        val productsWithScopes = products.map { p =>
          ProductDto.newBuilder(p).
            setVariants {
            p.getVariants.map { v =>
              scopes.get((p.getId, v.getId)).map(s =>
                ProductVariantDto.newBuilder(v).
                  setOfferScopes(s).build
              ).getOrElse(v)
            }
          }.build()
        }
        Marshallers.toJson(productsWithScopes)
      }
    } catch {
      case NonFatal(e) =>
        log.error("Error => ", e)
        throw e
    }
  }

  post( "/list/:productId/:variantId/offers/scopes" ) {
    Services.authService.withUser { user =>() =>
      val scopeBounds = parsedBody.extract[OfferScope]

      if (Services.inventory.createOfferScope(user.getId,
        params("productId").toInt,
        params("variantId").toInt,
        scopeBounds.start,
        scopeBounds.end
      )) Marshallers.ok()
      else Marshallers.ko("Offer scope was not created")
    }
  }
  put( "/list/:productId/:variantId/offers/offer/:offerId/reject" ) {
    Services.authService.withUser { user =>() =>
      Services.inventory.rejectOffer(user.getId, params("offerId").toInt)
      Marshallers.ok()
    }
  }
  put( "/list/:productId/:variantId/offers/offer/:offerId/accept" ) {
    Services.authService.withUser { user =>() =>
      Services.inventory.acceptOffer(user.getId, params("offerId").toInt, params("productId").toInt, params("variantId").toInt)
      Marshallers.ok()
    }
  }

  // create product
  post("/list") {
    log.info(s"post /list was called")
    Services.authService.withUser { user =>() =>
      val product = parsedBody.extract[Product]

      log.info(s"Extracted product: $product")


      Services.inventory.createProduct(product.toModel(user.getId))

      Marshallers.ok()
    }
  }
  // create variant
  post("/list/:productId") {
    val productId = params("productId").toInt
    log.info(s"post /list/$productId was called")
    Services.authService.withUser { user =>() =>
      val variant = parsedBody.extract[Variant]

      log.info(s"Extracted variant: $variant")


      Services.inventory.createVariant(productId, variant.toModel())

      Marshallers.ok()
    }
  }
  // create variant
  delete("/list/:productId/:variantId") {
    val productId = params("productId").toInt
    val variantId = params("variantId").toInt
    log.info(s"delete /list/$productId/$variantId was called")
    Services.authService.withUser { user =>() =>
      Services.inventory.deleteVariant(productId, variantId)

      Marshallers.ok()
    }
  }

}

object InventoryController {
  case class Variant(
                      title: Option[String],
                      description: Option[String],
                      price: Int,
                      amount: Int,
                      condition: String = ProductCondition.NEW.name(),
                      currencyCode: Int = Currencies.EURO.code
                      ) {
    require(scala.util.Try(ProductCondition.valueOf(condition)).isSuccess, s"Unknown condition [$condition], use one of ${ProductCondition.values().map(_.name()).mkString("[", ", ", "]")}")

    def toModel() = {
      val now = System.currentTimeMillis()
      val builder = ProductVariantDto.newBuilder().
        setCreated(now).
        setPrice(RichMoneyDto.newBuilder().setCurrencyId(currencyCode).setAmount(price).build()).
        setAmount(amount).
        setCondition(ProductCondition.valueOf(condition))
      title.foreach(builder.setTitle)
      description.foreach(builder.setDescription)

      builder.build()
    }
  }
  case class Product(
                      categoryId: Int,
                      title: String,
                      description: String,
                      price: Int,
                      amount: Int,
                      condition: String = ProductCondition.NEW.name(),
                      currencyCode: Int = Currencies.EURO.code) {
    require(scala.util.Try(ProductCondition.valueOf(condition)).isSuccess, s"Unknown condition [$condition], use one of ${ProductCondition.values().map(_.name()).mkString("[", ", ", "]")}")

    def toModel(sellerId: Int) = {
      val now = System.currentTimeMillis()
      ProductDto.
        newBuilder().
        setCategoryId(categoryId).
        setCreated(now).
        setDescription(description).
        setTitle(title).
        setSellerId(sellerId).setVariants(List(
        ProductVariantDto.newBuilder().
          setCreated(now).
          setDescription(description).
          setTitle(title).
          setPrice(RichMoneyDto.newBuilder().setCurrencyId(currencyCode).setAmount(price).build()).
          setAmount(amount).
          setCondition(ProductCondition.valueOf(condition)).
          build()
      )).build()
    }
  }

  case class OfferScope(start: Long, end: Option[Long])
}
