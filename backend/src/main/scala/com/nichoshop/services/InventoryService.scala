package com.nichoshop.services

import com.nichoshop.Environment.driver.simple._
import com.nichoshop.auth.ControllerAccess._
import com.nichoshop.model.dto._
import com.nichoshop.models
import com.nichoshop.models.helpers._
import com.nichoshop.services.converters._
import org.slf4j.LoggerFactory

import scala.collection.JavaConversions._
import scala.util.Try
import scala.util.control.NonFatal

/**
 * Created by Evgeny Zhoga on 23.08.15.
 */
class InventoryService {
  val log = LoggerFactory.getLogger(getClass)

  val ProductVariantsCondition = models.ProductVariants.Condition

  def getProduct(productId: Int): Option[ProductDto] = {
    import com.nichoshop.services.converters._

    DB.read { implicit session =>
      (for {
        product <- models.Products.query
        if product.id === productId
      } yield product).firstOption.map {product =>
        val variants = (for {
          variant <- models.ProductVariants.query
          if variant.productId === product.id
        } yield variant).list

        (product, variants)
      }
    }
  }

  def getProducts(categoryId: Int): List[ProductDto] = {
    import com.nichoshop.services.converters._

    DB.read { implicit session =>
      val products =
        (for {
          product <- models.Products.query
          if product.catId === categoryId
        } yield product).list
      products.map { product =>
        val variants = (for {
          variant <- models.ProductVariants.query
          if variant.productId === product.id
        } yield variant).list

        (product, variants): ProductDto
      }
    }
  }

  def createProduct(product: ProductDto)(implicit controllerAccess: ControllerAccessType, notAuthorized: () => Nothing):Boolean = {
    try {
      DB.write { implicit session =>
        val p = models.ProductEntity(None, product.getSellerId, product.getCategoryId, product.getTitle, product.getDescription, product.getCreated)
        val productId = models.Products.insert(p)
        product.getVariants.foreach {variant =>
          val pv = models.ProductVariantEntity(None,
            productId = productId,
            Some(variant.getTitle),
            Some(variant.getDescription),
            variant.getCondition,
            RichMoney(variant.getPrice.getAmount, variant.getPrice.getCurrencyId), variant.getAmount, variant.getCreated)
          models.ProductVariants.insert(pv)
        }
        true
      }
    } catch {
      case NonFatal(e) =>
        log.error("Exception while creating product -->", e)
        false
    }

  }
  def createVariant(productId: Int, variant: ProductVariantDto) (implicit controllerAccess: ControllerAccessType, notAuthorized: () => Nothing):Boolean = {
    try {
      DB.write { implicit session =>
          val pv = models.ProductVariantEntity(None,
            productId = productId,
            Option(variant.getTitle),
            Option(variant.getDescription),
            variant.getCondition,
            RichMoney(variant.getPrice.getAmount, variant.getPrice.getCurrencyId), variant.getAmount, variant.getCreated)
          models.ProductVariants.insert(pv)

        true
      }
    } catch {
      case NonFatal(e) =>
        log.error("Exception while creating product -->", e)
        false
    }
  }
  def deleteVariant(productId: Int, variantId: Int) (implicit controllerAccess: ControllerAccessType, notAuthorized: () => Nothing):Boolean = {
    try {
      DB.write { implicit session =>
        models.ProductVariants.delete(variantId)

        true
      }
    } catch {
      case NonFatal(e) =>
        log.error("Exception while creating product -->", e)
        false
    }
  }

  def getUserProducts(userId: Int, categoryId: Option[Int], c: String)(implicit controllerAccess: ControllerAccessType, notAuthorized: () => Nothing) = {
    val condition: Option[String] = Try(ProductCondition.valueOf(c)).toOption.map( v => v : String)

      import com.nichoshop.services.converters._

      DB.read { implicit session =>
        val products =
          (for {
            product <- models.Products.query
            if product.sellerId === userId
          } yield product).list

        val categories = models.Categories.getCategoriesWithParentsOrdered(products.map(_.catId))

        products.flatMap { product =>
          val variants = {
            val q = for {
              variant <- models.ProductVariants.query
              if variant.productId === product.id
            } yield variant

            log.info(s"condition to select ${condition}")

            condition.fold(q)(v => q.filter(_.condition === v))
          }.list

          if (variants.isEmpty) None
          else Some((product, variants): ProductDto)
        }
      }
  }

  /**
   * Create scope which will be bound with user offers for specific variant of product.
   * It could be any number of scopes, but they are not allowed to overlap.
   *
   * @param userId
   * @param productId
   * @param variantId
   * @param startTime
   * @param endTime
   * @return
   */
  def createOfferScope(userId: Int, productId: Int, variantId: Int, startTime: Long, endTime: Option[Long]) = {
    DB.write {implicit session =>
      val et = endTime.getOrElse(Long.MaxValue)
      // select all live scopes
      val runningOfferScopes =
        models.ProductOfferScopes.query.filter(pos => (pos.productId === productId) && (pos.variantId === variantId) && (pos.closed === false)).
          list.map(v => v.startTime -> v.endTime.getOrElse(Long.MaxValue))

      // and check if any of them overlap current
      runningOfferScopes.find{ case (start, end) => start <= et && end >= startTime }.fold {
        // if no overlaps - crete new scope
        val scope = models.ProductOfferScopeEntity(
          productId = productId,
          variantId = variantId,
          startTime = startTime,
          endTime = endTime
        )
        models.ProductOfferScopes.insert(scope)
        true
      // overwise scope will not be created
      } {_ => false}
    }
  }

  /**
   * Get product offer scope if exists. Detects if offer from buyer is acceptable
   *
   * @param productId
   * @param variantId
   * @return
   */
  def getScope(productId: Int, variantId: Int): Option[OfferScopeDto] = {
    val now = System.currentTimeMillis()
    DB.read {implicit session =>
      models.ProductOfferScopes.query.
        filter(pos => (pos.productId === productId) && (pos.variantId === variantId) && (pos.closed === false)).list.filter(pos => pos.startTime <= now && pos.endTime.getOrElse(Long.MaxValue) >= now).
        headOption.map(_.build())
    }
  }

  def getScopes(userId: Int, productVariants: List[Tuple2[Int, Int]]): Map[Tuple2[Int, Int], List[OfferScopeDto]] = {
    val now = System.currentTimeMillis()
    DB.read { implicit session =>
      val vals = (for {
        scope <- models.ProductOfferScopes.query
        offer <- models.Offers.query
        if (scope.productId inSet productVariants.map(_._1)) &&
          (scope.variantId inSet productVariants.map(_._2)) &&
          (offer.productOfferScopeId === scope.id)
      } yield (scope, offer)).list.groupBy { case (scope, _) => scope.productId -> scope.variantId }.
        map { case ((productId, variantId), listOfScopesWithOffers) =>
        (productId, variantId) -> listOfScopesWithOffers.groupBy(_._1).map { case (offer, listOfScopesWithOffers) => offer -> listOfScopesWithOffers.map(_._2) }
      }

      vals.map {case ( ((productId, variantId), m) ) =>
        (productId, variantId) -> m.toList.map {case (scope, offers) =>
            scope.
              setOffers(offers.map(o => o: OfferDto)).
              build
        }
      }
    }

  }

  /**
   * Create offer for specific offer scope. Available variant QTY is checked.
   *
   * @param offerScopeId
   * @param productId
   * @param variantId
   * @param userId
   * @param price
   * @param qty
   * @param message
   * @return
   */
  def createOffer(offerScopeId: Int, productId: Int, variantId: Int, userId: Int, price: Int, qty: Int, message: Option[String]) = {
    DB.write {implicit session =>
      models.ProductVariants.query.filter(pv => (pv.id === variantId) && (pv.productId === productId) && (pv.amount >= qty)).firstOption.fold {
        false
      } {_ =>
        val o = models.OfferEntity(productOfferScopeId = offerScopeId, userId = userId, offerPrice = models.helpers.RichMoney(amount = price), offerQty = qty, message = message, state = models.Offers.States.open)
        models.Offers.insert(o)
        true
      }
    }
  }

  /**
   * Make a reservation of variant if required QTY is available
   *
   * @param offerId
   * @param productId
   * @param variantId
   */
  def acceptOffer(sellerId:Int, offerId: Int, productId: Int, variantId: Int) = {
    // TODO need to check if offer is owned by sellerId

    DB.transaction {implicit session =>
      models.Offers.findById(offerId).flatMap { case offer =>
         (for {
          product <- models.Products.query
          variant <- models.ProductVariants.query
          if (product.id === variant.productId) && (product.id === productId) && (variant.id === variantId)
        } yield (product, variant)).firstOption.map{case (product, variant) => (offer, product, variant)}
//      }.flatMap {case (offer, product, variant) =>
        // reserve items
//        Option(ProductVariants.query.filter(v => (v.id === variantId) && (v.amount >= offer.offerQty)).map(_.amount).update(variant.amount - offer.offerQty)).
//          filter(_ == 1).map(_ => (offer, product, variant))
      }.map { case (offer, product, variant) => (offer, product, variant.copy(price = offer.offerPrice, amount = offer.offerQty))
      }.map { case (offer, product, variant) =>
          val p: ProductDto = (product, variant)
          val item: CartItemDto = CartItemDto.newBuilder().
            setProduct(p).
            setQty(offer.offerQty).
            build

        val reservation = Services.cart.makeReservationWithSession(offer.userId, item)
        log.info(s"reservation: $reservation")
        models.Offers.query.filter(_.id === offerId).map(_.state).update(models.Offers.States.accepted)
      }
    }
  }
  def rejectOffer(sellerId:Int, offerId: Int) = {
    // TODO need to check if offer is owned by sellerId

    DB.write {implicit session =>
      models.Offers.query.filter(_.id === offerId).map(_.state).update(models.Offers.States.rejected)
    }
  }
}
