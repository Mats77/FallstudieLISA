//Konfiguration der App und Services, factories; Deklarierung der benötigten services, module usw.
	var lisaApp = angular.module('LISA',['ngRoute', 'ngAnimate','ui.bootstrap', 'ngCookies','chieffancypants.loadingBar', 'notifications']);

  //ermöglicht verschiedene views
	lisaApp.config(function($routeProvider) {
    $routeProvider

      // route for the "login" page
      .when('/', {
        templateUrl : 'templates/login.html',
        controller  : 'loginController'
      })
      
      .when('/lobby',{
        templateUrl: 'templates/lobby.html',
        controller : 'lobbyController'
      })

      .when('/inbetween',{
        templateUrl: 'templates/inbetween.html',
        controller : "betweenRoundsController"
      })
      // route for the main game
      .when('/main', {
        templateUrl : 'templates/main.html',
        controller  : 'mainController'
      })
  });

lisaApp.factory('pollingService', ['$http', function($http){
        var defaultPollingTime = 3000;
        var polls = {};
        var serverBackendURL = "http://localhost:8080/"

        return {
            startPolling: function(name, pollingReason, payloadAndStuff, pollingTime, callback) {
                // Check to make sure poller doesn't already exist
                if (!polls[name]) {
                    var poller = function() {
                      $http.get(serverBackendURL, {params: { reason: pollingReason, payload: payloadAndStuff}}).then(callback);
                    }
                    poller();
                    polls[name] = setInterval(poller, pollingTime || defaultPollingTime);
                }
            },

            stopPolling: function(name) {
                clearInterval(polls[name]);
                delete polls[name];
            }
        }
    }]);

     