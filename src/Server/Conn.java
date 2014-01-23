package Server;


import java.net.Socket;


/**
 * @author Mats
 * 
 */
public class Conn {

	private int id;
	private String nick;
	private int gameID;
	private boolean ready = false;
	private boolean active = true;
	private Handler handler;
	private String openMessages;

	public Conn(Handler handler) {
		this.handler = handler;
		this.setGameID(handler.getGameID());
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

	public Conn(Socket skt, Handler handler2) {
		// TODO Auto-generated constructor stub
		System.out.println("Falscher Constructor!");
	}


	public void send(String txt) {
		System.out.println("Server sendet: " + txt);
		//socket.send(txt);
	}

	public long getId() {
		return id;
	}

	public String getNick() {
		return nick;
	}

	public void setNick(String nick) {
		this.nick = nick;
	}


	// NUR FÜRS TESTEN!!!!
	public void setId(int id) {
		this.id = id;
	}
	
	public boolean getReady() {
		return ready;
	}

	public void setReady(boolean ready) {
		this.ready = ready;
	}

	public String getOpenMessages() {
		String answer = openMessages;
		openMessages = "";
		return answer;
	}

	public void setOpenMessages(String openMessages) {
		this.openMessages += openMessages;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public int getGameID() {
		return gameID;
	}

	public void setGameID(int gameID) {
		this.gameID = gameID;
	}
	
}