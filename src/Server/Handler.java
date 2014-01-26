package Server;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.TimeZone;
import java.util.Vector;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;

public class Handler {

	private Vector<Conn> connections = new Vector<Conn>();
	private Mechanics mechanics;
	private Conn activePlayer;	//Ist das hier notwendig?????
	private int gameID; // Um Eindeutigkeit des Spiels zu gewährleisten (Wird in alle Conn-Klassen übertragen)
	private String content;
	private int[][] winners;
	private double[][] winnersDouble;
	private ObjectWriter ow;

	// Konstruktor, erstellt direkt Mechanics
	public Handler(int gameID) {
		mechanics = new Mechanics(this);
		this.gameID = gameID;
		System.out.println("Handler lebt!");
		ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
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
		//int activPlayerID = Integer.parseInt(txt.substring(0, 1));
		//activePlayer = connections.get(activPlayerID);
		
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
		} else if (command.startsWith("READY")) {
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
		}else if(command.equals("GETSTATS")){
			String values = "";
			Player[] tmp = mechanics.getPlayers();
			for (int i = 0; i < tmp.length; i++) {
				if (activePlayer.getId() == tmp[i].getId()) {
					Vector<PlayerData> data = tmp[i].getData();
					for(PlayerData playerdata : data){
						try {
							values += ow.writeValueAsString(playerdata);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} // end try catch
					}// end for playerdata
					return values;
				}//end if
			} // end for players
		}else if (command.equals("VERIFYFAILED")) {
			return "VERIFYFAILED";
		}else if(command.equals("VERIFY")){
			return "CHECK";
		}else if(command.equalsIgnoreCase("INVALIDSTRING")){
			content = "";
		}else if(command.equals("STARTGAME")){
			String[] clientdata;
			String answer = "ERROR";
			System.out.println(content);
			clientdata = content.split(":");
			System.out.println("Arraydaten: 1. Länge " + clientdata.length + " 2. Inhalt " + Arrays.toString(clientdata) );
			try{
			//1. Nutzername überprüfen
			if(checkNickName(clientdata[0])){
				String name = clientdata[0];
				System.out.println("My name is ... " + name);
				activePlayer.setNick(name);
				answer = "CHECKNICK";
			}else{
				answer = "NICKNAMEINUSE";
			}			
			//2. Rundenanzahl ( gewüschte Anzahl an Runden)
			activePlayer.setPrefRound(Integer.parseInt(clientdata[1]));
			//3. Siegbedingung
			}catch(Exception e){
				e.printStackTrace();
			}
			return answer;
		}else if(command.equals("CHAT")){
			String answer = "ERROR";
			answer = chatService();
			return answer;
		}
		
		return "INVALIDESTRING";	
	}
	
   private String chatService() {
		String time = getCurrentTimeAsString();
		String[] clientdata;
		System.out.println(content);
		clientdata = content.split(":");
		String message = clientdata[0];
		System.out.println("Nachricht: " + clientdata[0]);
		String avatar = clientdata[1];
		String direction;
		for(Conn con : connections){
			if (con == activePlayer) {
				direction = "out";
			}else{
				direction = "in";
			}
			ChatMessage Message = new ChatMessage(direction, avatar, activePlayer.getNick(), time, message);	
			String answer = "";
			try {
				answer = ow.writeValueAsString(Message);
				System.out.println(answer);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			con.setOpenMessages(answer);
		}
		return activePlayer.getOpenMessages();
	}

private String getCurrentTimeAsString()
	  {
	    DateFormat formatter = new SimpleDateFormat("HH:mm:ss"); 
	    formatter.setTimeZone(TimeZone.getTimeZone("GMT+2:00"));
	    return formatter.format(new Date());
	  }

	private String getCommand(String txt) {
		// TODO Auto-generated method stub
		String mes = txt;
		String result = "";
		// get payload
		if (txt.contains("payload")) {
			int tmpbeg = txt.lastIndexOf("payload");
			tmpbeg = tmpbeg +8;
			String gamePlayerId = txt.substring(tmpbeg, tmpbeg+3);
			txt = txt.substring(tmpbeg+3);
			int tmpend = txt.indexOf("$");
			// set content == data from client (for example input data)
			// get hole content!
			try{
			content = txt.substring(1, tmpend);
			}catch(Exception e){
				System.out.println("Kein Inhalt vorhanden");
			}
			// if active player found: set active player
			try{
				System.out.println("Player-ID = " + gamePlayerId.charAt(0));
				getactivePlayer(gamePlayerId);
			}catch(Exception e){
				System.out.println("Player not found");
				return "PLAYERNOTFOUND";
			}
		}
		// get reason-command
		if(mes.contains("reason")){
			int beg = mes.lastIndexOf("reason");
			beg = beg + 7;
			mes = mes.substring(beg);
			int end = mes.indexOf("$");
			String message = "";
			message = mes.substring(0, end);
			return message;
		}
		return result;
	}

	private void getactivePlayer(String gamePlayerId) {
		for (Conn conn : connections) {
			System.out.println(conn.getId() + " Id empfangen: " + gamePlayerId.substring(0, 1));
			if (conn.getId() == Integer.parseInt(gamePlayerId.substring(0, 1))) {
				activePlayer = conn;
				System.out.println("AktivPlayer gesetzt!");
				break;
			}
		}	
	}

	private String checkOpenMessages() {
		String tmp;
		try{
			tmp = activePlayer.getOpenMessages();
		}catch(Exception e){
			tmp = "NONEWS";
		}
			return tmp;
		

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
		this.winners = winnerMap;	
	}

	public int[][] getWinners() {
		return winners;
	}

	public double[][] getWinnersDouble() {
		return winnersDouble;
	}

	public void notifyWinners(double[][] winnerMapDouble) { //erster Wert ist jeweils der Wert und zweiter der Spieler
		this.winnersDouble = winnerMapDouble;
	}
	
	public Boolean checkNickName(String name){
		Boolean answer = false;
		for (Conn conn: connections) {
			if(conn.getNick().equals(name)){
				System.out.println("Nickname in Use");
				answer = false;
			}else{
				answer = true;
			}
		}
		return answer;
	}
}