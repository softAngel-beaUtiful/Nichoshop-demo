nichoshop
.controller('adminManageCategoriesController', ["$scope", "$http", "$timeout","$location", "$uibModal", ($scope, $http, $timeout, $location, $uibModal) =>
#    $scope.sidebar = new Categories($scope, $http, $location)
#    $scope.products = new CustomerProductList($scope, $http, $location)
#    $scope.categories = new DnDCategoriesList($scope, $http, $timeout)
    $scope.cl = new CategoryList($scope, $http, $uibModal)

    console.log("adminManageCategoriesController was initialized!")
])