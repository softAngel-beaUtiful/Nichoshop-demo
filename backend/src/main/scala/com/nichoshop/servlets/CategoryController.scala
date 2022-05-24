package com.nichoshop.servlets

import com.nichoshop.marshalling.Marshallers
import com.nichoshop.services.CategoryService
import com.nichoshop.servlets.swagger.CategoryOperations
import org.scalatra.ScalatraServlet
import org.scalatra.swagger.Swagger

class CategoryController(categoryService: CategoryService)
                        (implicit val swagger: Swagger)
  extends ScalatraServlet with Json with CategoryOperations {

  get("/parent/:parentId", operation(getSubcategories)) {
    categoryService.subcategories(params("parentId").toInt)
  }

  get("/sidebar", operation(sidebar)) {
    categoryService.sidebarCategories
  }

  get("/sidebar2", operation(sidebar)) {
    Marshallers.toJson(categoryService.sidebarCategories2)
  }

}
