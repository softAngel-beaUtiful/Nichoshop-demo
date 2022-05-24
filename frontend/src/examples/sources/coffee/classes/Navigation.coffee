class Navigation
  constructor: (@location) ->
    @seller = {
      goToCreateProduct: @__goToCreateProduct
      goToListProducts: @__goToListProducts
    }
    @admin = {
      goToManageCategories: @__goToManageCategories
    }

  goToSellerSpace: () ->
#    @location.url(@$location.path());
    @location.search('')
    @location.path("/seller").replace()

  goToBuyerSpace: () ->
#    @location.url(@$location.path());
    @location.search('')
    @location.path("/").replace()

  goToAdminSpace: () ->
#    @location.url(@$location.path());
    @location.search('')
    @location.path("/admin").replace()

# SELLER
  __goToCreateProduct: () =>
    @location.search('')
    @location.path("/seller/product").replace()
  __goToListProducts: () =>
    @location.search('')
    @location.path("/seller/products").replace()
# ADMIN
  __goToManageCategories: () =>
    @location.search('')
    @location.path("/admin/manageCategories").replace()



