class BuyItNow
  data: {
    id: 0
    image: "/images/test.jpg"
    title: ""
    description: ""
    price: 0.0
    variants: [
      {
        id: 0
        title: ""
        description: ""
      }
    ]
  }

  constructor: (@scope, @http, @timeout) ->

  setCartId: (cartId, categoryId) ->
    @cartId = cartId
    @categoryId = categoryId

    s = @scope
    @http.get("/api/cart/" + cartId).success (data) ->
      s.buyItNow.data = angular.copy(data)
    .error () ->
      toastr.error("Items not found")
      @timeout () ->
        s.setCentral(s.centrals.user.inventory.listProducts, s.products.categoryId)