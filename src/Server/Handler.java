package Server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.webbitserver.WebSocketConnection;

public class Handler {

	private Vector<Conn> connections = new Vector<Conn>();
	private Mechanics mechanics;
	private Conn sender;	//Ist das hier notwendig?????
	private int gameID; // Um Eindeutigkeit des Spiels zu gewährleisten (Wird in alle Conn-Klassen übertragen)

	// Konstruktor, erstellt direkt Mechanics
	public Handler(int gameID) {
		mechanics = new Mechanics(this);
		this.gameID = gameID;
		System.out.println("Handler lebt!");
	}

	// reicht das vom Server übergeben Conn objekt in das Array connections ein
	public void addPlayer(Conn player) {
		System.out.println("Spieler wird hinzugefügt");
		connections.add(player);
	}

	// veranlasst das senden einer Nachricht an alle Clients
	public void spread(String txt) { // sendet an alle
		for (Conn con : connections) {
			con.send(txt);
		}
	}

	// überprüft, was der Client gesendet hat und veranlasst Reaktion
	public void handleString(String txt, WebSocketConnection connection) {

		for (Conn con : connections) {
		if (con.getId() == Integer.parseInt((txt.substring(0, 2)))) {
			sender = con;
		}
//			if (con.getConnection().equals(connection)) {
//				sender = con;
//				break;
//			} else {
//				sender = null;
//			}
		}
		if (sender == null) {
			// get player ID
			// txt Datei nach Spieler-ID durchsuchen, wenn eine gefunden wurde:
			// player[i] mit Spieler-ID wird ausgelesen und die
			// WebSocketConnection neu gesetzt

		}

		if (txt.startsWith("CHAT ")) {
			String s = "CHAT " + getID(sender) + " " + sender.getNick() + ": "
					+ txt.substring(5);
			spread(s);

			// Einer der Spieler möchte das Spiel Starten, wenn alle Ready sind,
			// erstellt mechanics für jede
			// Conn ein Playerobjekt
		} else if (txt.startsWith("READY ")) {
			sender.setReady(true);
			if (areAllReady()) {
				mechanics.startGame(connections);
				String s = "ALLREADY ";
				spread(s);
			}

			// Ein Client fragt einen Nickname an
		} else if (txt.startsWith("ASKFORNICK")) {
			sender.setNick(txt.substring(11));

			// Ein Client hat seine Rundenwerte abgegeben
		} else if (txt.startsWith("VALUES")) { // String:
												// Produktion;Marketing;Entwicklung;Anzahl
												// Flgzeuge;Materialstufe;Preis
			mechanics.valuesInserted(txt.substring(7), sender.getNick());
		} else if (txt.startsWith("PLAYERNAME ")) {

		} else if (txt.startsWith("CREDIT")) {
			mechanics.newCreditOffer(txt.substring(7), sender.getNick()); // Höhe,
																		// Zins,
																		// Laufzeit
		}else if(txt.startsWith("ORDERINPUT ")){ //Nachricht vom Client : "ORDERINPUT ACCEPTED OrderID,OrderID... PRODUCE OrderId,OrderId"
			refreshPlayerOrderPool(txt, getID(sender));
		}
	}

	public int getID(Conn connection) {
		System.out.println("Get Player ID!");
		int toReturn = -1;
		for (Conn con : connections) {
			System.out.println(con.getId());
			if (connection == con)
				try {
					toReturn = (int) con.getId();
					return toReturn;
				} catch (Exception e) {
					// TODO: handle exception
				}
			toReturn = (int) con.getId();				
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
			Vector<Credit> credit = player.getCredits();			//hier sind die Kredite
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
