class CustomerAuctions
  @defaultBidData =
    amount: 1
  constructor: (@scope, @http, @timeout, @location, @uibModal) ->
    @productId = @location.search()["productId"]
    @variantId = @location.search()["variantId"]

    @bid = {}

    @data = []

    @updateData()
    @updateDataTimeout = false

    @modal = false

  updateData: () =>
    if (@updateDataTimeout)
      @timeout.cancel(@updateDataTimeout)
      @updateDataTimeout = false
    @http { method: "GET", url: "/api/auction/list?productId=#{@productId}" }
    .success (data) =>
      console.log("item auctions were updated => #{JSON.stringify(data)}")
      newData = angular.copy data

      updateItem = (item) =>
        item.currentPrice = @_price(item.currentPrice)
        item.finishAt = new Date(item.finishAt)
        if (!item.attendie)
          item.attendie = {}
        else
          item.attendie.amount = @_price(item.attendie.maxBid)

      updateItem(auction) for auction in newData
      @data = newData
      @scheduleNextUpdate()

    .error (data) =>
      # that's bad
      console.log("Error ===> #{JSON.stringify(data)}")
      @scheduleNextUpdate()

  scheduleNextUpdate: () =>
    @updateDataTimeout = @timeout () =>
      @updateData()
    , 5000

  _price: (p) =>
    high = Math.floor(p / 100)
    low = p % 100

    high =
      if high > 0
        "" + high
      else
        "0"
    low =
      if low > 9
        "" + low
      else
        "0" + low
    res = "#{high}.#{low}"
    console.log("convert [#{p}] to [#{res}]")
    res

  reset: () =>
    @bid = angular.copy CustomerAuctions.defaultBidData

  openModal: (auction) =>
    console.log("Will make a bid for auction: #{JSON.stringify(auction)}")
    @reset()
    @bid.auction = auction

    @modal = @uibModal.open {
      animation: true
      templateUrl: 'makeABid.html'
      scope: @scope
    }
  ok: () =>
    console.log("Will create bid: #{JSON.stringify(@bid)}")

    @http {method: "POST", url: "/api/auction/list/#{@bid.auction.id}/bid", data: {amount: Math.floor(@bid.amount * 100)}}
    .success () =>
      toastr.success "Your bid was accepted!"
      @modal.close()
      @modal = false
    .error () =>
      toastr.error "Your bid was not accepted!"

  close: () =>
    @modal.close()
    @modal = false
