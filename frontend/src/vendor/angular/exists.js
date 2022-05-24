angular.module( 'username-available', [] ).directive('usernameAvailable', function($timeout, $q, $http) {
    return {
        restrict: 'AE',
        require : 'ngModel',
        link: function(scope, elm, attr, model) {
            model.$asyncValidators.usernameExists = function(username) {
                return $http.get('/api/signup/check_userid/' + username).then(function(res){
                    $timeout(function(){
                        model.$setValidity('usernameExists', !res.data.exists);
                    }, 1000);
                });
            };
        }
    }
});
angular.module( 'email-available', [] ).directive('emailAvailable', function($timeout, $q, $http) {
    return {
        restrict: 'AE',
        require : 'ngModel',
        link: function(scope, elm, attr, model) {
            model.$asyncValidators.emailExists = function(email) {
                return $http.get('/api/signup/check_email/' + email).then(function(res){
                    $timeout(function(){
                        console.log('res.data.exists: ' + res.data.exists);
                        model.$setValidity('emailExists', !res.data.exists);
                    }, 1000);
                });
            };
        }
    }
});
angular.module( 'phone-ok', [] ).directive('phoneOk', function($timeout, $q, $http) {
    return {
        restrict: 'AE',
        require : 'ngModel',
        link: function(scope, elm, attr, model) {
            model.$asyncValidators.phoneOk = function(phone) {
               return $timeout(model.$setValidity('phoneOk', phone != ''), 500);
            };
        }
    }
});
