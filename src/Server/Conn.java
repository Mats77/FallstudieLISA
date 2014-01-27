package Server;


import java.net.Socket;
import java.util.Vector;
import java.util.concurrent.CopyOnWriteArrayList;


/**
 * @author Mats
 * 
 */
public class Conn {

	private int id;
	private String nick ="";
	private int gameID;
	private boolean ready = false;
	private boolean active = true;
	private Handler handler;
	private CopyOnWriteArrayList<Order> openNewOrders = new CopyOnWriteArrayList<Order>();
	private Vector<ChatMessage> chatMessages = new Vector<ChatMessage>();
	private CopyOnWriteArrayList<Order> acceptedOrders = new CopyOnWriteArrayList<Order>();
	private String openMessage;
	private int prefRound; //bevorzugte Rundenanzahl von Spieler

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


	public void send(Order order) {
		openNewOrders.add(order);
		//socket.send(txt);
	}

	public long getId() {
		return id;
	}

	public String getNick() {
		return nick;
	}
	
	public Vector getChatMessages() {
		return chatMessages;
	}

	public void setChatMessages(ChatMessage Message) {
		chatMessages.add(Message);
	}


	public void setNick(String nick) {
		System.out.println("Nickname ist: " + nick);
		this.nick = nick;
	}

	public int getPrefRound() {
		return prefRound;
	}

	public void setPrefRound(int prefRound) {
		this.prefRound = prefRound;
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

	public CopyOnWriteArrayList<Order> getNewOrders() {
		return openNewOrders;
	}

	public void setOpenNewOrders(CopyOnWriteArrayList<Order> order) {
		openNewOrders = order;
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
	
	public String getOpenMessage() {
		return openMessage;
	}

	public void setOpenMessage(String openMessage) {
		this.openMessage = openMessage;
	}
	
	public CopyOnWriteArrayList<Order> getAcceptedOrders() {
		return acceptedOrders;
	}

	public void setAcceptedOrders(CopyOnWriteArrayList<Order> acceptedOrders) {
		this.acceptedOrders = acceptedOrders;
	}


	
}