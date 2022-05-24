package com.nichoshop.servlets.admin

import com.nichoshop.marshalling.Marshallers
import com.nichoshop.model.dto.{ProductAttributeDto, ProductAttributeOptionsDto, ProductAttributeType}
import com.nichoshop.services.{PermissionsService, Services}
import com.nichoshop.servlets.Json
import com.nichoshop.servlets.customer.CustomerController
import org.json4s.JsonAST.JArray
import org.slf4j.LoggerFactory

import scala.collection.JavaConversions._
/**
 * Created by Evgeny Zhoga on 03.07.15.
 */
class CategoryController extends CustomerController  with Json {
  import CategoryController._

  def name = "admin/category"

  get("/top") {
    Marshallers.toJson(Services.category.sidebarCategories2)
  }

  get("/all") {
    Marshallers.toJson(Services.category.categoriesTree)
  }

  get("/children/:parentId") {
    Marshallers.toJson(Services.category.categories(params("parentId").toInt))
  }

  post ("/parent/:parentId") {
    Services.authService.withUser { user =>() =>
      val p = PermissionsService.Permissions.`categories:add`
      Services.permissions.withProtection(p)(user) {

        val c = parsedBody.extract[CreateCategory]

        Services.category.createCategory(c.name, params("parentId").toInt)

      }.getOrElse(halt(Marshallers.unauthorized(s"Permission required: ${p.name}")))
    }
  }
  put ("/parent/:parentId/:childId") {
    Services.authService.withUser { user =>() =>
      val p = PermissionsService.Permissions.`category:move`
      Services.permissions.withProtection(p)(user) {
        Services.category.moveCategory(params("parentId").toInt, params("childId").toInt)
        Marshallers.ok()
      }.getOrElse(halt(Marshallers.unauthorized(s"Permission required: ${p.name}")))
    }
  }

  put ( "/:categoryId" ) {
    logg.info(s"got request to change category")
    val category = parsedBody.extract[UpdateCategory]

    logg.info(s"will update category..")
    Services.category.updateCategory(params("categoryId").toInt, category.conditionType.filterNot(_ == ""))

    Marshallers.ok()
  }

  get ( "/:categoryId/attributes" ) {
    JArray(Services.category.getAttributes(params("categoryId").toInt).map(Marshallers.toJson))
  }


  post ( "/:categoryId/attributes" ) {
    val a = parsedBody.extract[CategoryAttribute]

    logg.info(s"Parsed attribute: $a")
    Services.category.createAttribute(a.toModel(params("categoryId").toInt))

    Marshallers.ok()
  }
  put ( "/:categoryId/attribute/:id" ) {
    val a = parsedBody.extract[CategoryAttribute]

    logg.info(s"Parsed attribute: $a")
    Services.category.updateAttribute(params("id").toInt, a.toModel(params("categoryId").toInt))

    Marshallers.ok()
  }
  delete ( "/:categoryId/attribute/:id" ) {
    Services.category.deleteAttribute(params("id").toInt)

    Marshallers.ok()
  }
}

object CategoryController {
  val logg = LoggerFactory.getLogger(getClass)

  case class CreateCategory(name: String)

  case class UpdateCategory(conditionType: Option[String])

  case class CategoryAttribute(name: String, attributeType: String, options: Seq[String], defaultValue: Option[String], isMultivariation: Option[Boolean]) {
    def toModel(categoryId: Int) = ProductAttributeDto.newBuilder().
      setId(-1).
      setCategoryId(categoryId).
      setName(name).
      setDefaultValue(defaultValue.orNull).
      setValueOptions(Option(options).filter(_.nonEmpty).map(l => ProductAttributeOptionsDto.newBuilder().setOptions(l).build()).orNull).
      setValueType(ProductAttributeType.valueOf(attributeType)).
      setIsMultivariation(isMultivariation.getOrElse(false)).build()
  }
}
