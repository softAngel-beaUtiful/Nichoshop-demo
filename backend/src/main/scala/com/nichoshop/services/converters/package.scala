package com.nichoshop.services

import com.nichoshop.model.dto._
import com.nichoshop.models
import com.nichoshop.utils.Conversions

import scala.collection.JavaConversions._
import scala.language.implicitConversions

/**
 * Created by Evgeny Zhoga on 12.10.15.
 */
package object converters {
  implicit def user2User(uANDp: (models.UserEntity,List[models.TPermission], List[models.PermissionEntity])): UserDto = Conversions.toUser(uANDp._1, uANDp._2, uANDp._3)

  implicit def product2productBuilder(product: models.ProductEntity): ProductDto.Builder = {
    val productBuilder = ProductDto.newBuilder().
      setCreated(product.creationTime).
      setCategoryId(product.catId).
      setDescription(product.description).
      setSellerId(product.sellerId).
      setTitle(product.title)
    product.id.foreach(productBuilder.setId( _ ))
    productBuilder
  }

  implicit def variant2variantBuilder(variant: models.ProductVariantEntity): ProductVariantDto.Builder = {
    val v = ProductVariantDto.newBuilder().
      setCreated(variant.creationTime).
      setAmount(variant.amount).
      setPrice(RichMoneyDto.newBuilder().setAmount(variant.price.amount).setCurrencyId(variant.price.currencyId).build)

    variant.id.foreach(v.setId( _ ))
    v
  }

  implicit def pv2product(productAndVariant: Tuple2[models.ProductEntity, models.ProductVariantEntity]): ProductDto =
    psv2product( (productAndVariant._1, Seq(productAndVariant._2)) )

  implicit def psv2product(productAndVariant: Tuple2[models.ProductEntity, Seq[models.ProductVariantEntity]]): ProductDto = {
    val pb: ProductDto.Builder = productAndVariant._1
    val v = productAndVariant._2.map(v =>
      v.setDescription(v.description.getOrElse(pb.getDescription)).
      setTitle(v.title.getOrElse(pb.getTitle)).
      build())
    pb.
      setVariants(seqAsJavaList(v)).
      build()
  }

  implicit def rm2richMoney(rm: models.helpers.RichMoney): RichMoneyDto =
    RichMoneyDto.newBuilder().
      setCurrencyId(rm.currencyId).
      setAmount(rm.amount).
      build()

  implicit def richModey2rm(rm: RichMoneyDto): models.helpers.RichMoney =
    models.helpers.RichMoney(
      rm.getAmount,
      rm.getCurrencyId
    )

  implicit def la2lauction(auction: List[models.AuctionEntity]): List[AuctionDto] = auction.map(a2auction)


  implicit def a2auction(auction: models.AuctionEntity): AuctionDto =
    a2auctionBuilder(auction).
      build()

  implicit def a2auctionBuilder(auction: models.AuctionEntity): AuctionDto.Builder =
    AuctionDto.newBuilder().
      setProduct( auction.product ).
      setId(auction.id.get).
      setAttendies(List.empty[AuctionAttendieDto]).
      setStartAt(auction.startTime).
      setFinishAt(auction.finishTime).
      setCurrentPrice( auction.startPrice )

  implicit def of2OfferScope(scope: models.ProductOfferScopeEntity): OfferScopeDto.Builder = {
    OfferScopeDto.newBuilder().
      setId(scope.id.get).
      setStart(scope.startTime).
      setEnd(scope.endTime.map(java.lang.Long.valueOf).orNull)
  }

  implicit def o2offer(offer: models.OfferEntity): OfferDto = {
    val builder = OfferDto.newBuilder()
    builder.
      setId(offer.id.get).
      setCreated(offer.timestamp).
      setPricePerItem(offer.offerPrice).
      setQty(offer.offerQty).
      setUserId(offer.userId).
      setAccepted(offer.state == models.Offers.States.accepted).
      setRejected(offer.state == models.Offers.States.rejected)
    offer.message.foreach(builder.setMessage)

    builder.build()
  }

  implicit def c2condition(condition: ProductCondition): String = condition match {
    case ProductCondition.NEW => models.ProductVariants.Condition.`new`
    case ProductCondition.USED => models.ProductVariants.Condition.used
  }

  implicit def address2adress(address: models.AddressEntity): AddressDto =
    AddressDto.newBuilder().
      setAddressIsVerified(address.addressIsVerified).
      setAddressLine1(address.address).
      setAddressLine2(address.address2.orNull).
      setCity(address.city).
      setCountry(address.country).
      setId(address.id.get).
      setPhones(address.phones).
      setState(address.state.orNull).
      setUserId(address.userId).
      setZip(address.zip).
      build()
}
