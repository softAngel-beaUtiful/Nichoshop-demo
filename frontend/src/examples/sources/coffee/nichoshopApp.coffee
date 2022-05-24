nichoshop = angular.module('nichoshopApp', ['ngRoute', 'username-available','email-available','internationalPhoneNumber','ui.tree', 'vcRecaptcha', 'ui.bootstrap'])

# http://midgetontoes.com/blog/2014/08/31/angularjs-check-user-login
# http://stackoverflow.com/questions/26993926/how-do-i-check-for-login-or-other-status-before-launching-a-route-in-angular-wit
checkLoggedIn = ['$q', '$timeout', '$http', '$location', '$rootScope', ($q, $timeout, $http, $location, $rootScope) ->
  deferred = $q.defer()
  isRecaptcha = (v) ->
    if (v == true || v == "true" || v == "yes")
      true
    else
      false

  $rootScope.hasPermission = (p) =>
    $rootScope.user && $rootScope.user.permissions && $rootScope.user.permissions.indexOf(p) != -1

  if $rootScope.user
    deferred.resolve()
  else
    $http.get '/api/sessions/status'
    .success (data) ->
#      console.log("angular.config data: #{JSON.stringify(data)}")
      if data
        $rootScope.user = angular.copy data

        $rootScope.navigation = new Navigation($location)
        $rootScope.logout = () =>
          delete $rootScope.user
          $http.post("/api/sessions/logout").success((data, status, headers, config) =>
            toastr.success "Logged out"
            $location.path("/login").replace()
          )
        $timeout(deferred.resolve)
      else
        $rootScope.recaptchaRequired = isRecaptcha(data.grecaptcha_required)
#        if !data.grecaptcha_required
#          $rootScope.recaptchaRequired = false
#        else
#          $rootScope.recaptchaRequired = true

        delete $rootScope.user
        if ($location.url() != '/login')
          $timeout(deferred.reject)
          $location.url('/login')
        else
          $timeout(deferred.resolve)
    .error (data) ->
#      console.log("angular.config data: #{JSON.stringify(data)}")
      $rootScope.recaptchaRequired = isRecaptcha(data.grecaptcha_required)
#      if !data.grecaptcha_required
#        $rootScope.recaptchaRequired = false
#      else
#        $rootScope.recaptchaRequired = true

      delete $rootScope.user
      if ($location.url() != '/login')
        $timeout(deferred.reject)
        $location.url('/login')
      else
        $timeout(deferred.resolve)

    deferred.promise
]

nichoshop
.config ['$routeProvider', ($routeProvider) ->

  $routeProvider
  .when '/', {
    templateUrl: 'views/main.html'
    controller: 'main1Controller'
    resolve:
      loggedIn: checkLoggedIn

  }
  .when '/login', {
    templateUrl: 'views/login.html'
    controller: 'loginController'
    resolve:
      loggedIn: checkLoggedIn
  }
  .when '/passwordReset', {
    templateUrl: 'views/passwordReset.html'
    controller: 'resetRequestController'
  }
  .when '/reset', {
    templateUrl: 'views/reset.html'
    controller: 'resetController'
  }
  .when '/register', {
    templateUrl: 'views/register.html'
    controller: 'registerController'
  }
  .when '/registerConfirm', {
    templateUrl: 'views/registerConfirm.html'
    controller: 'registerConfirmController'
  }

  .when '/purchase', {
    templateUrl: 'views/product.html'
    controller: 'purchaseController'
  }
  .when '/product', {
    templateUrl: 'views/product.html'
    controller: 'productController'
  }

  .when '/checkout', {
    templateUrl: 'views/checkout.html'
    controller: 'checkoutController'
  }
  .when '/fakePayment', {
    templateUrl: 'views/fakePayment.html'
    controller: 'fakePaymentController'
  }


  .when '/seller', {
    templateUrl: 'views/seller/main.html'
    controller: 'sellerMainController'
  }
  .when '/seller/product', {
    templateUrl: 'views/seller/createProduct.html'
    controller: 'sellerCreateProductController'
  }
  .when '/seller/products', {
    templateUrl: 'views/seller/listProducts.html'
    controller: 'sellerListProductsController'
  }


  .when '/admin', {
    templateUrl: 'views/admin/main.html'
    controller: 'adminMainController'
  }
  .when '/admin/manageCategories', {
    templateUrl: 'views/admin/manageCategories.html'
    controller: 'adminManageCategoriesController'
  }

  .otherwise {
    redirectTo: '/'
  }

]

nichoshop
.directive 'clickPrevent', () ->
  (scope, element, attrs) ->
    element.on('click', (e) ->
#      console.log('click prevented')
      e.preventDefault()
      e.stopPropagation()
      return
    )

nichoshop.factory('Bus', () ->
  subscribers = {}

  {
    subscribe: (action, subscriber, callback) =>
      console.log("request for subscription from [#{subscriber}] for action [#{action}]")

      if (_.isFunction(callback))
        if (!subscribers[action])
          console.log("registered [#{subscriber}] for action [#{action}]")
          subscribers[action] = {}

      subscribers[action][subscriber] = callback;
    broadcast: (action) =>
      console.log("broadcast message #{action}")
      if (subscribers[action])
        args = Array.prototype.slice.call(arguments)
        args.splice(0,1)
        for subscriber, obj of subscribers[action]
          try
            subscribers[action][subscriber].apply(this, args)
          catch err
            console.log('Error happens for subscription [' + action + '] for subscriber [' + subscriber + ']')
            console.log(err)


  }
)

# http://stackoverflow.com/questions/19697004/angularjs-how-to-redirect-back-to-the-next-route-after-logging-in
nichoshop
.run ['$location', '$rootScope', 'Bus', ($location, $rootScope, bus) ->
  routeWithRequiredLogin = ''
  $rootScope.bus = bus
  ## DISABLED

#  $rootScope.$on '$routeChangeStart', (event, nextRoute, currentRoute) ->
#    if (nextRoute.isLogin && currentRoute && currentRoute.templateUrl != '' && !currentRoute.isLogin)
#      routeWithRequiredLogin = $location.path()
#    else if (currentRoute && currentRoute.isLogin && routeWithRequiredLogin != '')
#      $location.path(routeWithRequiredLogin).replace()
#      routeWithRequiredLogin = ''
#    else
#      routeWithRequiredLogin = ''
]


