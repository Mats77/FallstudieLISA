lisaApp.controller("loginController", function ($scope, $http, $cookieStore, $location, cfpLoadingBar, $notification, pollingService) { //

    $notification.success("Willkommen","Willkommen bei LISA! Bitte trag' deine Daten ein");
    //Debug: Entferne alle Polling-Aufträge
    pollingService.stopPolling("activePlayers");
    pollingService.stopPolling("dash");
    pollingService.stopPolling("chat");
    pollingService.stopPolling("readyPlayers");
    //Nur ein Akkordion-Element darf gleichzeitig auf sein
    $scope.oneAtATime = true;
    $scope.chosenAvatar = "img/photo.jpg";

    $scope.userData = {};
    //URL zum Backend: wenn Änderungen nötig sind, entweder CORS im Browser abschalten oder den Accepted-Header in Server.java ändern
     var serverBackendURL = "http://digifurt.de:8080";

    //PHP-Krücke zum Anzeigen der Logos, TODO!
    $http.get('/lisa/json/avatars.php').success(function (data) {
        $scope.avatars = data;
    });

    //Faulheit FTW!
    $scope.debugRemoveAllCookies = function () {
        $cookieStore.remove('authCookie');
        $cookieStore.remove('lisaAvatar');
        $notification.warning("Debug", "Cookies erfolgreich Entfernt");
        $scope.userData.name = "Airbus";
        $scope.userData.rounds = "12";
        $scope.userData.win = "wintype2";
        $scope.setImage("img/avatars/airbus.png");
        $notification.warning("Debug", "User-Daten gesetzt");
    }

    $scope.setImage = function (imageUrl) {
        $scope.chosenAvatar = imageUrl;
        $cookieStore.put('lisaAvatar', imageUrl);

    }
    $scope.checkOut = function (user) {
        var authCookie = $cookieStore.get('authCookie');

        //Erstelle eine Lokale Kopie der Userdaten, weil sonst Apocalypse
         $scope.userData = angular.copy(user);


        if (authCookie == null) {
            //TODO: Entferne Debugs
            $notification.warning("Debug",'authCookie ist nich vorhanden, feuere get-Request ab');
            $http.get( serverBackendURL, {params: { reason: "AUTHORIZEME$"}}).success(function(data){
                $notification.warning("Debug","Antwort vom Auth-Request: "+data);
                var authData = data;
                $cookieStore.put('authCookie', authData);
                $notification.warning("Debug","Im cookie steht: "+$cookieStore.get('authCookie'));
                $scope.startGame($cookieStore.get('authCookie'));
             })
            .error(function() {
            $notification.error("Technischer Fehler","Auth gescheitert, CORS oder Server läuft nicht");
            });
        } else {
            //Für den Fall, sollte der Spieler aus dem Spiel gekickt werden und sich mit dem intakten Cookie einzuloggen versucht
            $notification.warning("Debug","Auth-Cookie gefunden: verifiziere...");
            $http.get(serverBackendURL, {params: { reason: "VERIFY$", payload: authCookie+"$"}}).success(function(data){
                if (data.indexOf("OK")) {
                    $notification.warning("Debug","Cookie OK, leite weiter...");
                
                    $location.path("main");
                   
                    
            } else {
                $notification.warning("Debug","Böser Cookie! Schäm dich!");
                $cookieStore.remove('authCookie');
            }
            }).error(function(){$notification.error("Technischer Fehler","verifizierung gescheitert, CORS oder Server läuft nicht")});
        }   
          
    }

    $scope.startGame = function (cookie){


            function isEmpty(str) {
            return (!str || 0 === str.length);
        }
        //Checke alle Daten durch
        if (isEmpty($scope.userData.name) || 
            isEmpty($scope.userData.rounds) || 
            isEmpty($scope.userData.win)) { 
            //hier darf der wert des cookies nicht "undefined" sein!
            $notification.error("Fehler","Bitte setze alle erforderlichen Werte!")


        } else {
            //wenn alles ok, schicke an den server, leite bei positiver antwort auf die lobby/warteraum um
            $http.get(serverBackendURL, {params: {reason: "STARTGAME$", payload: cookie+":"+$scope.userData.name+":"+$scope.userData.rounds+":"+$scope.userData.win+"$" }})
            .success(function(data){
                if (data.indexOf("CHECKNICK") >= 0) {
                    //Username für Main/Chat/usw.
                    $cookieStore.put('lisaUserName', $scope.userData.name);
                    $location.path("lobby"); 
                } else {
                    $notification.error("Debug","kein OK vom Server");
                    //lösche den cookie bei negativer antwort um neuen versuch zuzulassen
                    $cookieStore.remove('authCookie');
                }
            })
            .error(function(){
                $notification.error("Debug","STARTGAME failed");
            });
        }
    }

});

lisaApp.controller("mainController", 
    function ($scope, $http, $cookieStore, $route, $location, $log, pollingService, $templateCache, cfpLoadingBar, $timeout, $notification) {

 /* *******Hauptcontroller***** */

    //Hole Username, Bild aus den cookies; setze Standarttemplate auf dashboard
    $scope.userName = $cookieStore.get('lisaUserName');
    $scope.avatar = $cookieStore.get('lisaAvatar');
    $scope.template = '/lisa/templates/dashboard.html';

    //Wirklich nötig?
    $scope.toggleNavbar = function(){
        $scope.navBarState = ($scope.navBarState) ? false : true;
        return $scope.navBarState;
    };

    $scope.isEmpty = function(str) {
            return (!str || 0 === str.length);
        }

    var serverBackendURL2 = "http://www.digifurt.de:8080";
    //Objekte für Usereingaben: R&D, Marketing usw.
    $scope.userEntry = {};
    $scope.userEntry.planePrice = "";
    $scope.userEntry.research = "";
    $scope.userEntry.marketing = "";
    $scope.userEntry.material = "";
    $scope.userEntry.capacity = "";

    //Hole Sales erst nach 5sec. verschnaufpause für den Server, damit er alle Spieler auch registriert
    $timeout(function() {
     $http.get( serverBackendURL2, {params: { reason: "GETSALES$", payload: $cookieStore.get('authCookie')+"$"}})
            .success(function(data){
                $scope.recentOrders = data; 
            })
            .error(function() {$notification.error("GetSales failed", "Verbindung kaputt");});    
    }, 5000);


   
        //Hole zufallsevents vom Server, 2sec später um nicht mit Notifications überflutet zu werden
        $http.get( serverBackendURL2, {params: { reason: "GETEVENT$", payload: $cookieStore.get('authCookie')+"$"}})
            .success(function(data){
                $scope.eventData = data; 
                if ($scope.eventData == "NOEVENT") {
                    $scope.eventData = "Keine Nachrichten sind manchmal gute Nachrichten.";
                };
                $timeout(function() {
                $notification.info("Nachrichten", $scope.eventData);                
            }, 7000);
                
            })
            .error(function() {$notification.error("GetSales failed", "Verbindung kaputt");});

        //TODO: entfernen wenn nicht mehr nötig
        $scope.debug = function(){};


        
    //umgehe HTML-Caching des Browsers und erzwinge reload der partials
    $scope.random = function(){
        return Math.round(new Date().getTime() / 1000 + Math.random()*100);
    }; 

    $scope.includes =
        [ { name: 'Dashboard', url: '/lisa/templates/dashboard.html?v='+$scope.random(), icon: 'dashboard'}
        , { name: 'Sales', url: '/lisa/templates/sales.html?v='+$scope.random(), icon: 'usd'} 
        , { name: 'R&D', url: '/lisa/templates/rd.html?v='+$scope.random(), icon: 'flask'} 
        , { name: 'Marketing', url: '/lisa/templates/marketing.html?v='+$scope.random(), icon: 'bullhorn'} 
        , { name: 'Procurement', url: '/lisa/templates/procurement.html?v='+$scope.random(), icon: 'shopping-cart'} 
        , { name: 'Manufacturing', url: '/lisa/templates/manufacturing.html?v='+$scope.random(), icon: 'wrench'} 
        , { name: 'Financials', url: '/lisa/templates/financials.html?v='+$scope.random(), icon: 'money'} 
        , { name: 'Chat', url: '/lisa/templates/chat.html?v='+$scope.random(), icon: 'comments'} 

        ];

    $scope.setTemplate = function (template) {
        $scope.template = template;
    };


    //Haupt-Poller, holt Dashboardstatistiken, Financials und Manufacturing-Daten alle 10sec
    pollingService.startPolling("dash", "GETBASICDASHBOARD$", $cookieStore.get('authCookie')+"$" , 10000, function(result){
             $scope.dashStatsC = result.data[0];
             $scope.dashStatOverview = result.data[1];
             $scope.costsOverview = result.data[2];
             $scope.earningsOverview = result.data[3];
             $scope.loans = result.data[4];
            $http.get( serverBackendURL2, {params: { reason: "GETPRODUCEORDERS$", payload: $cookieStore.get('authCookie')+"$"}})
            .success(function(data){
                $scope.manufacturingPipeline = data; 
            })
            .error(function() {$notification.error("GetProduction failed", "Verbindung kaputt");});
         });

    //Hole die aktuellen Chatnachrichten der anderen Spieler und entferne URL-Gewurstel
    pollingService.startPolling("chat", "CHATREFRESH$", $cookieStore.get('authCookie'), 10000, function(result){ 
           $scope.chatJSON = result.data;
           decodeURI($scope.chatJSON);
          });



     
    //TODO: "Netteres" Feedback geben
    $scope.sendChat = function(msg){
        var message = $cookieStore.get('authCookie')+":"+msg+":"+$scope.avatar+"$";
            $http.get(serverBackendURL2, {params: { reason: "CHATSEND$", payload: message}})
            .success(function(data){
                $notification.info("Antwort CHAT: ",data); 
            })
            .error(function() {$notification.error("Chat", "Sending your Message failed");});
    };

    //Beende die Runde: Checken der Werte, umleitung auf "Wartezimmer" 
    $scope.endRound = function() {

        var message = $cookieStore.get('authCookie') +":"+ $scope.userEntry.capacity +";"+ $scope.userEntry.marketing + ";" + $scope.userEntry.research +";"+ $scope.userEntry.material +";"+ $scope.userEntry.planePrice + "$";
        ////String: ProduktionInvestment;Marketing;Entwicklung;Materialstufe;Preis
        if ($scope.isEmpty($scope.userEntry.capacity) ||
            $scope.isEmpty($scope.userEntry.marketing) ||
            $scope.isEmpty($scope.userEntry.material) ||
            $scope.isEmpty($scope.userEntry.research) ||
            $scope.isEmpty($scope.userEntry.planePrice)
            ) {
            $notification.error("Fehler", "Bitte setze alle Werte in R&D, Marketing, Financials, Procurement und Manufacturing!");

        } else {       
            $http.get(serverBackendURL2 ,{params: {reason: "VALUES$", payload: message }} )
            .success(function (data){
            if (data.indexOf("VALUESUCC")) {
                $notification.info("Runde beendet: ","Bitte warte bis die anderen auch soweit sind");
                $location.path("inbetween");
            };
        }).error(function(){$notification.error("Endround", "Sending failed");})
        }

 
    };

    $scope.orderAction = function(id, action, index){
        
        if (action == "accept") {
            var param = $cookieStore.get('authCookie') +":"+"ACCEPTED;"+id+"$";
            $http.get( serverBackendURL2, {params: { reason: "ORDERINPUT$", payload: param}})
            .success(function(data){
                $notification.success("Sales", "Auftrag angenommen"); 
                $scope.recentOrders.splice(index, 1);

            })
            .error(function() {$notification.error("Sales",  "Laden der Aufträge gescheitert");});
        }else{
            $scope.recentOrders.splice(index, 1);  
            $notification.warning("Sales", "Auftrag abgelehnt");          
    };
            
    };
    /*
    Für die Aufträge:
ORDERINPUT;ACCEPTED OrderID,OrderID... PRODUCE;OrderId,OrderId
Der wird so aufgebaut
Also reason=ORDERINPUT 
payload=player-Game-ID:Accepted;OrderID,OrderID,...;PRODUCE;OrderID,...



    */

    $scope.manufactureJob = function(id, action){
        var param = $cookieStore.get('authCookie') +":"+"PRODUCE;"+id+"$";
            $http.get( serverBackendURL2, {params: { reason: "PRODUCE$", payload: param}})
            .success(function(data){
                console.log(data);
                if (data == "true") {
                    $notification.success("Produktion", "Auftrag erfolgreich gesendet"); 
                } else {
                    $notification.error("Produktion", "Du hast keine freien Kapazitäten mehr.");                    
                };
            })
            .error(function() {$notification.error("Error","manufactureJob failed. Server Down or CORS");});
    };

    //hole einen kreditvorschlag ein und kopiere die werte in ein array, mangels kredit-ID
    $scope.loanCalc = {};
    $scope.serverLoanArray;
    $scope.calculateLoan = function(){
        $http.get(serverBackendURL2, {params: {reason: "CREDIT$", payload: $cookieStore.get('authCookie') +":"+$scope.loanCalc.sum+","+$scope.loanCalc.period+"$"}})
        .success(function (data){
            $scope.loanCalc.interest = data[2];
            $scope.serverLoanArray = data;
        }).error(function(){console.log("CALCLOAN$ failed");})
    };

    //akzeptiere kreditvorschlag und lösche danach alle werte um missbrauch zu vermeiden
    $scope.acceptLoan = function(){
        $http.get(serverBackendURL2, {params: {reason: "ACCEPTCREDIT$", payload:$cookieStore.get('authCookie') +":"+ $scope.serverLoanArray[0]+":"+$scope.serverLoanArray[1]+":"+$scope.serverLoanArray[2]+"$"}})
        .success(function (data){
                $notification.success("Kredit", "Dein Kredit wurde genehmigt!");
                $scope.loanCalc.interest = "";
                $scope.loanCalc.sum = "";
                $scope.loanCalc.period = "";
                $scope.serverLoanArray = "";
        }).error(function(){console.log("CALCLOAN$ failed");})
    };

    $scope.declineLoan = function(){
        $notification.warning("Kredit", "Du hast den Kredit abgelehnt");
                $scope.loanCalc.interest = "";
                $scope.loanCalc.sum = "";
                $scope.loanCalc.period = "";
                $scope.serverLoanArray = "";    };

});

lisaApp.controller("lobbyController", function ($scope, $http, $cookieStore, $location, cfpLoadingBar, $timeout, $notification, pollingService){
    //polle den Server, um allen spielern den gleichen eintrittspunkt zu ermöglichen
    pollingService.startPolling("activePlayers", "GETACTIVEPLAYER$", $cookieStore.get('authCookie'), 3000, function(result){
              var players = result.data;
              $scope.players = players.split(":");
              if ($scope.players.length -1 == 4) {
                $notification.success("Info", "Alle Spieler sind anwesend! Los geht's!");
                $timeout(function() {
                $location.path('main');
                }, 1200);
                pollingService.stopPolling("activePlayers");
              };
          });

});


lisaApp.controller("betweenRoundsController", function ($scope, $http, $cookieStore, $location, cfpLoadingBar, $timeout, $notification, pollingService){
    //annäherndes duplikat des lobbycontrollers, nur mit fix für gecachtes dashboard
    pollingService.startPolling("readyPlayers", "GETREADYPLAYERS$", $cookieStore.get('authCookie'), 3000, function(result){
              var readyPlayers = result.data;
              $scope.roundPlayers = readyPlayers.split(":");
              if ($scope.roundPlayers.length == 4) {
                $notification.success("Ok", "Alle Spieler sind bereit! Nächste Runde beginnt!");
                console.log("if erfüllt");
                $timeout(function() {
                $location.path('main');
                }, 1200);
                pollingService.stopPolling("readyPlayers");
              };
          });

});
