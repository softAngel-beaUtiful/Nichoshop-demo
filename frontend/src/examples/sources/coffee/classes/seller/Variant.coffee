class Variant
  constructor: (@scope, @http, @uibModal) ->
    @data = {}
    @productId = false
    @reset()

    @modal = false

  reset: () =>
    @data = {
      title: ""
      description: ""
      price: ""
      amount: ""
      condition: "NEW"
    }

  createVariant: () ->
    submitData = angular.copy(@data)
    submitData.price = Math.ceil(submitData.price * 100)

    console.log(JSON.stringify(submitData))
    @http.post("/api/admin/inventory/list/#{@productId}", submitData).
    success (data) ->
      toastr.success("Variant was created!")
    .error () ->
      toastr.error("Variant was not created!")

  openModal: (productId) =>
    @reset()
    @productId = productId

    @modal = @uibModal.open {
      animation: true
      templateUrl: 'createVariant.html'
      scope: @scope
      size: 'lg'
    }

  ok: () =>
    @createVariant()
    @close()

  close: () =>
    @modal.close()
    @modal = false



