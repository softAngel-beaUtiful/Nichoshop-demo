nichoshop
.controller('indexController', ["$scope", "$http", "$timeout", ($scope, $http, $timeout) =>

  class Sidebar
    categories: []
    setup: () =>
      $http.get("/api/category/sidebar2").success((data, status, headers, config) =>
        console.log("success")
        console.log(JSON.stringify(data))
        @categories = data
      ).error( (data, status, headers, config) =>
        console.log("error")
      )

    constructor: (@scope, @http) ->
      @setup()

  $scope.sidebar = new Sidebar($scope, $http)
  console.log(">>>>>>>>>>>>>>> Hey!")

])