class OfferScope
  presets:
    hstep: 1
    mstep: 1
    ismeridian: false

  constructor: (@scope, @http, @uibModal) ->
    @withEndDate = false
    @data = {}
    @reset()

    @modal = false

  load: () =>
    @reset()


  reset: () =>
    @data =
      startAtDate: new Date()
      startAtTime: new Date()
      finishAtDate: new Date()
      finishAtTime: new Date()

  openModal: (productId, variantId) =>
    @reset()

    @data.productId = productId
    @data.variantId = variantId

    @modal = @uibModal.open {
      animation: true
      templateUrl: 'offerScope.html'
      scope: @scope
    }
  ok: () =>
    merge = (date, time) =>
      new Date(date.getFullYear(), date.getMonth(), date.getDate(), time.getHours(), time.getMinutes(), time.getSeconds()).getTime();
    dataToSend = {}
    dataToSend.start = merge(@data.startAtDate, @data.startAtTime)
    if (@withEndDate)
      dataToSend.end = merge(@data.finishAtDate, @data.finishAtTime)
    @http {method: "POST", url:"/api/admin/inventory/list/#{@data.productId}/#{@data.variantId}/offers/scopes", data: dataToSend}
    .success (data) =>
      toastr.success "Offer scope was created"
      @close()
    .error () =>
      toastr.error "Offer scope was not created"
      @close()


    console.log(JSON.stringify(dataToSend))

  close: () =>
    @modal.close()
    @modal = false


