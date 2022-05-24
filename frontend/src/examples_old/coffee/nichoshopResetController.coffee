angular.module('nichoshopApp').controller('resetController', ["$scope", "$http", "$location", "$timeout", ($scope, $http, $location, $timeout) =>
  class ResetPassword
    data: {}

    constructor: (@scope, @http, @timeout) ->
      console.log('constructor was called!')

    resetPasswordSubmit: () =>
      console.log('data: ' + JSON.stringify(@data))
      @http.post("/api/user/reset_password", @data).success((data, status, headers, config) =>
        toastr.success("Password was successfully changed!", "Success!")
        @timeout(() =>
          console.log('success timeout')
          window.location.replace("/test.html")
        , 2000)
      ). error( (data, status, headers, config) =>
        console.log('error!')
        toastr.error("Error: " + data.error, 'Error!')
        @timeout(() =>
          console.log('error timeout')
          window.location.replace("/test.html")
        ,2000)
      )

  $scope.resetPassword = new ResetPassword($scope, $http, $timeout)
  $scope.resetPassword.data.code = $location.path().substring(1)
  if (!$scope.resetPassword.data.code || $scope.resetPassword.data.code == '')
    $scope.resetPassword.data.code = "nothing"

])