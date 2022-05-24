class CustomerProduct
  @actions: {

  }

  data: {}
  constructor: (@scope, @http, @location) ->
    @productId = @location.search()["productId"]
    @variantId = @location.search()["variantId"]

    @http.get("/api/inventory/product/#{@productId}")
    .success (data) =>
      console.log(JSON.stringify(data))
      @data.product = angular.copy data
      price = data.variants[0].price
      @data.price = "" + Math.ceil(price/100) + "." + price%100
      @data.amount = data.variants[0].amount

      @data.qty = "1"
      @data.image = "/images/test.jpg"
    .error (data) =>
      if (data.error)
        toastr.error data.error
      else
        toastr.error "Some error happens while get product"
  getProductForCreate: () =>
    {
      productId: @productId
      variantId: @variantId
      qty: @data.qty
    }

  getCart: () ->
    @http.get("/api/cart/")
    .success (data) =>
      @scope.product.data = angular.copy data
      if (!@data.image)
        @data.image = "/images/test.jpg"
      if (!@data.qty)
        @data.qty = "1"
      else
        @data.qty = "" + @data.qty

      console.log("CustomerProduct(this) ------> #{JSON.stringify(@data)}")
    .error (data) ->
      toastr.error "Error"

