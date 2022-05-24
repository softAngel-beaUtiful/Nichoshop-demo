nichoshop
.controller('fakePaymentController', ["$scope", "$http", "$timeout","$location", ($scope, $http, $timeout, $location) =>
    $scope.sidebar = new Categories($scope, $http, $location)

    $scope.purchase = new CustomerProductPurchase($scope, $http, $location)

    console.log("fakePaymentController was initialized!")
])