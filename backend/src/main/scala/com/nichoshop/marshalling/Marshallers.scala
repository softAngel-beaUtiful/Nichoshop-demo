package com.nichoshop.marshalling

import com.nichoshop.model.dto._
import com.nichoshop.utils.Conversions
import org.json4s.JsonAST._
import org.scalatra.{BadRequest, Forbidden, NotFound, Unauthorized}
import org.slf4j.LoggerFactory

import scala.collection.JavaConversions._
import scala.language.implicitConversions

/**
 * Created by Evgeny Zhoga on 04.06.15.
 */
object Marshallers {
  private val log = LoggerFactory.getLogger(getClass)
  private implicit def long2BigInt(v: Long): BigInt = BigInt(v)
  private implicit def javaInteger2BigInt(v: java.lang.Integer): BigInt = BigInt(v.intValue())
  private implicit def javaLong2BigInt(v: java.lang.Long): BigInt = BigInt(v.longValue())

  def toJson(user: UserDto): JValue = {
    JObject(List(
      "id" -> JInt(user.getId),
      "userId" -> JString(user.getUserid),
      "firstName" -> JString(user.getFirstName),
      "lastName" -> JString(user.getLastName),
      "email" -> JString(user.getEmail),
      "roles" -> JArray(user.getRoles.map(_.name()).map(JString).toList)
    ))
  }
  def toJson(category: CategoryDto): JValue = {
    JObject(List(
      "name" -> JString(category.getName),
      "id" -> JInt(category.getId.intValue()),
      "parentId" -> JInt(category.getParentId.intValue()),
      "conditionType" -> JString(category.getConditionType)
    ))
  }

  def toJson(tree: CategoryTreeDto): JValue = {
    if (tree.getCategory == Conversions.ROOT_CATEGORY) {
      JArray(
        tree.getChildren.map(toJson).toList
      )
    } else {
      JObject(List(
        Some("category" -> toJson(tree.getCategory)),
        if (tree.getChildren.isEmpty) None else Some("children" -> JArray(tree.getChildren.map(toJson).toList))
      ).flatten)
    }
  }

  def toJson(products: List[ProductDto]): JValue = {
    JArray( products.map{ case p: ProductDto  => toJson(p)} )
  }

  def toJson(products: List[ProductDto], scopes: List[OfferScopeDto]): JValue = {
    JObject(List(
      "products" -> toJson(products),
      "scopes" -> JArray(scopes.map(toJson))
    ))
    JArray( products.map{ case p: ProductDto  => toJson(p)} )
  }

  def toJson(product: ProductDto): JValue = {
    JObject(List(
      "id" -> JInt(product.getId.intValue()),
      "title" -> JString(product.getTitle),
      "description" -> JString(product.getDescription),
      "sellerId" -> JInt(product.getSellerId.intValue()),
      "categoryId" -> JInt(product.getCategoryId.intValue()),
      "variants" -> JArray(product.getVariants.map{case v: ProductVariantDto => toJson(v)}.toList)
    ))
  }

  def toJson(attendie: AuctionAttendieDto): JValue = {
    JObject(List(
      "userId" -> JInt(attendie.getUserId),
      "maxBid" -> JInt(attendie.getMaxBid.getAmount),
      "timestamp" -> JInt(attendie.getTimestamp)
    ))
  }
  def toJson(auction: List[AuctionDto], user: UserDto): JValue = JArray(auction.map(toJson(_, user)))

  def toJson(auction: AuctionDto, user: UserDto): JValue = {
    JObject(List(
      "id" -> JInt(auction.getId),
      "product" -> toJson(auction.getProduct),
      "startAt" -> JInt(auction.getStartAt),
      "finishAt" -> JInt(auction.getFinishAt),
      "currentPrice" -> JInt(auction.getCurrentPrice.getAmount),
      "isWinner" -> JBool(Option(auction.getWinner).exists(_.getUserId.intValue() == user.getId)),
      "attendie" -> Option(auction.getAttendies).flatMap(_.find(_.getUserId == user.getId)).map(toJson).orNull
    ))
  }

  def toJson(item: CartItemDto): JValue = {
    JObject(List(
      "product" -> toJson(item.getProduct),
      "qty" -> JInt(item.getQty.intValue())
    ))
  }

  def toJson(variant: ProductVariantDto): JValue = {
    JObject(List(
      Some("id" -> JInt(variant.getId.intValue())),
      Some("title" -> JString(variant.getTitle)),
      Some("description" -> JString(variant.getDescription)),
      Some("price" -> JInt(variant.getPrice.getAmount.intValue())),
      Some("amount" -> JInt(variant.getAmount.intValue())),
      Some("condition" -> JString(variant.getCondition.name())),
      Option(variant.getOfferScopes).map(os => "offerScopes" -> JArray(os.toList.map(toJson)))
    ).flatten)
  }

  def toJson(scope: OfferScopeDto): JValue = {
    JObject(List(
      Some("id" -> JInt(scope.getId)),
      Some("start" -> JInt(scope.getStart)),
      Some("end" -> Option(scope.getEnd).map(JInt( _ ): JValue).getOrElse(JNull)),
      Option(scope.getOffers).map(o => "offers" -> JArray(o.toList.map(toJson)))
    ).flatten)
  }
  def toJson(offer: OfferDto): JValue = {
    JObject(List(
      Some("id" -> JInt(offer.getId)),
      Some("amount" -> JInt(offer.getPricePerItem.getAmount)),
      Some("qty" -> JInt(offer.getQty)),
      Some("userId" -> JInt(offer.getUserId)),
      Some("timestamp" -> JInt(offer.getCreated)),
      Some("accepted" -> JBool(offer.getAccepted)),
      Some("rejected" -> JBool(offer.getRejected)),
      Option(offer.getMessage).map(m => "message" -> JString(m))
    ).flatten)
  }
  def toJson(productAttribute: ProductAttributeDto) = {
    JObject(List(
      Some("id" -> JInt(productAttribute.getId)),
      Some("categoryId" -> JInt(productAttribute.getCategoryId)),
      Some("name" -> JString(productAttribute.getName)),
      Some("valueType" -> JString(productAttribute.getValueType.name())),
      Some(productAttribute.getValueType).filter(_ == ProductAttributeType.ENUM).map(_ => "valueOptions" -> JArray(productAttribute.getValueOptions.getOptions.toList.map(JString))),
      Some("defaultValue" -> JString(productAttribute.getDefaultValue)),
      Some("isMultivariation" -> JBool(productAttribute.getIsMultivariation))
    ).flatten)
  }

  def data(data: String): JObject = {
    JObject(List(
      "data" -> JString(data)
    ))
  }

  def ok() = {
    JObject(List(
      "status" -> JString("ok")
    ))
  }

  def empty(): JValue = {
    JObject()
  }

  def ko(message: String, additions: Map[String, String] = Map.empty) = {
    val tuples: List[(String, JString)] = ("error", JString(message)) :: additions.toList.map{case (k, v) => k -> JString(v)}
    JObject( tuples )
  }

  def code(code: String) = {
    JObject(List(
      "code" -> JString(code)
    ))
  }

  def unauthorized(message: String, additions: Map[String, String] = Map.empty) = {
    log.info(s" >>>>>>>> $additions")
    val tuples: List[(String, JString)] = ("error", JString(message)) :: additions.toList.map{case (k, v) => k -> JString(v)}
    Unauthorized(body =
      JObject( tuples )
    )
  }

  def bad(message: String) = {
    BadRequest(body =
      JObject(List(
        "error" -> JString(message)
      ))
    )
  }

  def forbidden(message: String) = {
    Forbidden(body =
      JObject(List(
        "error" -> JString(message)
      ))
    )
  }
  def notFound(message: String) = {
    NotFound(body =
      JObject(List(
        "error" -> JString(message)
      ))
    )
  }

}
