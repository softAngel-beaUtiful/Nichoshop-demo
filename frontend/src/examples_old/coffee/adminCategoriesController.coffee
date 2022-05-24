angular.module('nichoshopApp',  ['ui.tree']).controller('adminCategoriesController', ["$scope", "$http", "$timeout", ($scope, $http, $timeout) =>
  console.log('hello from adminCategoriesController!')

  $scope.log = (message, pure = false) =>
    if (typeof message == 'object')
      console.log("adminCategoriesController: ==============> OBJECT START")
      if (pure)
        console.log(message)
      else
        console.log(JSON.stringify(message))
      console.log("adminCategoriesController: ==============> OBJECT END")
    else
      console.log("adminCategoriesController: " + message)



  class CateroriesList
    data: {}

    constructor: (@scope, @http, @timeout) ->
      @http.get("/api/admin/category/all").success((data) ->
        $scope.categories.data = angular.copy(data)
        $scope.log($scope.categories.data)
      ).error((data) ->
        toastr.error("Something bad happens..")
      )



  $scope.categories = new CateroriesList($scope, $http, $timeout)
])