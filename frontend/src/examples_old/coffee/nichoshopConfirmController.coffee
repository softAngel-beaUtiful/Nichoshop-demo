nichoshop.controller('confirmController', ["$scope", "$http", "$location", "$timeout", ($scope, $http, $location, $timeout) =>
  code = $location.path().substring(1)
  if (!code || code == '')
    code = "nothing"
  $http.get("/api/signup/confirm_email/" + code).success((data, status, headers, config) =>
    toastr.success("Email was successfully confirmed!", "Success!")
    $timeout(() =>
      console.log('success timeout')
      window.location.replace("/examples/index.html")
    , 2000)
  ). error( (data, status, headers, config) =>
    console.log('error!')
    toastr.error("Error: " + data.error, 'Error!')
    $timeout(() =>
      console.log('error timeout')
      window.location.replace("/examples/index.html")
    ,2000)
  )
])