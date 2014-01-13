package Spielwiese;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


public class hobbyserver extends Thread {
    private static int connectionCount;
    private static int port;
    private static Socket server;
    private static  BufferedReader in;
    private static  PrintWriter out;
    private static String txt;

    public static void main(String[] args) {
    	port = 8080;
		try {
			server = new Socket("localhost", port);
			System.out.print(server.getLocalPort());
			
			in = new BufferedReader(new InputStreamReader(server.getInputStream()));
			out = new PrintWriter(server.getOutputStream());
			
			while (true) {
				//Jede Verbindung wird angenommen und der String an den GameHandler weitergegeben
				try {
					if ((txt = in.readLine()) != null) {
						System.out.println("Server bekommt: " + txt);
						handler.handleString(txt);
					}
				} catch (Exception e) {
					// TODO: handle exception
				}
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
