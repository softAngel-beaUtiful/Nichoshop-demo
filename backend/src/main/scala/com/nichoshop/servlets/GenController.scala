package com.nichoshop.servlets

import com.nichoshop.legacy.models.UsersRow
import com.nichoshop.services.CommonService
import com.nichoshop.servlets.swagger.ApiDescription
import org.scalatra.{NotFound, ScalatraServlet}

trait GenController[T] extends Pagination {
  this: ScalatraServlet with Json with ApiDescription[T] =>

  def service: CommonService[T]

  get("/:id", operation(getById)) {
    okOrNotFound[UsersRow](service.findById(params("id").toInt))
  }

  get("/", operation(getAll)) {
    service.findAll
  }

  post("/", operation(create)) {
    service.create(parsedBody.extract[T])
  }

  delete("/:id", operation(deleteById)) {
    service.deleteById(params("id").toInt)
  }

  put("/:id", operation(updateById)) {
    service.updateById(params("id").toInt, parsedBody.extract[T])
  }

  error {
    case _ => NotFound(s"Not found")
  }
}

trait Pagination {
  self: ScalatraServlet =>

  implicit lazy val pagination = (params("count").toInt, params("page").toInt)
}
