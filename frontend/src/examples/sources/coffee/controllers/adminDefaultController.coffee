nichoshop
.controller('adminDefaultController', ["$scope", "$http", "$timeout", ($scope, $http, $timeout) =>
  console.log('hello from adminDefaultController!')

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

])