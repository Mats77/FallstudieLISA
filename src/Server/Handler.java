package Server;

import java.util.ArrayList;
import java.util.Vector;

import org.webbitserver.WebSocketConnection;

public class Handler {

	private Vector<Conn> connections = new Vector<Conn>();
	private Mechanics mechanics;
	private Conn sender;

	// Konstruktor, erstellt direkt Mechanics
	public Handler() {
		mechanics = new Mechanics(this);
	}

	// reicht das vom Server übergeben Conn objekt in das Array connections ein
	public void addPlayer(Conn player) {
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
			if (con.getConnection().equals(connection)) {
				sender = con;
				break;
			} else {
				sender = null;
			}
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
			mechanics.newCredit(txt.substring(7), sender.getNick()); // Höhe,
																		// Zins,
																		// Laufzeit
		}else if(txt.startsWith("ACCEPT-ORDER:")){
			acceptOrder(txt, getID(sender));
		}else if(txt.startsWith("PRODUCE-ORDER:")){
			produceOrder(txt, getID(sender));
		}
		
	}

	public int getID(Conn connection) {
		int toReturn = -1;
		for (Conn con : connections) {
			if (connection == con)
				toReturn = (int) con.getId();
		}
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
	
	private void acceptOrder(String txt, int playerId){
		
		txt = txt.split(":")[1];
		int orderId= Integer.parseInt(txt.split(";")[0]);
		mechanics.acceptOrderForPlayer(orderId, (int)sender.getId()); // PlayerID herausfinden aus Con?!
	}
	
	private void produceOrder(String txt, int playerId){
		txt = txt.split(":")[1];
		int orderId= Integer.parseInt(txt.split(";")[0]);
		mechanics.produceOrderForPlayer(orderId, (int)sender.getId()); // PlayerID herausfinden aus Con?!
	}

	public void newRoundStarted() {
		spread("NEWROUND");
	}
}
