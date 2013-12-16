package Client;

import java.net.*;
import java.io.*;


public class Conn extends Thread{

	//test an Max
	//Hallo Max!
	// heyho
	
	private int id;
	private String nick;
	private Socket socket;
	private PrintWriter out = null;
	private BufferedReader in = null;
	private boolean active = true;
	private Handler handler;
		
public Conn (Socket socket, Handler handler) {
	this.socket = socket;
	this.handler = handler;
	handler.setConn(this);
	try {
		out = new PrintWriter(socket.getOutputStream(), true);
		in = new BufferedReader(new InputStreamReader( socket.getInputStream()));
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		
	}
	start();


}


public void run() {
	send("ASKFORNICK "+this.nick);		//�bermittelt Nickname an den Server, f�r Chat, Statistik etc
	System.out.print("Run gestartet...Client");
	while(active){
			String txt;
			try {
					if ((txt = in.readLine()) != null){
					handler.handleString(txt);
				}
				

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
}


public void send (String txt){
	out.println(txt);	
}

public void close(){
	active = false;
	try {
		socket.close();
		in.close();
		out.close();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
}


public String getNick() {
	return nick;
}


public void setNick(String nick) {
	this.nick = nick;
}


public long getId() {
	return id;
}


public void setId(int id) {
	this.id = id;
}


}