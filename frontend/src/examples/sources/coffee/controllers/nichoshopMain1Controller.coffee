nichoshop
.controller('main1Controller', ["$scope", "$http", "$timeout","$location", ($scope, $http, $timeout, $location) =>
    $scope.sidebar = new Categories($scope, $http, $location)
    $scope.products = new CustomerProductList($scope, $http, $location)

    console.log("main1Controller was initialized!")
])