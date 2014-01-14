package Spielwiese;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;


public class hobbyserver{
    private static int connectionCount;
    private static int port;
    private static ServerSocket server;
    private static Socket connection;
    private static  BufferedReader in;
    private static  PrintWriter out;
    private static String txtin;
    private static String txtout;
    
    public static void main(String[] args) {
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
					while(!(txtin = in.readLine()).equals("")) {
						System.out.println(txtin);
						input.add(txtin);
					}
					System.out.println("Server hat empfangen");
					//txtout = handler.handleString(txtin);
					
					for (int i = 0; i < input.size(); i++) {
					out.println(input.get(i));						
					}
					out.println();
					out.flush();
					System.out.println("Server hat gesendet");
					// Verbindung trennen
					in.close();
					out.close();
					connection.close();
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
