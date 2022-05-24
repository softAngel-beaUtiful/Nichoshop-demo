nichoshop
.controller('resetController', ["$scope", "$http", "$location", "$timeout", ($scope, $http, $location, $timeout) =>
  class ResetPassword
    data: {}

    constructor: (@scope, @http, @timeout) ->

    resetPasswordSubmit: () =>
      console.log('data: ' + JSON.stringify(@data))
      @http.post("/api/user/reset_password", @data).success((data, status, headers, config) =>
        toastr.success("Password was successfully changed!", "Success!")
        @timeout(() =>
          console.log('success timeout')
          $location.path("/").replace()
        , 2000)
      ). error( (data, status, headers, config) =>
        toastr.error("Error: " + data.error, 'Error!')
        @timeout(() =>
          console.log('error timeout')
          $location.path("/").replace()
        ,2000)
      )

  $scope.resetPassword = new ResetPassword($scope, $http, $timeout)
  $scope.resetPassword.data.code = $location.search().code
  if (!$scope.resetPassword.data.code)
    $scope.resetPassword.data.code = ""




])