#angular
#.module('nichoshopApp', ['username-available','email-available','internationalPhoneNumber'])
#.directive 'clickPrevent', () ->
#  (scope, element, attrs) ->
#    element.on('click', (e) ->
#      console.log('click prevented')
#      e.preventDefault()
#      e.stopPropagation()
#      return
#    )
nichoshop
.controller('mainController', ["$scope", "$http", "$timeout", ($scope, $http, $timeout) =>
#  https://github.com/Bluefieldscom/intl-tel-input
#  https://github.com/mareczek/international-phone-number

  $scope.states = {
    signon: "signon"
    login: "login"
    confirmLocal: "confirmLocal"
    confirm: "confirm"
    loggedIn: "loggedIn"
    resetPasswordRequest: "resetPasswordRequest"
  }
  $scope.areas = {
    userSpace: "userSpace"
    adminSpace: "adminSpace"
  }
  $scope.defaultCentral = {
    name: ""
    init: () ->
      console.log('default init was called')
    finalize: () ->
      console.log('default finalize was called')
  }
  $scope.centrals = {
    admin: {
      inventory: {
        createProduct: {
          name:  "createProduct"
          init: () ->
            $scope.product = new Product($scope, $http)
          finalize: () ->
            delete $scope.product
        }
        listProducts: {
          name:  "listProducts"
          init: () ->
            $scope.products = new Products($scope, $http)
          finalize: () ->
            delete $scope.products
        }
      }
    }
    user: {
      inventory: {
        listProducts: {
          name: "listProductsForCustomer"
          init: (categoryId) ->
            $scope.products = new CustomerProducts($scope, $http)
            $scope.products.setCategoryId(categoryId)
          finalize: () ->
            delete $scope.products

        }
        buyItNowStep1: {
          name: "buyItNowStep1"
          init: (cartId, categoryId) ->
            $scope.buyItNow = new BuyItNow($scope, $http, $timeout)
            $scope.buyItNow.setCartId(cartId, categoryId)

          finalize: () ->
            delete $scope.buyItNow
        }
      }
    }
  }
  $scope.navbars = {
    admin: {
      inventory: {
        sidebars: [
          {
            name: "Create product"
            central: $scope.centrals.admin.inventory.createProduct
          }
          {
            name: "List products"
            central: $scope.centrals.admin.inventory.listProduct
          }
        ]
      }
    }
    user: {}
  }


  $scope.currentState = $scope.states.login
  $scope.currentArea = $scope.areas.userSpace
  $scope.currentCentral = $scope.defaultCentral
  $scope.currentSidebar = ""

  $scope.setState = (newState, resetRecapcha) =>
    $scope.currentState = newState
    if (resetRecapcha)
      $scope.capchaReady = false
      console.log('grecaptcha.reset was called')

  #    if (newState == $scope.states.signon)
  #      $timeout () =>
  #        console.log('called?')
  #        $("#phone").intlTelInput()

  $scope.signon = new Signon($scope, $http)
  $scope.login = new Login($scope, $http)
  $scope.userData = new UserData($scope, $http)
  $scope.restore = new RestorePassword($scope, $http)
  $scope.sidebar = new Categories($scope, $http)


  $scope.setupUserData = (data) =>
    $scope.userData.data = angular.copy(data)
    console.log(JSON.stringify($scope.userData.data))
    $scope.currentState = $scope.states.loggedIn

  $scope.resetUserData = () =>
    $scope.userData.data = null
    $scope.currentState = $scope.states.login

  $scope.passwordReset = () =>
    console.log('passwordReset was called')
    $scope.currentState = $scope.states.resetPasswordRequest
  $scope.setArea = (area) =>
    $scope.currentArea = area

  $scope.setCentral = (central) =>
    $scope.currentCentral.finalize()
    $scope.currentCentral = central

    args = [].slice.call(arguments).splice(1)
    $scope.currentCentral.init.apply($scope.currentCentral, args)

  $scope.setCentralWithoutFinalization = (central) =>
    $scope.currentCentral = central

    args = [].slice.call(arguments).splice(1)
    $scope.currentCentral.init.apply($scope.currentCentral, args)

  $scope.setSidebar = (sidebar) =>
    $scope.sidebar = sidebar

])