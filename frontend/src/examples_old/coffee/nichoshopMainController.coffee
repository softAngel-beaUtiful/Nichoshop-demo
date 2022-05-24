angular.module('nichoshopApp',  ['username-available','email-available','internationalPhoneNumber'])
.directive('clickPrevent', () ->
  (scope, element, attrs) ->
    element.on('click', (e) ->
      console.log('click prevented')
      e.preventDefault()
      e.stopPropagation()
      return
    )

)
.controller('mainController', ["$scope", "$http", "$timeout", ($scope, $http, $timeout) =>
#  https://github.com/Bluefieldscom/intl-tel-input
#  https://github.com/mareczek/international-phone-number
  class Navigation
    @spaces: {
      visitorSpace: {
        name: "visitor"
      }
      userSpace: {
        name: "user"
      }
      adminSpace: {
        name: "admin"
        navbars: [
          {
            name: "Inventory"
            sidebars: [
              {
                name: "Create product"
                init: (scope) ->
                  scope.product = new Product
                finalize: (scope) ->
                  delete scope.product
              }
            ]
          }
        ]
      }
    }

  class Signon
    data: {}
    phoneError: true

    constructor: (@scope, @http) ->

    registerSubmit2: () ->
#      @data.phone = $("#phone").intlTelInput("getNumber")
      console.log(JSON.stringify(@data))

    checkPhone: () ->
      @phoneError = (!@data.phone || @data.phone == '')

    registerSubmit: () ->
      @data.grecaptcha = grecaptcha.getResponse()
      console.log(JSON.stringify(@data))
      @http.post("/api/signup/", @data).success((data, status, headers, config) =>
        @data = {}
        if (data?.confirmation_code)
          @data.confirmation = {
            code: data.confirmation_code
          }
          @scope.currentState = @scope.states.confirmLocal
        else
          @data.confirmation = {
            email: data.email
          }
          @scope.currentState = @scope.states.confirm
        console.log('success! ' + JSON.stringify(data))
        toastr.success("Registration was successfull!", "Success!")
      ). error( (data, status, headers, config) =>
        console.log('error!')
        toastr.error("Error: " + data.error, 'Error!')
      )
    confirm: () ->
      $http.get("/api/signup/confirm_email/" + @data.confirmation.code).success((data, status, headers, config) =>
        toastr.success("Email was successfully confirmed!", "Success!")
        @scope.currentState = @scope.states.login
      ). error( (data, status, headers, config) =>
        console.log('error!')
        toastr.error("Error: " + data.error, 'Error!')
      )
  class Login
    data: {}

    capchaRequired: false

    constructor: (@scope, @http) ->

    loginSubmit: () ->
      console.log("data: " + JSON.stringify(@data))
      @http.defaults.headers.common['Authorization'] = 'Basic ' + btoa(@data.login + ':' + @data.password)
      #      console.log("grecaptcha: " + grecaptcha.getResponse())
      rememberMe = ""
      if (@data.rememberMe)
        rememberMe = "yes"
      grecapcha = ""
      if (@capchaRequired)
        grecapcha = grecaptcha.getResponse()
      @http(
        method: 'POST'
        url: "/api/sessions"
        data: $.param({grecaptcha: grecapcha, rememberMe: rememberMe})
        headers: {'Content-Type': 'application/x-www-form-urlencoded'}
      ).
      success((data, status, headers, config) =>
        console.log(JSON.stringify(data))
        @scope.setupUserData(data)
        toastr.success("Logged in as " + @scope.userData.data.first_name + ' ' + @scope.userData.data.last_name, "Success!")
      ).
      error((data, status, headers, config) =>
        toastr.error("Error: " + data.error, 'Error!')
        @setCaptchaRequired(data["grecaptcha-required"])
      )
    setCaptchaRequired: (value) =>
      console.log("set capchaRequired to " + value)
      if (value == "true")
        @capchaRequired = true
      else
        @capchaRequired = false

  class UserData
    data: null

    constructor: (@scope, @http) ->
      @http.get("/api/sessions/status").success((data, status, headers, config) =>
        @scope.setupUserData(data)
      ).error((data, status, headers, config) =>
        console.log("not logged in")
        @scope.login.setCaptchaRequired(data["grecaptcha_required"])
      )
    logout: () ->
      @http.post("/api/sessions/logout").success((data, status, headers, config) =>
        @scope.resetUserData()
      )

  class RestorePassword
    data: {
      phone: ""
      email: ""
      makeACall: false
      sendSMS: false
    }
    constructor: (@scope, @http) ->

    restorePassword: () =>
      console.log(JSON.stringify(@data))
      dataToSend = {}
      if (@data.email != '')
        dataToSend.userid = @data.email
      else
        dataToSend.phone = @data.phone
        if (@data.makeACall)
          dataToSend.asSms = false
        else
          dataToSend.asSms = true
      @http.post("/api/user/restore", dataToSend).success((data, status, headers, config) =>
        toastr.success("Accepted")
        if (dataToSend.phone)
          $timeout(() =>
            window.location.replace("/phonecode.html")
          ,2000)

      ).error((data, status, headers, config) =>
        console.log(JSON.stringify(data))
        toastr.error(data.error)
      )

  class Categories
    data: {}
    lastId: false

    constructor: () ->
      $http.get("/api/category/sidebar2").

      success((data) ->
#        console.log(JSON.stringify(data))
        $scope.sidebar.data = data
      )
    showProducts: (categoryId) ->

  class Product
    data: {}

    categories: [
      {
        id: 0
        title: "Root Category"
      }
    ]
    children: []

    currentChildren: []

    constructor: () ->
      @getChildren()

    chooseCategory: (id) ->
      i = _.findIndex(@categories, (o) ->
        o.id == id
      )
      @categories.splice i + 1, @categories.length
      @getChildren(@categories[i].id)

    getChildren: (id = 0) ->
      @currentChildren = []
      i = _.findIndex(@children, (o) ->
        o.id == id
      )
      if (i == -1)
        $http.get("/api/admin/category/children/" + id).
        success((data) ->
          if (data.children)
            data = data.children
          if (!_.isArray(data))
            data = []
          console.log('got children: ' + JSON.stringify(data))
          $scope.product.children.splice $scope.product.children.length, 0, {
            id: id
            children: angular.copy data
          }
          console.log(JSON.stringify($scope.product.children))
          $scope.product.getChildren(id)
        )
      else
        @currentChildren = @children[i].children

    chooseSubCategory: (id) ->
      i = _.findIndex(@currentChildren, (o) ->
        o.category.id == id
      )

      @categories.splice @categories.length, 0, {
        id: @currentChildren[i].category.id
        title: @currentChildren[i].category.name
      }

      @getChildren(@currentChildren[i].category.id)

    createProduct: () ->
      submitData = angular.copy(@data)
      submitData.categoryId = @categories[@categories.length - 1].id

      $http.post("/api/admin/inventory/list", submitData).
      success((data) ->
        toastr.success("Product was created!")
      ).error(() ->
        toastr.error("Product was not created!")
      )


  class Products
    data: {}

    constructor: () ->
      $http.get("/api/admin/inventory/list").
      success((data) ->
        $scope.products.data = angular.copy data

        console.log('---> ' + JSON.stringify($scope.products.data))
      ).
      error(() ->

      )

  class CustomerProducts
    data: {}

    constructor: () ->

    setCategoryId: (categoryId) ->
      @categoryId = categoryId
      console.log('---> category id set to [' + categoryId + ']')
      $http.get("/api/inventory/list/" + categoryId).success((data) ->
        $scope.products.data = angular.copy data
        setPriceAndImage = (item) ->
          item.price = $scope.products.randomPrice()
          item.image = "/images/test.jpg"
          console.log(JSON.stringify(item))
        setPriceAndImage(item) for item in $scope.products.data
      ).error((data) ->
        toastr.error("Cannot get products for selected category")
      )
    randomPrice: () ->
      max = 1000
      min = 10

      Math.floor(Math.random() * (max - min + 1)) + min + 0.95
    buyItNow: (productId, variantId) ->
      console.log("Got request to add to cart #{productId}:#{variantId}")
      $http.post("/api/cart/", {productId: productId, productVariantId: variantId})
      .success (data) ->
        toastr.success "Product was added to cart"
        $scope.setCentral($scope.centrals.user.inventory.buyItNowStep1, data.cartId, $scope.products.categoryId)
      .error () ->
        toastr.error "Some problem with adding product"

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

    constructor: () ->

    setCartId: (cartId, categoryId) ->
      @cartId = cartId
      @categoryId = categoryId

      $http.get("/api/cart/" + cartId).success (data) ->
        $scope.buyItNow.data = angular.copy(data)
      .error () ->
        toastr.error("Items not found")
        $timeout () ->
          $scope.setCentral($scope.centrals.user.inventory.listProducts, $scope.products.categoryId)

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
            $scope.product = new Product
          finalize: () ->
            delete $scope.product
        }
        listProducts: {
          name:  "listProducts"
          init: () ->
            $scope.products = new Products
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
            $scope.products = new CustomerProducts
            $scope.products.setCategoryId(categoryId)
          finalize: () ->
            delete $scope.products

        }
        buyItNowStep1: {
          name: "buyItNowStep1"
          init: (cartId, categoryId) ->
            $scope.buyItNow = new BuyItNow
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
  $scope.sidebar = new Categories


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