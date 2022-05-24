package com.nichoshop.models

import com.nichoshop.Environment.driver.simple._
import com.nichoshop.models.helpers._

import scala.slick.lifted.Tag

/**
 * Created by Evgeny Zhoga on 22.11.15.
 */
case class AddressEntity(
                      override val id: Option[Int] = None,
                      userId: Int,
                      address: String,
                      address2: Option[String] = None,
                      city: String,
                      state: Option[String] = None, //2 letters
                      zip: String,
                      country: String,//ISO country code (2 letters)
                      phones: List[String] = List(),// comma separated
                      addressIsVerified: Boolean = false
                      ) extends IdAsPrimaryKey

class Addresses(tag:Tag) extends TableWithIdAsPrimaryKey[AddressEntity](tag, "addresses") {
  implicit val commaSeparatedString = separatedString()

  def userId = column[Int]("user_id")
  def address = column[String]("address")
  def address2 = column[Option[String]]("address2")
  def city = column[String]("city")
  def state = column[Option[String]]("state")
  def zip = column[String]("zip")
  def country = column[String]("country")
  def phones = column[List[String]]("phones")
  def addressIsVerified = column[Boolean]("address_is_verified")

  def * = ( id.?, userId, address, address2, city, state, zip, country, phones, addressIsVerified) <> ( AddressEntity.tupled, AddressEntity.unapply )
}

object Addresses extends QueryForTableWithIdAsPrimaryKey[AddressEntity, Addresses](TableQuery[Addresses])

