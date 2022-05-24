nichoshop
.controller('checkoutController', ["$scope", "$http", "$timeout","$location", ($scope, $http, $timeout, $location) =>
    $scope.sidebar = new Categories($scope, $http, $location)

#    $scope.product = new CustomerProduct($scope, $http, $location)
    $scope.cart = new CustomerCart($scope, $http, $location)
    $scope.purchase = new CustomerProductPurchase($scope, $http, $location)

    console.log("checkoutController was initialized!")
])