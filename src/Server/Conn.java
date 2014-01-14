package Server;

import java.io.IOException;
import java.net.Socket;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.webbitserver.BaseWebSocketHandler;
import org.webbitserver.WebSocketConnection;


/**
 * @author Mats
 * 
 */
public class Conn {

	private int id;
	private String nick;
	private int gameID;
	private boolean ready = false;
	private WebSocketConnection socket;
	private boolean active = true;
	private Handler handler;
	private String openMessages;

	public Conn(Handler handler) {
		this.handler = handler;
		this.gameID = handler.getGameID();
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

	public void close() {
		active = false;
		handler.spread("CHAT " + handler.getID(this) + " " + nick
				+ " hat das Spiel verlassen.");
		 socket.close();
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
	
}