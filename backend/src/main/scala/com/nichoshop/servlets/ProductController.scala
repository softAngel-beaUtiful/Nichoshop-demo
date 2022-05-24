package com.nichoshop.servlets

import com.nichoshop.legacy.models.ProductsRow
import com.nichoshop.marshalling.Marshallers
import com.nichoshop.services.{ProductService, SellService, Services}
import com.nichoshop.servlets.swagger.ProductOperations
import org.scalatra.swagger.Swagger

class ProductController(productService: ProductService, sellService: SellService)
                       (implicit val swagger: Swagger)
  extends AuthServlet with Json with ProductOperations with GenController[ProductsRow] {

  //  before() {
  //    requireLogin()
  //  }

  def service = productService

  get("/search", operation(search)) {
    val str = params.get("str")
    val catId = params.get("catId")

    (str, catId) match {
      case (Some(s), Some(id)) => productService.searchWithCategoryId(id.toInt, s)
      case (Some(s), None) => productService.search(s)
      case (None, Some(id)) => productService.filterByCategory(id.toInt)
      case _ => productService.findAll
    }
  }

  get("/bestProducts", operation(bestProducts)) {
    productService.bestProducts()
  }

  get("/active/f/:filter/p/:page/c/:count", operation(active)) {
    productService.active(uid, params("filter").toInt)
  }

  get("/sold/f/:filter/p/:page/c/:count", operation(sold)) {
    productService.sold(uid, params("filter").toInt)
  }

  get("/unsold/p/:page/c/:count", operation(unsold)) {
    productService.unsold(uid)
  }

  get("/archive/f/:filter/p/:page/c/:count", operation(archive)) {
    productService.archive(uid, params("filter").toInt)
  }

  get("/watchlist/p/:page/c/:count", operation(watchlist)) {
    productService.watchlist(uid)
  }

  put("/dispatched/:sellId", operation(markAsDispatched)) {
    sellService.markAsDispatched(uid, params("sellId").toInt)
  }

  put("/tracking/:sellId", operation(addTracking)) {
    sellService.addTracking(uid, params("sellId").toInt, params("number"), params("courier"))
  }

  get("/:catId") {
    Marshallers.toJson(Services.inventory.getProducts(params("catId").toInt))
  }

  //  post("/note") {
  //
  //  }

  delete("/", operation(deleteMarked)) {
    productService.deleteMarked(uid, parsedBody.extract[List[Int]])
  }

  //  post("add_listing") {
  //    val user = fromSession(session.getId)
  //
  //  }
}