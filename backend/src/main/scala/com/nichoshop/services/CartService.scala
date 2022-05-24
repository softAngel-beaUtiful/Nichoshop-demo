package com.nichoshop.services

import com.nichoshop.Environment.driver.simple._
import com.nichoshop.model.dto._
import com.nichoshop.models.helpers._
import com.nichoshop.services.converters._
import com.nichoshop.{Environment, models}
import org.slf4j.LoggerFactory

import scala.collection.JavaConversions._

/**
 * Created by Evgeny Zhoga on 06.09.15.
 */
class CartService {
  private val log = LoggerFactory.getLogger(getClass)

  protected def addProductToCartInternal(userId: Int, cart: CartDto) = true

  def addProductToCart(userId: Int, productId: Int, productVariantId: Int, qty: Int) = {
    log.info(s"Got request to add product to cart: userId=$userId, productId=$productId, productVariantId=$productVariantId, qty=$qty")

    val product: ProductDto = DB.read { implicit session =>
        (for {
          product <- models.Products.query
          variant <- models.ProductVariants.query
          if (product.id === productId) && (product.id === variant.productId) && (variant.id === productVariantId)
        } yield (product, variant)).firstOption.map {case (product, variant) =>
          (product, variant.copy(amount = qty))
        }.get
    }
    val item = CartItemDto.newBuilder().
      setProduct(product).
      setQty(qty).build
    val cart = CartDto.newBuilder().
      setItems(seqAsJavaList(Seq(item))).
      build()

    addProductToCartInternal(userId, cart)
  }

  def addOptions(userId: Int, qty: Option[Int] = None) = {
    getCart(userId).
    fold(false) {cart =>
      val builder = CartDto.newBuilder(cart)

      cart.getItems.headOption.fold(false) {item =>
        val cartItemBuilder = CartItemDto.newBuilder(item)

        qty.foreach(i => cartItemBuilder.setQty(i))

        builder.setItems(List(cartItemBuilder.build()))

        addProductToCartInternal(userId, builder.build())

        true
      }
    }
  }


  def makeReservationOld(userId: Int, cartItem: CartItemDto): Option[Int] = {
    DB.read {implicit session => models.ProductVariants.findById(cartItem.getProduct.getVariants.head.getId)}.map(_.amount).filter(_ >= cartItem.getQty).flatMap {_ =>
      val reservation: models.ProductReservationEntity = models.ProductReservationEntity(
        productId = cartItem.getProduct.getId,
        productVariantId = cartItem.getProduct.getVariants.head.getId,
        userId = userId,
        sellerId = cartItem.getProduct.getSellerId,
        qty = cartItem.getQty,
        price = RichMoney(cartItem.getProduct.getVariants.head.getPrice.getAmount, cartItem.getProduct.getVariants.head.getPrice.getCurrencyId),
        item = cartItem
      )
      val id = DB.write { implicit session =>
        models.ProductReservations.insert(
          reservation
        )
      }
      Some(id)
    }
  }

  /**
   * Reservation will check if required quantity is available and reserve this amount for some time
   * to allow
   * @param userId
   * @param cartItem
   * @return
   */
  def makeReservation(userId: Int, cartItem: CartItemDto): Option[Int] = {
    log.trace(s" ========> makeReservation was called")

    DB.transaction { implicit session =>
      makeReservationWithSession(userId, cartItem)
    }
  }
  def makeReservationWithSession(userId: Int, cartItem: CartItemDto)(implicit session: Environment.driver.simple.Session): Option[Int] = {
      // select all variants from cart item:
      val variants: List[models.ProductVariantEntity] = models.ProductVariants.query.
        filter(_.id inSet cartItem.getProduct.getVariants.map(_.getId.intValue())).list

      // check that variants size is the same as required
      if (variants.size == cartItem.getProduct.getVariants.size()) {

        val reservedSuccessfully = cartItem.getProduct.getVariants.forall {pv =>
          val dbVariant = variants.find(_.id.get == pv.getId).get

          val updated = models.ProductVariants.query.
            // we check if: 1. we have enough amount and 2. that we update exactly same state as we selected couple
            // of lines before. Kind of optimistic lock
            filter(v => (v.id === pv.getId.intValue()) && (v.amount >= pv.getAmount.intValue()) && (v.amount === dbVariant.amount)).
            // and remove required amount of items from available qty
            map(_.amount).update(dbVariant.amount - pv.getAmount)
          // if update happens, that's ok. Means that we have researved required amount
          if (updated == 1) true
          // otherwise - stop
          else false
        }
        if (!reservedSuccessfully) {
          session.rollback()
          None
        } else {
          val reservation: models.ProductReservationEntity = models.ProductReservationEntity(
            productId = cartItem.getProduct.getId,
            productVariantId = cartItem.getProduct.getVariants.head.getId,
            userId = userId,
            sellerId = cartItem.getProduct.getSellerId,
            qty = cartItem.getQty,
            price = RichMoney(cartItem.getProduct.getVariants.head.getPrice.getAmount, cartItem.getProduct.getVariants.head.getPrice.getCurrencyId),
            item = cartItem
          )
          val id = DB.write { implicit session =>
            models.ProductReservations.insert(
              reservation
            )
          }
          Some(id)
        }
      } else {
        session.rollback()
        None
      }
  }

  def makeReservation(userId: Int, cart: CartDto): Option[Int] = makeReservation(userId, cart.getItems.head)

  def makeReservation(userId: Int): Option[Int] = {
    getCart(userId).
      fold(Option.empty[Int])(makeReservation(userId, _))
  }

  def getCart(userId: Int): Option[CartDto] = None
}
