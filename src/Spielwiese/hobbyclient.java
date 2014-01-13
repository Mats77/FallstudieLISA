package Spielwiese;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public class hobbyclient {

	private static Socket socket;
	private static BufferedReader in;
	private static PrintWriter out;
	private static String txt;
//	private static
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String test = "AUTHORIZEME ";
		System.out.println("Client startet");
		try {
			socket = new Socket("localhost", 8080);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(socket.getOutputStream());
			System.out.println("Verbindung hergestellt");
			
			out.println(test);
			out.println();
			out.flush();
			
			System.out.println("Text gesendet");
			ArrayList<String> input = new ArrayList<String>();
			txt = "";
			while (!(txt = in.readLine()).equals("")) {
				input.add(txt);
				System.out.println("Client empfängt: " + txt);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}finally{
			try {
				in.close();
				out.close();
				socket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

}
