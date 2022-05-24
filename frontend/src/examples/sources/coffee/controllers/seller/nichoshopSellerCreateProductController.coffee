nichoshop
.controller('sellerCreateProductController', ["$scope", "$http", "$timeout","$location", ($scope, $http, $timeout, $location) =>
#    $scope.sidebar = new Categories($scope, $http, $location)
#    $scope.products = new CustomerProductList($scope, $http, $location)
    $scope.product = new Product($scope, $http, $timeout)

    console.log("sellerMainController was initialized!")
])