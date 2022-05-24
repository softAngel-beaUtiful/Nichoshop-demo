class Products

  constructor: (@scope, @http) ->
    @data = {}
    @filters =
      condition: "ALL"
    @refresh()

  setWatches: (name) =>
    @scope.$watchCollection "#{name}.filters.condition", () =>
      @refresh()



  refresh: () =>
    filters = ("#{k}=#{v}" for k, v of @filters).join("&")

    @http.get("/api/admin/inventory/list?#{filters}")
    .success (data) =>
      newData = angular.copy data
      for product in newData
        for variant in product.variants
          variant.price = new Money(variant.price)
          variant.offers = []
          for offerScope in variant.offerScopes ? []
            for offer in offerScope.offers
              offer.price = new Money(offer.amount)
              offer.created = new Date(offer.timestamp)
              variant.offers.push(offer)


      console.log('---> ' + JSON.stringify(newData))
      @data = newData
    .error () =>

  deleteVariant: (productId, variantId, deleteDisabled) =>
    if (!deleteDisabled)
      @http.delete("/api/admin/inventory/list/#{productId}/#{variantId}")
      .success () =>
        toastr.success "Variant was deleted"
        @refresh()
      .error () =>
        toastr.success "Variant was not deleted"

  price: (id) =>
    p =
      (product for product in @data when product.id == id)
    if (p.length && p[0].variants.length)
      v = p[0].variants[0]
      high = Math.floor(v.price / 100)
      low = v.price % 100
      "#{high}.#{low}"
    else
      "<unknown>"

  amount: (id) =>
    p =
      (product for product in @data when product.id == id)
    if (p.length && p[0].variants.length)
      v = p[0].variants[0]
      v.amount
    else
      "<unknown>"

  acceptOffer: (productId, variantId, id) =>
    console.log("Ask to accept offer for id #{id}")
    @http {method: "PUT", url: "/api/admin/inventory/list/#{productId}/#{variantId}/offers/offer/#{id}/accept"}
    .success (data) =>
      toastr.success "Offer was successfully accepted"
      for product in @data when product.id == productId
        for variant in product.variants when variant.id == variantId
          for offer in variant.offers when offer.id == id
            variant.amount = variant.amount - offer.qty
            offer.accepted = true

    .error () =>
      toastr.error "Offer was not rejected"

  rejectOffer: (productId, variantId, id) =>
    console.log("Ask to reject offer for id #{id}")
    @http {method: "PUT", url: "/api/admin/inventory/list/#{productId}/#{variantId}/offers/offer/#{id}/reject"}
    .success (data) =>
      toastr.success "Offer was successfully rejected"
      for product in @data when product.id == productId
        for variant in product.variants when variant.id == variantId
          for offer in variant.offers when offer.id == id
            offer.rejected = true

    .error () =>
      toastr.error "Offer was not rejected"

