nichoshop
.controller('productController', ["$scope", "$http", "$timeout","$location", "$uibModal", ($scope, $http, $timeout, $location, $uibModal) =>
    $scope.bus.subscribe CustomerCart.actions.putToCart, "productController", () =>
      $location.path("/checkout").replace()

    $scope.sidebar = new Categories($scope, $http, $location)
    $scope.product = new CustomerProduct($scope, $http, $location)
#    $scope.purchase = new CustomerProductPurchase($scope, $http, $location)
    $scope.cart = new CustomerCart($scope, $http, $location)
    $scope.cancel = () =>
      $location.path("/").replace()
    $scope.auctions = new CustomerAuctions($scope, $http, $timeout, $location, $uibModal)
    $scope.offerScope = new CustomerOffers($scope, $http, $location, $uibModal)

    console.log("purchaseController was initialized!")
])