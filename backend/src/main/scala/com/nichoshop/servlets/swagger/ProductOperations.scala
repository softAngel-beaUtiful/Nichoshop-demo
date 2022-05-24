package com.nichoshop.servlets.swagger

import com.nichoshop.legacy.models.{Manifests, ProductsRow}


trait ProductOperations extends ApiDescription[ProductsRow] {
  def name: String = "Product"

  implicit def manifestForT: Manifest[ProductsRow] = Manifests.product

  val search = (apiOperation[List[ProductsRow]]("search")
    summary s"Search for Products containing str in title"
    tags "Product"
    notes "To search in all categories don't use catId param"
    parameters(queryParam[String]("str").description("string to search in product title").optional,
    queryParam[Int]("catId").description("category id to filter with").optional)
    )

  val bestProducts = (apiOperation[List[ProductsRow]]("bestProducts")
    summary s"Get 12 random products from Daily Deals category"
    tags "Product"
    )

  val active = (apiOperation[List[ProductsRow]]("active")
    summary s"Returns user's active sells"
    tags "Product"
    parameters(pathParam[Int]("filter").description("0 - all, 1 - fixed price, 2 - with open offers, 3 - auctions, 4 - classified ads"),
    pathParam[Int]("page").description("page number"),
    pathParam[Int]("count").description("sells per page"))
    )

  val sold = (apiOperation[List[ProductsRow]]("sold")
    summary s"Returns user's sold products"
    tags "Product"
    parameters(pathParam[Int]("filter").description("0 - all, 1 - awaiting payment, 2 - awaiting dispatch, 3 - dispatched, 4 - awaiting feedback"),
    pathParam[Int]("page").description("page number"),
    pathParam[Int]("count").description("sells per page"))
    )

  val unsold = (apiOperation[List[ProductsRow]]("unsold")
    summary s"Returns user's unsold products (expired)"
    tags "Product"
    parameters(pathParam[Int]("page").description("page number"),
    pathParam[Int]("count").description("sells per page"))
    )

  val archive = (apiOperation[List[ProductsRow]]("archive")
    summary s"Returns user's selling archive"
    tags "Product"

    parameters(pathParam[Int]("filter").description("0 - all, 1 - Price: highest first, 2 - Price: lowest first, 3 - Added: newest first, 4 - Added: oldest first"),
    pathParam[Int]("page").description("page number"),
    pathParam[Int]("count").description("sells per page"))
    )

  val deleteMarked = (apiOperation[Unit]("deleteMarked")
    summary s"Removes marked products"
    tags "Product"
    parameter bodyParam[List[Int]].description("Array of marked product ids")
    )

  val addTracking = (apiOperation[Unit]("addTracking")
    summary s"Add tracking number and courier t sell"
    tags "Product"
    parameters(formParam[String]("number").description("Tracking number"),
    formParam[String]("courier").description("Courier name"))
    )

  val markAsDispatched = (apiOperation[Unit]("markAsDispatched")
    summary s"Mark sell as dispatched"
    tags "Product"
    parameter pathParam[Int]("sellId").description("Sell id")
    )

  val watchlist = (apiOperation[List[ProductsRow]]("watchlist")
    summary s"Returns user's watchlist"
    tags "Product"
    parameters(pathParam[Int]("page").description("page number"),
    pathParam[Int]("count").description("sells per page"))
    )
}