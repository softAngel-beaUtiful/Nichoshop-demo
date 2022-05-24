nichoshop
.controller('sellerListProductsController', ["$scope", "$http", "$timeout","$location", "$uibModal", ($scope, $http, $timeout, $location, $uibModal) =>
#    $scope.sidebar = new Categories($scope, $http, $location)
#    $scope.products = new CustomerProductList($scope, $http, $location)

    watches = []
    $scope.products = new Products($scope, $http, $timeout)

    watches.push $scope.products.setWatches("products")

    $scope.auction = new Auction($scope, $http, $timeout, $uibModal)
    $scope.offerScope = new OfferScope($scope, $http, $uibModal)
    $scope.product = new Product($scope, $http, $uibModal)
    $scope.variant = new Variant($scope, $http, $uibModal)

    $scope.$on "$destroy", () =>
      console.log "destroy was called on sellerListProductsController"
      for deregister in watches
        deregister()


    console.log("sellerMainController was initialized!")
])