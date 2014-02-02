app.service('WebSocketFactory', ['$q', '$rootScope', function($q, $rootScope) {
    
    var Service = {}; // Object to return for the service
    var callbacks = {}; // Keep all pending requests here until they get responses
    var currentCallbackId = 0; // Create a unique callback ID to map requests to responses
    var ws = null; // Initialize our websocket variable
    var connectStatus = false; // Is the websocket connected?
    var authStatus = false; // Are we authorized?
    var disconnects = 0; // How many times have we disconnected and retried (used for calculating retry wait)
    var retryWaitMax = 120; // Maximum amount of time to wait for a re-connect attempt
    var pendingRequests = []; // Any requests coming in before we connect are stored and run when connected


    // Create our websocket object with the address to the websocket
           Service.connect = function (address) {
            console.log('Connecting to websocket...');
            address = 'ws://localhost:8080/hellowebsocket';
            ws = new WebSocket(address);

            ws.onopen = function () {
                console.log('Socket has been opened!');
                connectStatus = true;
                checkCookie();
                console.log("pendingRequests: "+pendingRequests);
                ws.send(pendingRequests[0]);
                if (pendingRequests != null) {
                   for (var i = 0; i < pendingRequests.length; i++) {
                  console.log(pendingRequests[i]);
                  sendRequest(pendingRequests[i]);
                  console.log(i +"test");
                }
                };
               
                
            };

            ws.onmessage = function (message) {
                console.log(message.data);
                //listener(JSON.parse(message.data));
            };

            ws.onclose = function () {
                console.log('Connection lost!');
                connectStatus = false;
                disconnects++;
                var retryWait = Math.pow(2, disconnects);
                if (retryWait > retryWaitMax) {
                    disconnects--;
                    retryWait = retryWaitMax;
                }
                console.log('Retrying in ' + retryWait + ' seconds...');
                console.log("Pending: "+pendingRequests);
                setTimeout(Service.connect, retryWait * 1000);
            };
        };

        //@TODO: get connectionID, serverID from server, setCookie
        function checkCookie() {
            if ($.cookie('gameActive')) {
              console.log("connID: "+$.cookie(connectionID));
              console.log("gameID: "+$.cookie(gameID));
              console.log("active: "+$.cookie(gameActive));

              return true;
            } else {
              authorize();
            }
            //return true;
        }

        function authorize(data){
          if (data == null) {
            sendRequest("AUTHORIZEME");
          } else {
            //$.cookie("connectionID", data.connectionID, { path: '/lisa'});
            $.cookie("gameID", data.gameID, { path: '/lisa'});
            $.cookie("gameActive", true, { path: '/lisa'});

          }

        }

    function sendRequest(request) {
            // var defer = $q.defer();
            // var callbackId = getCallbackId();
            // callbacks[callbackId] = {
            //     time: new Date(),
            //     cb: defer
            // };
            //request.callback_id = callbackId;
            if (connectStatus == true) {
                console.log('Sending request:', request);
                //ws.send(request);
                ws.send(JSON.stringify(request));
            } else {
                console.log('Saving request until connected');
                pendingRequests.push(request);
            }
            // return defer.promise;
        }


    function listener(data) {
      var messageObj = data;
      console.log("Received data from websocket: ", messageObj);
      if (messageObj.authorization != '') {
        authorize(messageObj);
      };
      // If an object exists with callback_id in our callbacks object, resolve it
      if(callbacks.hasOwnProperty(messageObj.callback_id)) {
        console.log(callbacks[messageObj.callback_id]);
        $rootScope.$apply(callbacks[messageObj.callback_id].cb.resolve(messageObj.data));
        delete callbacks[messageObj.callbackID];
      }
    }
    // This creates a new callback ID for a request
    function getCallbackId() {
      currentCallbackId += 1;
      if(currentCallbackId > 10000) {
        currentCallbackId = 0;
      }
      return currentCallbackId;
    }

     /**
     * Subscribe to a freeswitch event
     * @param {String} eventName
     * @return {Promise}
     */
    Service.subscribe = function (eventName) {
      var request = {
            type: 'subscribe',
            event_name: eventName
            };
            // Storing in a variable for clarity on what sendRequest returns
          var promise = sendRequest(request);
        return promise;
        };

    // Define a "getter" for getting customer data
    Service.getCustomers = function() {
      // var request = "nicht funktionierender request";
      // sendRequest(request);
      // Storing in a variable for clarity on what sendRequest returns
      // var promise = sendRequest(request); 
      // return promise;
    }
      //Finally, start the service
      Service.connect();

    return Service;
}]);