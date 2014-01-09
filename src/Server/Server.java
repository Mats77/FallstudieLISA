package Server;

import org.webbitserver.*;
import org.webbitserver.handler.StaticFileHandler;

class Server extends BaseWebSocketHandler {
	private static int connectionCount;
	private static WebServer webServer;
	private static Handler handler;

	public void onOpen(WebSocketConnection connection){
		Conn conn = new Conn(connection, handler);
		handler.addPlayer(conn);
	}
	
    public void onClose(WebSocketConnection connection) {
        connectionCount--;
    }

    public void onMessage(WebSocketConnection connection, String message) {
    	handler.handleString(message, connection); // verbindung und Nachricht wird an den Handler übertragen
        //connection.send(message.toUpperCase()); // echo back message in upper case
    }
	
	public static void main(String args[]) {
		
		//Server erstellt ein Handlerobjekt, öffnet den Server-Socket und beginnt Deamon Prozess
		// Erzeugung einer Game-ID (Auf einmaligkeit der GameID wird erst einmal verzichtet)
		int gameID = 3;
		Handler handler = new Handler(3);
        webServer = WebServers.createWebServer(8080).add("/hellowebsocket", new Server()).add(new StaticFileHandler("index.html"));
        webServer.start();
        System.out.println("Server running at " + webServer.getUri());
        System.out.println(connectionCount);
	
        /*		try {
			server = new ServerSocket(56557);
			System.out.print(server.getLocalPort());
			while (true) {
				
				//Für jede eingehende Verbindung wird ein Conn Objekt erstellt und dem Handler zum Verwalten übergeben
				Socket skt = server.accept();
				Conn conn = new Conn(skt, handler);
				handler.addPlayer(conn);
			}//Deamon-Schleife
		} catch (Exception e) {
			System.out.print("Whoops! It didn't work!\n");
			e.printStackTrace();
		}
	}//main

	public static void close() {
		try {
			server.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		}*/
	}
}
