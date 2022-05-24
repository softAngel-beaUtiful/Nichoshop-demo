package com.nichoshop.models

import com.nichoshop.Environment.driver.simple._
import com.nichoshop.model.dto._
import com.nichoshop.models.helpers._

import scala.slick.lifted.Tag

/**
 * Created by Evgeny Zhoga on 13.11.15.
 */
case class ProductSpecificAttributeEntity(
                                     override val id: Option[Int] = None,
                                     catId: Int,
                                   name: String,
                                    valueType: String,
                                      valueOptions: Option[ ProductAttributeOptionsDto ],
                                     defaultValue: Option[String],
                                     creationTime: Long = System.currentTimeMillis(),
                                   isMultivariation: Boolean = false
                                     ) extends IdAsPrimaryKey

object ProductSpecificAttributeImplicits {
  implicit val schema = ProductAttributeOptionsDto.getClassSchema
  implicit val avroMapper = avroBasedColumnType[ProductAttributeOptionsDto]
}

class ProductSpecificAttributes(tag:Tag) extends TableWithIdAsPrimaryKey[ProductSpecificAttributeEntity](tag, "product_specific_attributes") {
  import ProductSpecificAttributeImplicits._

  def catId = column[Int]("cat_id")
  def name = column[String]("name")
  def valueType = column[String]("value_type")
  def valueOptions = column[Option[ProductAttributeOptionsDto]]("value_options")
  def defaultValue = column[Option[String]]("default_value")
  def creationTime = column[Long]("creation_time")
  def isMultivariation = column[Boolean]("is_multivariation")

  def * = (id.?, catId, name, valueType, valueOptions, defaultValue, creationTime, isMultivariation) <>(ProductSpecificAttributeEntity.tupled, ProductSpecificAttributeEntity.unapply)
}


object ProductSpecificAttributes extends QueryForTableWithIdAsPrimaryKey[ProductSpecificAttributeEntity, ProductSpecificAttributes](TableQuery[ProductSpecificAttributes]){
  object ValueTypes {
    val string = "string"
    val integer = "int"
    val unsignedInteger0 = "uint0"
    val unsignedInteger1 = "uint1"
    val enum = "enum"
  }
}