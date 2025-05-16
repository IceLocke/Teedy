'use strict';

/**
 * Settings user page controller.
 */
angular.module('docs').controller('SettingsUser', function($scope, $state, Restangular) {
  /**
   * Load users from server.
   */
  $scope.loadUsers = function() {
    Restangular.one('user/list').get({
      sort_column: 1,
      asc: true
    }).then(function(data) {
      $scope.users = data.users;
    });
  };

  $scope.loadRegisterRequests = function() {
    Restangular.one('register-request/list').get().then(function(data) {
      $scope.registerRequests = data.requests;
    })
  }

  $scope.loadUsers();
  $scope.loadRegisterRequests();
  
  /**
   * Edit a user.
   */
  $scope.editUser = function(user) {
    $state.go('settings.user.edit', { username: user.username });
  };

  $scope.accept = function(id) {
    Restangular.one('register-request').post('accept', {
      id: id
    }).then(function(data){
      $scope.loadRegisterRequests();
      $scope.loadUsers();
    })
  }

  $scope.reject = function(id) {
    Restangular.one('register-request').post('reject', {
      id: id
    }).then(function(data){
      $scope.loadRegisterRequests();
      $scope.loadUsers();
    })
  }
});