package Server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;
import java.util.concurrent.ExecutionException;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.webbitserver.WebSocketConnection;

public class Handler {

	private Vector<Conn> connections = new Vector<Conn>();
	private Mechanics mechanics;
	private Conn activePlayer;	//Ist das hier notwendig?????
	private int gameID; // Um Eindeutigkeit des Spiels zu gewährleisten (Wird in alle Conn-Klassen übertragen)
	private String content;

	// Konstruktor, erstellt direkt Mechanics
	public Handler(int gameID) {
		mechanics = new Mechanics(this);
		this.gameID = gameID;
		System.out.println("Handler lebt!");
	}

	// veranlasst das senden einer Nachricht an alle Clients
	public void spread(String txt) { // sendet an alle
		for (Conn con : connections) {
			con.send(txt);
		}
	}

	// überprüft, was der Client gesendet hat und veranlasst Reaktion
	public String handleString(String txt) {
		String command = getCommand(txt);
		String result = "";
		// Zun�chst wird der Spieler zugewiesen, au�er String enth�lt AUTHORIZEME
		if (command.equals("AUTHORIZEME")) {
			//Dem Client muss die Game-ID und die Player-ID zugewiesen werden
			connections.add(new Conn(this));
			connections.lastElement().setId(this.getID(connections.lastElement()));
			System.out.println(connections.lastElement().getId() + " " + this.gameID);
			result = connections.lastElement().getId() + " " + this.gameID;
			if (connections.size() == 4) {
				//start game
				mechanics.startGame(connections);
			}else{
				return result;				
			}

		}else if (txt.startsWith("CHAT")) {
			String s = "CHAT " + getID(activePlayer) + " " + activePlayer.getNick() + ": "
					+ txt.substring(5);
			spread(s);
			return s;
			// Einer der Spieler möchte das Spiel Starten, wenn alle Ready sind,
			// erstellt mechanics für jede
			// Conn ein Playerobjekt
		} else if (command.startsWith("READY ")) {
			String s = "";
			activePlayer.setReady(true);
			if (areAllReady()) {
				if (anzPlayer()) {
					mechanics.startGame(connections); // muss �berpr�ft werden ob genug spieler vorhanden sind!!!!	
					s = "ALLREADY ";
				}else{
					s = "NOTENOUGHPLAYER " + String.valueOf(connections.size());
				}
				return s;
			}
			return "WAITFORPLAYER";

			// Ein Client fragt einen Nickname an
		} else if (command.startsWith("VALUES")) { // String:
												// Produktion;Marketing;Entwicklung;Anzahl
												// Flgzeuge;Materialstufe;Preis
			mechanics.valuesInserted(txt.substring(7), activePlayer.getNick());
		} else if (command.startsWith("PLAYERNAME")) {

		} else if (command.startsWith("CREDIT")) {
			mechanics.newCreditOffer(txt.substring(7), activePlayer.getNick()); // Höhe,
																		// Laufzeit
		}else if(command.startsWith("ORDERINPUT")){ //Nachricht vom Client : "ORDERINPUT ACCEPTED OrderID,OrderID... PRODUCE OrderId,OrderId"
			refreshPlayerOrderPool(txt, getID(activePlayer));
		} else if(command.startsWith("ACCEPTCREDITOFFER")){
			mechanics.creditOfferAccepted(txt.substring(18), activePlayer.getNick());
		}else if (command.startsWith("REFRESH")) {
			result = "";
			Boolean newRound = false;
			result = checkOpenMessages();
			for (Conn conn : connections) {
				if (conn.getReady()== false) {
					newRound = false;
					break;
				}else{
					newRound = true;
				}		
			}//End of For
			if (newRound) {
				return ""; // Alle relevanten Objekte f�r neue Runde
			}else{
				return "NOINFOS";
			}
		}else if(command.contains("VERIFY")){
			return "CHECK";
		}else if (command.equals("VERIFYFAILED")) {
			return "VERIFYFAILED";
		}
		content = "";
		return "INVALIDESTRING";
	}

	private String getCommand(String txt) {
		// TODO Auto-generated method stub
		String mes = txt;
		String result = "";
		// get payload
		if (txt.contains("payload")) {
			int tmpbeg = txt.lastIndexOf("payload");
			tmpbeg = tmpbeg +7;
			String gamePlayerId = txt.substring(tmpbeg, tmpbeg+3);
			txt = txt.substring(tmpbeg+3);
			int tmpend = txt.indexOf("$");
			// set content == data from client (for example input data)
			// get hole content!
			content = txt.substring(0, tmpend);
			// if active player found: set active player
			try{
			activePlayer = connections.get(Integer.valueOf(gamePlayerId.charAt(0)));
			}catch(Exception e){
				System.out.println("Player not found");
				return "VERIFYFAILED";
			}
		}
		// get reason-command
		if(mes.contains("reason")){
			int beg = mes.lastIndexOf("reason");
			beg = beg + 7;
			int end = mes.indexOf("$");
			String message = "";
			message = mes.substring(beg, end);
			return message;
		}
		return result;
	}

	private String checkOpenMessages() {
		return activePlayer.getOpenMessages();
	}

	private boolean anzPlayer() {
		// TODO Auto-generated method stub
		if (connections.size()==4) {
			return true;
		}else{
			return false;	
		}

	}

	public int getID(Conn connection) {
		System.out.println("Get Player ID!");
		int toReturn = -1;
		for (Conn con : connections) {
			System.out.println(con.getId());
			if (con.equals(connection)){
				toReturn = connections.indexOf(con)+1;
			}			
		}
		System.out.println("Return PlayerID: " + toReturn);
		return toReturn;
	}

	public boolean areAllReady() {// teste in der Lobby ob alle fertig sind.
									// Evtl markieren wer fertig ist usw.
		boolean toReturn = true;
		for (Conn con : connections) {
			if (!con.getReady())
				toReturn = false;
		}
		return toReturn;
	}

	public void sendPlayerOrderPool(int playerID, PlayerOrderPool playerOderPool) {
		ArrayList<Order> acceptedOrders = playerOderPool.getAcceptedOrders();
		ArrayList<Order> newOrders = playerOderPool.getNewOrders();
		
		// Dies ist nur ein Test!
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();

		try {
			String json = ow.writeValueAsString(acceptedOrders);
			json += ow.writeValueAsString(acceptedOrders);
			System.out.println(json);
		} catch (JsonGenerationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// Ende vom Test
		
		String txt = "Player " + playerID;
		
		//String senden mit Player (ID) acceptedOrders:OrderID,Kundenname,Bestellmenge,noch zu produzierende Menge,bis wann zu produzieren;
		if (acceptedOrders.size() > 0) {
			txt += " acceptedOrders:";
			for (Order order : acceptedOrders) {
				txt += order.getOrderId() + "," + order.getClientName() + "," + order.getTotalQuantity() + ","
						+ order.getQuantityLeft() + "," + order.getQuartalValidTo() + ";";
			}
		}
		
		// an den obigen String anhängen: newOrders:OrderID,Kundennamen,Bestellmenge,Lieferzeitpunkt;
		if (newOrders.size() > 0) {
			txt += " newOrders:";
			for (Order order : newOrders) {
				txt += order.getOrderId() + "," + order.getClientName() + "," + order.getTotalQuantity() + ","
						+ order.getQuartalValidTo() + ";";
			}
		}
		connections.elementAt(playerID).send(txt);		//Nachricht an Client senden.
	}
	
	//Deaktiviert bzw. Aktiviert die Eingabefelder des Client wenn auf die Abhandlung der orders gewartet wird.
	public void setStatusForInputValues(boolean bol, int playerId){
	
		connections.elementAt(playerId).send("STATUS INPUT "+bol);
	}
	
	
	//Aktualisiert den PlayerOrderPool der Spieler mit den neu angenommen und den kommend produzierenden Orders
	private void refreshPlayerOrderPool(String txt, int playerId){
	
	
		String produce= txt.split(" ")[2];
		String accepted = txt.split(" ")[4];
		
		String orderIdToProduce [] = produce.split(",");
		String orderIdAccepted [] = accepted.split(",");
		
		int orderByIdToProduce [] = new int [orderIdToProduce.length];
		int orderByIdAccepted [] = new int [orderIdAccepted.length];
		
		for (int i = 0; i < orderIdToProduce.length; i++) {
			orderByIdToProduce [i] = Integer.parseInt(orderIdToProduce[i]); 
		}
		
		for (int i = 0; i < orderIdAccepted.length; i++) {
			orderByIdAccepted [i]= Integer.parseInt(orderIdAccepted[i]);
		}
		
		mechanics.refreshPlayerOrderPool(playerId, orderByIdToProduce, orderByIdAccepted);	
		
	}

	public void newRoundStarted(Player[] players) {
		spread("NEWROUND");
		for (Player player : players) {
			PlayerData newData = player.getData().lastElement();	//Hier sind die Daten für Max, kannst du auswerten und versenden
			Vector<LongTimeCredit> credits = player.getCredits();			//hier sind die Kredite
			ShortTimeCredit shortTimeCredits = player.getShortTimeCredit();
		}
	}
	
	public void offerCredit(double[] offer, String nick) {	//Für den Spieler mit dem nick muss das Angebot zurückgegeben werden
														//offer= amount, runtime, interestRate
	}	
	
	//NUR ZUM TESTEN!!!
	public void setConnections(Vector<Conn> connections) {
		this.connections = connections;
	}
	
	public int getGameID(){
		return gameID;
	}

	public void notifyWinners(int[][] winnerMap) {	//erster Wert ist jeweils der Wert und zweiter der Spieler
		// TODO Auto-generated method stub
		
	}

	public void notifyWinners(double[][] winnerMapDouble) { //erster Wert ist jeweils der Wert und zweiter der Spieler
		// TODO Auto-generated method stub
		
	}
}
