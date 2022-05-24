class CustomerProductList

  data: {}

  constructor: (@scope, @http, @location) ->
    filters = @location.search()["filters"]
    category = Utils.extractFilters(filters, "category")
    if (category && category.length)
      @setCategoryId(category[0])

  setCategoryId: (categoryId) ->
    @categoryId = categoryId
    @http.get("/api/inventory/list/" + categoryId)
    .success (data) =>
      @scope.products.data = angular.copy data

      setPriceAndImage = (item) =>
#        item.price = @randomPrice()
        item.image = "/images/test.jpg"
        price = item.variants[0].price
        item.price = "" + Math.ceil(price/100) + "." + price%100

      setPriceAndImage(item) for item in @scope.products.data

    .error (data) ->
      toastr.error "Cannot get products for selected category"


  randomPrice: () ->
    max = 1000
    min = 10

    Math.floor(Math.random() * (max - min + 1)) + min + 0.95

  productDescription: (productId, variantId) ->
    @location.search("productId", productId)
    @location.search("variantId", variantId)
    @location.path("/product").replace()
