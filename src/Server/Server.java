package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Vector;


class Server{
    private static int port;
    private static ServerSocket server;
    private static Socket connection;
    private static  BufferedReader in;
    private static  PrintWriter out;
    private static String txtin;
    private static String txtout;
	private static Handler handler;
	
	public static void main(String args[]) {
		
		//Server erstellt ein Handlerobjekt, ��ffnet den Server-Socket und beginnt Deamon Prozess
		// Erzeugung einer Game-ID (Auf einmaligkeit der GameID wird erst einmal verzichtet)
		int gameID = 1;
		Vector<Handler> games = new Vector<Handler>();
		handler = new Handler(gameID);
		games.add(handler);
    	port = 8080;
		try {
			server = new ServerSocket(port);
			System.out.println(server.getLocalPort());
			
			
			while (true) {
				//Jede Verbindung wird angenommen und der String an den GameHandler weitergegeben
				connection = server.accept();
				System.out.println("Neue Verbindung angenommen");
				in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				out = new PrintWriter(connection.getOutputStream());
					
					ArrayList<String> input = new ArrayList<String>();
					txtin = "";
					// String entgegennehmen (können mehrere Zeilen sein)
					try{
					while(!(txtin = in.readLine()).equals("")) {
						if (txtin.contains("GET")) {		// abfragen für schöner Debuganzeige
							if (txtin.contains("CHATREFRESH")) { // abfragen für schöner Debuganzeige
								
							}else if(games.lastElement().getConnections().size() == 4 && games.size() != 10 && txtin.contains("AUTHORIZEME")){
								gameID++;
								games.add(new Handler(gameID));
							}else{
								System.out.println(txtin);									
							}

						}
						input.add(txtin);
					}	
					String tmp = "";
					for (int i = 0; i < input.size(); i++) {
						tmp += URLDecoder.decode(input.get(i), "utf-8");						
					}
					int activeGame = getGameID(tmp);
					// Inhalt erstellen
					if (activeGame != 99) {
						txtout = games.elementAt(activeGame-1).handleString(tmp);
					}else{
						txtout = games.lastElement().handleString(tmp);
					}

					}catch(Exception e){
						e.printStackTrace();
						System.out.println("Keine Daten empfangen");
					}

					String result = "";// Verbindung akzeptieren
					result += "HTTP/1.1 200 OK \n";
					result += "Access-Control-Allow-Origin: http://www.digifurt.de" + "\n";
					result += "Access-Control-Allow-Origin: http://digifurt.de" + "\n";
					result += "Access-Control-Allow-Origin: http://localhost" + "\n";
					result += "Content-type: text/html \n";
					result += " \n";
					out.println(result);
					System.out.println(result);
					out.flush();
					// Inhalt senden
					out.print(txtout);
					//System.out.println(txtout);
					out.flush();
					// Verbindung trennen
					in.close();
					out.close();
					connection.close();
			}//Deamon-Schleife zur Annahme von Verbindungen
		} catch (Exception e) {
			System.out.print("Whoops! It didn't work!\n");
			e.printStackTrace();
		}
	}//main

	private static int getGameID(String txt) {
		// get payload
		int answer;
		if (txt.contains("payload")) {
			int tmpbeg = txt.lastIndexOf("payload");
			tmpbeg = tmpbeg + 8;
			String incomingGame = txt.substring(tmpbeg+2, tmpbeg + 3);
			System.out.println("Abgeshnittene GameID = " + incomingGame);
			answer = Integer.parseInt(incomingGame);
		}else{
			answer = 99;
		}
		return answer;
	}

	public static void close() {
		try {
			server.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		}
	}

