class CustomerProducts
  data: {}

  constructor: (@scope, @http, @location) ->
    filters = @location.search()["filters"]
    category = Utils.extractFilters(filters, "category")
    if (category && category.length)
#      console.log("Category: #{JSON.stringify(category)}")
      @setCategoryId(category[0])

  setCategoryId: (categoryId) ->
    @categoryId = categoryId
    s = @scope
    @http.get("/api/inventory/list/" + categoryId)
    .success (data) ->
      s.products.data = angular.copy data
      setPriceAndImage = (item) ->
        item.price = s.products.randomPrice()
        item.image = "/images/test.jpg"
#        console.log(JSON.stringify(item))
      setPriceAndImage(item) for item in s.products.data
    .error (data) ->
      toastr.error("Cannot get products for selected category")


  randomPrice: () ->
    max = 1000
    min = 10

    Math.floor(Math.random() * (max - min + 1)) + min + 0.95

  buyItNow: (productId, variantId) ->
#    console.log("Got request to add to cart #{productId}:#{variantId}")
    s = @scope
    @http.post("/api/cart/", {productId: productId, productVariantId: variantId})
    .success (data) =>
#      toastr.success "Product was added to cart"
      @location.path('/purchase').replace()
    .error () ->
      toastr.error "Some problem with adding product"

  productDescription: (productId, variantId) ->
    @location.path("/")

    s = @scope
    @http.post("/api/cart/", {productId: productId, productVariantId: variantId})
    .success (data) =>
#      toastr.success "Product was added to cart"
      @location.path('/purchase').replace()
    .error () ->
      toastr.error "Some problem with adding product"