angular.module('nichoshopApp').controller('phoneCodeController', ["$scope", "$http", "$location", "$timeout", ($scope, $http, $location, $timeout) =>
  class PhoneCode
    data: {}

    constructor: (@scope, @http, @timeout) ->
      console.log('constructor was called!')

    checkCode: () =>
      console.log('data: ' + JSON.stringify(@data))
      @http.post("/api/user/check_code", @data).success((data, status, headers, config) =>
        if (data.code)
          toastr.success("Password was successfully changed!", "Success!")
          @timeout(() =>
            console.log('success timeout')
            window.location.replace("/reset.html#/" + data.code)
          , 2000)
        else
          toastr.error("Something wrong...")
      ). error( (data, status, headers, config) =>
        console.log('error!')
        toastr.error("Error: " + data.error, 'Error!')
        @timeout(() =>
          console.log('error timeout')
          window.location.replace("/test.html")
        ,2000)
      )

  $scope.phoneCode = new PhoneCode($scope, $http, $timeout)

])