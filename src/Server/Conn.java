package Server;

import java.net.Socket;

import org.webbitserver.WebSocketConnection;


/**
 * @author Mats
 * 
 */
public class Conn extends Thread {

	private long id;
	private String nick;
	private boolean ready = false;
	private WebSocketConnection socket;
	private boolean active = true;
	private Handler handler;

	public boolean isReady() {
		return ready;
	}

	public void setReady(boolean ready) {
		this.ready = ready;
	}

	// public Conn (Socket socket, Handler handler) {
	// this.socket = socket;
	// this.handler = handler;
	// try {
	// out = new PrintWriter(socket.getOutputStream(), true);
	// in = new BufferedReader(new InputStreamReader( socket.getInputStream()));
	// } catch (IOException e) {
	// close();
	// e.printStackTrace();
	// }
	//
	// start();
	// }

	public Conn(WebSocketConnection socket, Handler handler) {
		this.socket = socket;
		this.handler = handler;
		id = handler.getID(this);
		start();
	}

	public Conn(Socket skt, Handler handler2) {
		// TODO Auto-generated constructor stub
	}

	public void run() {
		send("CONNECTED "); // damit Client-Thread beginnt
		System.out.print("Run gestartet...Server");
		int tmp = handler.getID(this);
		if (tmp != -1) {
			this.id = tmp;
		} else {
			System.out.println("Error while asking for Player ID");
		}

		// while(active){ //horchen
		// String txt;
		// try {
		// if ((txt = in.readLine()) != null) {
		// System.out.println("Server bekommt: " + txt + ";");
		// handler.handleString(txt, this);
		// }
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// close();
		// }
	}

	public void send(String txt) {
		System.out.println("Server sendet: " + txt);
		socket.send(txt);
	}

	public long getId() {
		return id;
	}

	public void close() {
		active = false;
		handler.spread("CHAT " + handler.getID(this) + " " + nick
				+ " hat das Spiel verlassen.");
		// socket.close();
	}

	public String getNick() {
		return nick;
	}

	public void setNick(String nick) {
		this.nick = nick;
	}

	public WebSocketConnection getConnection() {
		return socket;
	}

	// NUR FÜRS TESTEN!!!!
	public void setId(int id) {
		this.id = id;
	}

}