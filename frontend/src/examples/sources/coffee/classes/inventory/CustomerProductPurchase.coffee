class CustomerProductPurchase
  constructor: (@scope, @http, @location) ->

  checkout: () ->
    if (@scope.product && @scope.product.data)
      @http.put("/api/cart", qty: parseInt(@scope.product.data.qty))
      .success () =>
        @location.path("/checkout").replace()
      .error () =>
        toastr.error "Some error during setting QTY"

  reserve: () =>
    @http.post("/api/cart/makeReservation")
    .success (data) =>
      if (data.reservationId)
#        toastr.success "Got reservation with ID #{data.reservationId}"
        @location.search("reservationId", data.reservationId)
        @location.path("/fakePayment").replace()
      else
        toastr.error "Reservation unsuccessful, nothing on cart or requested amount too high"
    .error (data) =>
      if (data.error)
        toastr.error "Reservation unsuccessful. Error: #{data.error}"
      else
        toastr.error "Reservation unsuccessful with unknown error"
  purchase: () =>
    reservationId = @location.search()["reservationId"]
    @http.post("/api/purchase/#{reservationId}")
    .success (data) =>
      toastr.success("Reservation #{reservationId} was purchased!")
    .error (data) =>
      toastr.error("Reservation #{reservationId} was not purchased")

  cancel: () =>
    @location.path("/").replace()
