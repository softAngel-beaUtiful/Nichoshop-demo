class CustomerCart
  @actions: {
    putToCart: "CustomerCart.putToCart"
  }

  data: {}

  constructor: (@scope, @http, @location) ->
    @http.get("/api/cart/")
    .success (data) =>
      @data = angular.copy data
      if (!@data.image)
        @data.image = "/images/test.jpg"
      if (!@data.qty)
        @data.qty = "1"
      else
        @data.qty = "" + @data.qty
    .error (data) ->
      toastr.error "Error"

  putToCart: (product) ->
    if (product.productId && product.variantId && product.qty)
      @http.post("/api/cart/", {productId: product.productId, productVariantId: product.variantId, qty: parseInt(product.qty)})
      .success (data) =>
        @scope.bus.broadcast(CustomerCart.actions.putToCart)
      .error () ->
        toastr.error "Some problem with adding product"
