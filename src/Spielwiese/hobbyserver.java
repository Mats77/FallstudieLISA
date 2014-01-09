package Spielwiese;

import org.webbitserver.BaseWebSocketHandler;
import org.webbitserver.WebServer;
import org.webbitserver.WebServers;
import org.webbitserver.WebSocketConnection;
import org.webbitserver.handler.StaticFileHandler;

public class hobbyserver extends BaseWebSocketHandler {
    private static int connectionCount;

    public void onOpen(WebSocketConnection connection) {
        connection.send("Hello! There are " + connectionCount + " other connections active");
        connectionCount++;
    }

    public void onClose(WebSocketConnection connection) {
        connectionCount--;
    }

    public void onMessage(WebSocketConnection connection, String message) {
        connection.send(message.toUpperCase()); // echo back message in upper case
    }

    public static void main(String[] args) {
        WebServer webServer = WebServers.createWebServer(8080)
                .add("/hellowebsocket", new hobbyserver())
                .add(new StaticFileHandler("index.html"));
        webServer.start();
        System.out.println("Server running at " + webServer.getUri());
        System.out.println(connectionCount);
    }
}