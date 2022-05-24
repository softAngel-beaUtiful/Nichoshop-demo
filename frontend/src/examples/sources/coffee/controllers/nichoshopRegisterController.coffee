nichoshop
.controller('registerController', ["$scope", "$http", "$location", "$timeout", "vcRecaptchaService", ($scope, $http, $location, $timeout, vcRecaptchaService) =>
    $scope.vcRecaptchaService = vcRecaptchaService
    $scope.signon = new Signon($scope, $http, $location)

])