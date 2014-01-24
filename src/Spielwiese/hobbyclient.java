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
		String[] test = {"AUTHORIZEME ", "REFRESH ", "READY "};
		String user = "";
		System.out.println("Client startet");
		try {
			for (int i = 0; i < test.length; i++) {
				try {
					socket = new Socket("localhost", 8080);
					in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					out = new PrintWriter(socket.getOutputStream());
					System.out.println("Verbindung hergestellt");
					
					if (i != 0) {
						out.println(user + " " + test[i]);
					}else {
						out.println(test[i]);	
					}

					out.println();
					out.flush();
					
					System.out.println("Text gesendet: " + test[i]);
					ArrayList<String> input = new ArrayList<String>();
					txt = "";
					System.out.println("Wartet auf Antwort");
					while (!(txt = in.readLine()).equals("")) {
						input.add(txt);
						System.out.println("Client empfängt: " + txt);
					}
					
					String ans = "";
					for (int j = 0; j < input.size(); j++) {
						ans += input.get(i);
					}
					if (i == 0) {
						user = ans;
					}
					System.out.println(ans);
				} catch (Exception e) {
					// TODO: handle exception
				}finally{
					in.close();
					out.close();
					socket.close();
				}
			}

		} catch (Exception e) {
			// TODO: handle exception
		}
	}

}
