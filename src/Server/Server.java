package Server;

import java.lang.*;
import java.io.*;
import java.net.*;

class Server {
	private static ServerSocket server;

	public static void main(String args[]) {
		
		//Server erstellt ein Handlerobjekt, öffnet den Server-Socket und beginnt Deamon Prozess
		Handler handler = new Handler();
		try {
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

	}
}
