nichoshop
.controller('registerConfirmController', ["$scope", "$http", "$location", "$timeout", "vcRecaptchaService", ($scope, $http, $location, $timeout, vcRecaptchaService) =>

    $scope.o = {
      code: $location.search().code
    }
    if (!$scope.o.code)
      $scope.o.code = ""

    $scope.confirmCode = (code) =>
      console.log("$scope.code=#{$scope.o.code}")
      if ($scope.code != "")
        $http.get("/api/signup/confirm_email/" + $scope.o.code)
        .success (data, status, headers, config) =>
          toastr.success("Email was successfully confirmed!", "Success!")
          $timeout(() =>
            $location.path('/login').replace()
          , 2000)
        .error (data, status, headers, config) =>
          toastr.error("Error: " + data.error)
      else
        console.log()
    $scope.formSubmit = false
    if ($scope.o.code != "")
      $scope.confirmCode()
    else
      $scope.formSubmit = true
      $scope.o.code = ""

])