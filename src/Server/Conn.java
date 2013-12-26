package Server;
import org.webbitserver.WebSocketConnection;

//Test Kommentar! gdff
public class Conn extends Thread{

	private int id;
	private String nick;
	private boolean ready;	//raus und in Mechanics
	private WebSocketConnection socket;
	private boolean active = true;
	private Handler handler;

	public boolean isReady() {
		return ready;
	}


	public void setReady(boolean ready) {
		this.ready = ready;
	}


public Conn (WebSocketConnection socket,  Handler handler) {
	this.socket = socket;
	this.handler = handler;
	id = handler.getID(this);
}


public void send (String txt){
	System.out.println("Server sendet: " + txt);
	socket.send(txt);	
}

public void close(){
	active = false;
	handler.spread("CHAT " + handler.getPlayerID(this)+ " " + nick + " hat das Spiel verlassen.");
	//socket.close();
}


public String getNick() {
	return nick;
}


public void setNick(String nick) {
	this.nick = nick;
}

public WebSocketConnection getConnection(){
	return socket;
}

}