package Server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

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
		// Zun�chst wird der Spieler zugewiesen
		int activPlayerID = Integer.parseInt(txt.substring(0, 1));
		activePlayer = connections.get(activPlayerID);
		
		if (txt.startsWith("CHAT ")) {
			String s = "CHAT " + getID(activePlayer) + " " + activePlayer.getNick() + ": "
					+ txt.substring(5);
			spread(s);
			return s;
			// Einer der Spieler möchte das Spiel Starten, wenn alle Ready sind,
			// erstellt mechanics für jede
			// Conn ein Playerobjekt
		} else if (txt.startsWith("READY ")) {
			activePlayer.setReady(true);
			if (areAllReady()) {
				mechanics.startGame(connections);
				String s = "ALLREADY ";
				return s;
			}

			// Ein Client fragt einen Nickname an
		} else if (txt.startsWith("AUTHORIZEME ")) {
			//Dem Client muss die Game-ID und die Player-ID zugewiesen werden
			connections.add(new Conn(this));
			connections.lastElement().setId(this.getID(connections.lastElement()));
			System.out.println(connections.lastElement().getId() + " " + this.gameID);
			return String.valueOf(connections.lastElement().getId()) + " " + String.valueOf(this.gameID);
			
			// Ein Client hat seine Rundenwerte abgegeben
		} else if (txt.startsWith("VALUES")) { // String:
												// Produktion;Marketing;Entwicklung;Anzahl
												// Flgzeuge;Materialstufe;Preis
			mechanics.valuesInserted(txt.substring(7), activePlayer.getNick());
		} else if (txt.startsWith("PLAYERNAME ")) {

		} else if (txt.startsWith("CREDIT")) {
			mechanics.newCreditOffer(txt.substring(7), activePlayer.getNick()); // Höhe,
																		// Laufzeit
		}else if(txt.startsWith("ORDERINPUT ")){ //Nachricht vom Client : "ORDERINPUT ACCEPTED OrderID,OrderID... PRODUCE OrderId,OrderId"
			refreshPlayerOrderPool(txt, getID(activePlayer));
		} else if(txt.startsWith("ACCEPTCREDITOFFER")){
			mechanics.creditOfferAccepted(txt.substring(18), activePlayer.getNick());
		}else if (txt.startsWith("READY ")) {
			activePlayer.setReady(true);
		}else if (txt.startsWith("REFRESH ")) {
			Boolean newRound = false;
			for (Conn conn : connections) {
				if (conn.isReady()== false) {
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
		}
		return "INVALIDESTRING";
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
			if (!con.isReady())
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
				txt += order.getOrderId() + "," + order.getClientName() + "," + order.getQuantity() + ","
						+ order.getQuantityLeft() + "," + order.getQuartalValidTo() + ";";
			}
		}
		
		// an den obigen String anhängen: newOrders:OrderID,Kundennamen,Bestellmenge,Lieferzeitpunkt;
		if (newOrders.size() > 0) {
			txt += " newOrders:";
			for (Order order : newOrders) {
				txt += order.getOrderId() + "," + order.getClientName() + "," + order.getQuantity() + ","
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
}
