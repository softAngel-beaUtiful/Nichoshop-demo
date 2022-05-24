class CustomerOffers
  constructor: (@scope, @http, @location, @uibModal) ->
    @data = {}

    @productId = @location.search()["productId"]
    @variantId = @location.search()["variantId"]

    @modal = false

    @http { method: "GET", url: "/api/inventory/offers/#{@productId}/#{@variantId}" }
    .success (data) =>
      console.log("Get offers success")
      @data = angular.copy data
      console.log("@data for offer = #{JSON.stringify(@data)}")
      @data.amount = 0
      @data.qty = 1

    .error (data) =>
      console.log("Get offers error")

  isDefined: () => if (@data.id) then true else false

  openModal: () =>
    @modal = @uibModal.open {
      animation: true
      templateUrl: 'makeAnOffer.html'
      scope: @scope
    }

  amountToMoney: () =>

  offerSum: () =>
    if (@isDefined() && @data.amount > 0 && @data.qty > 0)
      (Money.fromDecimal(@data.amount).multiply(@data.qty)).asString()
    else
      ""

  ok: () =>
    dataToSend = angular.copy @data
    dataToSend.amount = Money.fromDecimal(@data.amount).amount
    dataToSend.qty = Math.floor(dataToSend.qty)
    console.log("Will create offer: #{JSON.stringify(dataToSend)}")

    @http {method: "POST", url: "/api/inventory/offers/#{@productId}/#{@variantId}", data: dataToSend}
    .success (data) =>
      if (data.accepted)
        toastr.success "Your offer was delivered!"
      else
        toastr.warning "Your offer was not delivered!"
      @modal.close()
      @modal = false
    .error () =>
      toastr.error "Your offer was not delivered because of error!"

  close: () =>
    @modal.close()
    @modal = false
