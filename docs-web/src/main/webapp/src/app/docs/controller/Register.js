'use strict';

/**
 * Modal password lost controller.
 */
angular.module('docs').controller('Register', function ($scope, $uibModalInstance) {
  $scope.username = '';
  $scope.close = function() {
    $uibModalInstance.close();
  }

  $scope.register = function(username, email, password) {
    $uibModalInstance.close(username, email, password);
  };
});