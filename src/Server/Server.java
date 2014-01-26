package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;
import java.util.ArrayList;


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
		int gameID = 3;
		handler = new Handler(gameID);
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
						if (txtin.contains("GET")) {
							System.out.println(txtin);	
						}
						input.add(txtin);
					}	
					String tmp = "";
					for (int i = 0; i < input.size(); i++) {
						tmp += URLDecoder.decode(input.get(i), "utf-8");						
					}
					// Inhalt erstellen
					txtout = handler.handleString(tmp);
					}catch(Exception e){
						e.printStackTrace();
						System.out.println("Keine Daten empfangen");
					}

					String result = "";// Verbindung akzeptieren
					result += "HTTP/1.1 200 OK \n";
					result += "Access-Control-Allow-Origin: http://www.digifurt.de" + "\n";
					result += "Content-type: text/html \n";
					result += " \n";
					out.println(result);
					System.out.println(result);
					out.flush();
					// Inhalt senden
					out.print(txtout);
					System.out.println(txtout);
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

	public static void close() {
		try {
			server.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		}
	}

