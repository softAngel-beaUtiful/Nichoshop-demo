nichoshop
.controller('loginController', ["$scope", "$http", "$timeout", "$location", ($scope, $http, $timeout, $location) =>
    $scope.login = new Login($scope, $http, $location)
    console.log("Login controller was reset")
])
