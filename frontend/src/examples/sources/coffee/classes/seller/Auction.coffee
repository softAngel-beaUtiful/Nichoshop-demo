class Auction
  resetData: () =>
    @data =
      productId: -1
      variantId: -1
      startAt: new Date()
      finishAt: (new Date() + 1)


  constructor: (@scope, @http, @timeout, @uibModal) ->
    @data = {}
    @minDate = new Date()
    @resetData()
    @modal = false

  openModal: (productId, variantId) =>
    @resetData()

    @data.productId = productId
    @data.variantId = variantId

    @modal = @uibModal.open {
      animation: true
      templateUrl: 'myModalContent.html'
      scope: @scope
    }
  ok: () =>
    console.log("ok was clicked => #{JSON.stringify(@data)}")
    dataToSend = angular.copy @data
    dataToSend.startAt = dataToSend.startAt.getTime()
    dataToSend.finishAt = dataToSend.finishAt.getTime()
    @http {url: "/api/seller/auction/list", method:"POST", data: dataToSend}
    .success (data) =>
      toastr.success "Auction was created successfully"
      @modal.close()
    .error (data) =>
      toastr.error "Auction was not created"
  close: () =>
    @modal.close()



