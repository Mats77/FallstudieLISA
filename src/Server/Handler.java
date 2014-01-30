package Server;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;
import java.util.Vector;
import java.util.concurrent.CopyOnWriteArrayList;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;

public class Handler {

	private Vector<Conn> connections = new Vector<Conn>();
	private Mechanics mechanics;
	private Conn activePlayer; // Ist das hier notwendig?????
	private int gameID; // Um Eindeutigkeit des Spiels zu gewährleisten (Wird
						// in alle Conn-Klassen übertragen)
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
			con.setOpenMessage(txt);
		}
	}

	// überprüft, was der Client gesendet hat und veranlasst Reaktion
	public String handleString(String txt) {
		// Zun�chst wird der Spieler zugewiesen
		String command = getCommand(txt);
		String result = "";
		// Zun�chst wird der Spieler zugewiesen, au�er String enth�lt
		// AUTHORIZEME
		if (command.equals("AUTHORIZEME")) {
			// Dem Client muss die Game-ID und die Player-ID zugewiesen werden
			System.out.println("Start Athorization");
			connections.add(new Conn(this));
			connections.lastElement().setId(this.getID(connections.lastElement()));
			System.out.println("ID gesetzt");
			System.out.println(connections.lastElement().getId() + " " + this.gameID);
			result = connections.lastElement().getId() + " " + this.gameID;
			if (connections.size() == 4) {
				// start game
				mechanics.startGame(connections);
				newRoundStarted();
				return result;
			} else {
				return result;
			}
		} else if (command.startsWith("READY")) {
			String s = "";
			activePlayer.setReady(true);
			if (areAllReady()) {
				if (anzPlayer()) {
					mechanics.startGame(connections); // muss �berpr�ft
														// werden ob genug
														// spieler vorhanden
														// sind!!!!
					s = "ALLREADY ";
				} else {
					s = "NOTENOUGHPLAYER " + String.valueOf(connections.size());
				}
				return s;
			}
			return "WAITFORPLAYER";
		} else if (command.startsWith("GETACTIVEPLAYER")) {
			String s = "";
			for(Conn conn:connections){
				s += conn.getNick() + ":";
			}
			return s;
		} else if (command.equals("GETREADYPLAYERS")){
			String s = "";
			for (Conn conn : connections) {
				if (conn.getReady()) {
					s += conn.getNick() + ":";
				}
			}
			return s;
		} else if (command.startsWith("GETBASICDASHBOARD")) {
			String s = getDashboardValues();
			return s;
		} else if (command.startsWith("VALUES")) { // String:
													// Marketing;Entwicklung;Materialstufe;Preis
													// an Player
			Player[] players = mechanics.getPlayers();
			Boolean newRound = false;
			for (Player player : players) {
				if (player.getId() == activePlayer.getId()) {
					player.saveNextRoundValues(content, mechanics.getQuartal());
					activePlayer.setReady(true);
					for (Conn conn : connections) {
						if (conn.getReady()) {
							newRound = true;
						}else{
							newRound = false;
							break;
						}//end inner if
					}// end inner-for
				} // end if
			} // end for
			if (newRound) {
				newRoundStarted();
			}
			// Flugzeuge;Materialstufe;Preis
			mechanics.valuesInserted(content, activePlayer.getNick());
			return "VALUESSUCC";
		} else if (command.startsWith("CREDIT")) {
			mechanics.newCreditOffer(content, activePlayer.getNick()); // Höhe,
			// Laufzeit
		} else if (command.startsWith("ORDERINPUT")) { // Nachricht vom Client :
			refreshPlayerAcceptedOrderPool();// "ORDERINPUT ACCEPTED OrderID,OrderID... PRODUCE OrderId,OrderId"
			return "ORDERSACCEPTED";
		} else if (command.startsWith("ACCEPTCREDITOFFER")) {
			mechanics.creditOfferAccepted(txt.substring(18),activePlayer.getNick());
		} else if (command.equals("GETPRODUCEORDERS")) {
			System.out.println("Get Orders");
			refreshPlayerProduceOrderPool();
			CopyOnWriteArrayList<Order> acceptedOrders = activePlayer.getAcceptedOrders();
			String tmp = "";
			try {
				tmp = ow.writeValueAsString(acceptedOrders);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return tmp;
		} else if (command.equals("PRODUCE")){
			setOrdersToProduce();
			return "PRODUCESUCC";
		} else if (command.startsWith("GETSALES")) {
			result = "";
			result = checkOpenMessages();
			for (Conn conn : connections) {
				if (conn.getReady() == false) {
					break;
				} else {
					newRoundStarted();
					setStatusForNewRoundFalse();
				}
			}// End of For
			return result;
		} else if (command.equals("GETSTATS")) {
			String values = "";
			Player[] tmp = mechanics.getPlayers();
			for (int i = 0; i < tmp.length; i++) {
				if (activePlayer.getId() == tmp[i].getId()) {
					Vector<PlayerData> data = tmp[i].getData();
						try {
							values = ow.writeValueAsString(data);
						} catch (Exception e) {
							e.printStackTrace();
						} // end try catch
					return values;
				}// end if
			} // end for players
		} else if (command.equals("VERIFYFAILED")) {
			return "VERIFYFAILED";
		} else if (command.equals("VERIFY")) {
			return "CHECK";
		} else if (command.equalsIgnoreCase("INVALIDSTRING")) {
			content = "";
		} else if (command.equals("STARTGAME")) {
			String[] clientdata;
			String answer = "ERROR";
			System.out.println(content);
			clientdata = content.split(":");
			try {
				// 1. Nutzername überprüfen
				if (checkNickName(clientdata[0])) {
					String name = clientdata[0];
					activePlayer.setNick(name);
					answer = "CHECKNICK";
				} else {
					answer = "NICKNAMEINUSE";
				}
				// 2. Rundenanzahl ( gewüschte Anzahl an Runden)
				activePlayer.setPrefRound(Integer.parseInt(clientdata[1]));
				// 3. Siegbedingung
			} catch (Exception e) {
				e.printStackTrace();
			}
			return answer;
		} else if (command.equals("CHATSEND")) {
			String answer = "ERROR";
			answer = chatSendService();
			return answer;
		} else if (command.equals("CHATREFRESH")) {
			String answer = "";
			try {
				answer = ow.writeValueAsString(activePlayer.getChatMessages());
			} catch (Exception e) {
				e.printStackTrace();
			}
			return answer;
		}

		return "INVALIDESTRING";
	}

	private Vector<DashboardIcon> createStringBasicdashboarForNewRound(Player player) {
		// object round
		DashboardIcon round = new DashboardIcon();
		round.setTitle("Round");
		round.setIcon("calendar");
		round.setColor("success");
		round.setValue(Integer.toString(mechanics.getQuartal()));
		
		//object reliability
		DashboardIcon reli = new DashboardIcon();
		reli.setTitle("Reliability");
		reli.setIcon("thumbs-up");
		reli.setColor("success");
		reli.setValue(Double.toString(player.getReliability()));
		
		// object Active Orders
		DashboardIcon acOrd = new DashboardIcon();
		acOrd.setTitle("Active Orders");
		acOrd.setIcon("wrench");
		acOrd.setColor("important");
		acOrd.setValue(Integer.toString(player.getPlayerOrderPool().getAcceptedOrders().size()));
		
		// object Loans
		DashboardIcon loans = new DashboardIcon();
		try{
		loans.setTitle("Loans");
		loans.setIcon("credit-card");
		loans.setColor("important");
		loans.setValue(Double.toString(player.getShortTimeCredit().getAmount()));
		}catch(Exception e){
			System.out.println("Keine Kredite vorhanden");
			loans.setValue("0");
		}
		Vector<DashboardIcon> dashboard = new Vector<DashboardIcon>();
		dashboard.add(round);
		dashboard.add(reli);
		dashboard.add(acOrd);
		dashboard.add(loans);	

		
		return dashboard;
	}

	private Vector<DashboardIcon> getCostensValues(Player player) {
		// object variable costs
		DashboardIcon variableCosts = new DashboardIcon();
		variableCosts.setTitle("variable costs");
		variableCosts.setIcon("align-left");
		variableCosts.setColor("turquoise");
		try {
			variableCosts.setValue(Double.toString(player.getData().lastElement().getVarCosts()));
		} catch (Exception e) {
		}
		//object fix costs
		DashboardIcon cumulativeCosts = new DashboardIcon();
		cumulativeCosts.setTitle("fix costs");
		cumulativeCosts.setIcon("sort-alpha-asc");
		cumulativeCosts.setColor("red");
		try {
			cumulativeCosts.setValue(Double.toString(player.getData().lastElement().getFixCosts()));
		} catch (Exception e) {
		}
		//object price per Airplane
		DashboardIcon costsPerPlane = new DashboardIcon();
		costsPerPlane.setTitle("price per Airplane");
		costsPerPlane.setIcon("plane");
		costsPerPlane.setColor("gray");
		try {
			costsPerPlane.setValue(Double.toString(player.getData().lastElement().getPricePerAirplane()));
		} catch (Exception e) {
			// TODO: handle exception
		}
		// object overhead costs
		DashboardIcon overheadCosts = new DashboardIcon();
		overheadCosts.setTitle("overhead costs");
		overheadCosts.setIcon("align-justify");
		overheadCosts.setColor("purple");
		try {
			overheadCosts.setValue(Double.toString(player.getData().lastElement().getCosts()));
		} catch (Exception e) {
			// TODO: handle exception
		}

		Vector<DashboardIcon> dashboard = new Vector<DashboardIcon>();
		dashboard.add(variableCosts);
		dashboard.add(cumulativeCosts);
		dashboard.add(costsPerPlane);
		dashboard.add(overheadCosts);
		
		return dashboard;
	}

	private String getDashboardValues() {
		// activen Player bekommen
		Player[] players = mechanics.getPlayers();
		Player player = null;
		for(Player play : players){
			if (play.getId() == activePlayer.getId()) {
				player = play;
			}
		}
		// objekt für Geld
		DashboardIcon cash = new DashboardIcon();
		cash.setTitle("Cash");
		cash.setColor("green");
		cash.setIcon("usd");
		try{
		cash.setValue(Double.toString(player.getCash()));
		}catch(Exception e){
			return "PLAYERDONTEXIST";
		}
		//objekt für MarketShare
		DashboardIcon marketShare = new DashboardIcon();
		marketShare.setTitle("Market Share");
		marketShare.setIcon("globe");
		marketShare.setColor("turquoise");
		try{
		cash.setValue(Double.toString(player.getData().lastElement().getMarketshare()));
		}catch(Exception e){
			return "PLAYERDONTEXIST";
		}
		// objekt für Capacity
		DashboardIcon capacity = new DashboardIcon();
		capacity.setTitle("Capacity");
		capacity.setIcon("wrench");
		capacity.setColor("gray");
		try{
		cash.setValue(Double.toString(player.getData().lastElement().getCapacity()));
		}catch(Exception e){
			return "PLAYERDONTEXIST";
		}
		// objekt für marketing
		DashboardIcon marketing = new DashboardIcon();
		marketing.setTitle("Marketing");
		marketing.setIcon("bullhorn");
		marketing.setColor("purple");
		try{
		cash.setValue(Double.toString(player.getData().lastElement().getMarketing()));
		}catch(Exception e){
			return "PLAYERDONTEXIST";
		}
		// objekt für R&D
		DashboardIcon research = new DashboardIcon();
		research.setTitle("R&D");
		research.setIcon("flask");
		research.setColor("blue");
		try{
		cash.setValue(Double.toString(player.getData().lastElement().getResearch()));
		}catch(Exception e){
			return "PLAYERDONTEXIST";
		}
		// objekt für earnings
		DashboardIcon earnings = new DashboardIcon();
		earnings.setTitle("Earnings");
		earnings.setIcon("money");
		earnings.setColor("green");
		try{
		cash.setValue(Double.toString(player.getData().lastElement().getProfit()));
		}catch(Exception e){
			return "PLAYERDONTEXIST";
		}
		Vector<DashboardIcon> dashboard = new Vector<DashboardIcon>();
		dashboard.add(cash);
		dashboard.add(marketShare);
		dashboard.add(capacity);
		dashboard.add(marketing);
		dashboard.add(research);
		dashboard.add(earnings);
		String s = "";
		ArrayList<Vector> tmp = new ArrayList<Vector>();
		tmp.add(dashboard);
		tmp.add(createStringBasicdashboarForNewRound(player));
		tmp.add(getCostensValues(player));
		tmp.add(getEarnings(player));
		tmp.add(getLoans(player));
		try {
			s = ow.writeValueAsString(tmp);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return s;
	}

	private Vector<DashboardLoans> getLoans(Player player) {
		Vector<DashboardLoans> dashboard = new Vector<DashboardLoans>();
		DashboardLoans short1 = new DashboardLoans();
		try {
			short1.setPeriod("short-term");
			short1.setRate(Double.toString(player.getShortTimeCredit().getInterestRate()));
			short1.setSum(Double.toString(player.getShortTimeCredit().getAmount()));
			short1.setInterestsForQuarter(Double.toString(player.getShortTimeCredit().getInterestsForQuarter()));
		} catch (Exception e) {
			
		}
		if (short1 != null) {
			dashboard.add(short1);
		}
		for (LongTimeCredit credit : player.getCredits()) {
		DashboardLoans long1 = new DashboardLoans();	
		long1.setPeriod("long-term");
		long1.setRate(Double.toString(credit.getInterestRate()));
		long1.setSum(Double.toString(credit.getAmount()));
		long1.setInterestsForQuarter(Double.toString(credit.getInterestsForQuarter()));
		dashboard.add(long1);
		}
		return dashboard;
	}

	private Vector<DashboardIcon> getEarnings(Player player) {
		DashboardIcon cash = new DashboardIcon();
		cash.setTitle("Cash");
		cash.setIcon("money");
		cash.setColor("green");
		cash.setValue(Double.toString(player.getData().lastElement().getCash()));
		
		DashboardIcon revenue = new DashboardIcon();
		revenue.setTitle("Revenue");
		revenue.setIcon("repeat");
		revenue.setColor("blue");
		revenue.setValue(Double.toString(player.getData().lastElement().getProfit()));
		
		DashboardIcon price = new DashboardIcon();
		price.setTitle("Price per Airplane");
		price.setIcon("usd");
		price.setColor("red");
		price.setValue(Double.toString(player.getData().lastElement().getPricePerAirplane()));
		
		Vector<DashboardIcon> tmp = new Vector<DashboardIcon>();
		tmp.add(cash);
		tmp.add(revenue);
		tmp.add(price);
		return tmp;
	}

	public Mechanics getMechanics() {
		return mechanics;
	}

	private void setOrdersToProduce() {

	}

	private void setStatusForNewRoundFalse() {
		for (Conn conn : connections) {
			conn.setReady(false);
		}
	}

	private String chatSendService() {
		String time = getCurrentTimeAsString();
		String[] clientdata;
		//System.out.println(content);
		clientdata = content.split(":");
		String message = clientdata[0];
		//System.out.println("Nachricht: " + clientdata[0]);
		String avatar = clientdata[1];
		String direction;
		for (Conn con : connections) {
			if (con == activePlayer) {
				direction = "out";
			} else {
				direction = "in";
			}
			ChatMessage tmp = new ChatMessage(direction, avatar,
					activePlayer.getNick(), time, message);
			con.setChatMessages(tmp);
		}
		return "SENDSUCC";
	}

	private String getCurrentTimeAsString() {
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
			tmpbeg = tmpbeg + 8;
			String gamePlayerId = txt.substring(tmpbeg, tmpbeg + 3);
			txt = txt.substring(tmpbeg + 3);
			int tmpend = txt.indexOf("$");
			// set content == data from client (for example input data)
			// get hole content!
			try {
				content = txt.substring(1, tmpend);
				System.out.println(content);
			} catch (Exception e) {
				System.out.println("Kein Inhalt vorhanden");
			}
			System.out.println("Content = " + content);
			// if active player found: set active player
			try {
				System.out.println("Player-ID = " + gamePlayerId.charAt(0));
				getactivePlayer(gamePlayerId);
			} catch (Exception e) {
				System.out.println("Player not found");
				return "PLAYERNOTFOUND";
			}
		}
		// get reason-command
		if (mes.contains("reason")) {
			int beg = mes.lastIndexOf("reason");
			beg = beg + 7;
			mes = mes.substring(beg);
			int end = mes.indexOf("$");
			String message = "";
			message = mes.substring(0, end);
			System.out.println("Reason = " + message);
			return message;
		}
		return result;
	}

	private void getactivePlayer(String gamePlayerId) {
		for (Conn conn : connections) {
			System.out.println(conn.getId() + " Id empfangen: "
					+ gamePlayerId.substring(0, 1));
			if (conn.getId() == Integer.parseInt(gamePlayerId.substring(0, 1))) {
				activePlayer = conn;
				System.out.println("AktivPlayer gesetzt!");
				break;
			}
		}
	}

	private String checkOpenMessages() {
		CopyOnWriteArrayList<Order> tmp;
		String answer = "";
		tmp = activePlayer.getNewOrders();

		try {
			answer = ow.writeValueAsString(tmp);
			System.out.println(answer);
		} catch (Exception e) {
			// TODO: handle exception
			answer = "NONEWS";
		}
		return answer;

	}

	private boolean anzPlayer() {
		// TODO Auto-generated method stub
		if (connections.size() == 4) {
			return true;
		} else {
			return false;
		}

	}

	public int getID(Conn connection) {
		System.out.println("Get Player ID!");
		int toReturn = -1;
		System.out.println(connections.indexOf(connections.lastElement()));
		toReturn = connections.indexOf(connections.lastElement())+1;
	//	for (Conn con : connections) {
	//		System.out.println(con.getId());
	//		if (con.equals(connection)) {
	//			toReturn = connections.indexOf(con) + 1;
	//		}
	//	}
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

	public void setPlayerOrderPoolActivOrders(Conn conn) {
		System.out.println(content);
		int index = content.indexOf(";");
		String produce = content.substring(index + 1);
		int produceId = Integer.parseInt(produce);
		mechanics.produceOrder(activePlayer.getId(), produceId);
	}

	public void setPlayerOrderPoolNewOrders(Conn conn) {
		// von Conn auf Player schlie�en
		Player[] players = mechanics.getPlayers();
		String answer = "NOORDERS";
		for (Player player : players) {
			if (player.getId() == conn.getId()) {
				PlayerOrderPool pool = player.getPlayerOrderPool(); // orderpool
																	// f�r
																	// player
																	// holen
				CopyOnWriteArrayList<Order> newOrders = pool.getNewOrders(); // neuen
																				// bestellungen
																				// holen
				conn.setOpenNewOrders(newOrders);
				try {
					answer = ow.writeValueAsString(newOrders); // Bestellungen
																// in String
																// abspeichern
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		}
		}

	// Deaktiviert bzw. Aktiviert die Eingabefelder des Client wenn auf die
	// Abhandlung der orders gewartet wird.
	public void setStatusForInputValues(boolean bol, int playerId) {
		connections.elementAt(playerId).setOpenMessage("STATUS INPUT " + bol);
	}

	// Aktualisiert den PlayerOrderPool der Spieler mit den neu angenommen und
	// den kommend produzierenden Orders
	private void refreshPlayerAcceptedOrderPool() {
		System.out.println(content); // 8
		String accepted = content.substring(9);
		int orderId = Integer.parseInt(accepted);
		mechanics.acceptOrder(activePlayer.getId(), orderId);
	}

	private void refreshPlayerProduceOrderPool() {
		// von Conn auf Player schlie�en
		Player[] players = mechanics.getPlayers();
		String answer = "NOORDERSACTIV";
		for (Player player : players) {
			if (player.getId() == activePlayer.getId()) {
				PlayerOrderPool pool = player.getPlayerOrderPool(); // orderpool
																	// f�r
																	// player
																	// holen
				CopyOnWriteArrayList<Order> newOrders = pool.getOrdersToDisplay(); // neuen
																				// bestellungen
																				// holen
				activePlayer.setAcceptedOrders(newOrders);
				try {
					answer = ow.writeValueAsString(newOrders); // Bestellungen
																// in String
																// abspeichern
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
	}
	}

	public void newRoundStarted() {
		Player[] players = mechanics.getPlayers();
		// spread("NEWROUND");
		for (Conn conn : connections) {
			setPlayerOrderPoolNewOrders(conn);
		}
		for (Player player : players) {
			PlayerData newData = player.getData().lastElement(); // Hier sind
																	// die Daten
																	// für Max,
																	// kannst du
																	// auswerten
																	// und
																	// versenden
			Vector<LongTimeCredit> credits = player.getCredits(); // hier sind
																	// die
																	// Kredite
			ShortTimeCredit shortTimeCredits = player.getShortTimeCredit();
		}
	}

	public void offerCredit(double[] offer, String nick) { // Für den Spieler
															// mit dem nick muss
															// das Angebot
															// zurückgegeben
															// werden
		// offer= amount, runtime, interestRate
	}

	// NUR ZUM TESTEN!!!
	public void setConnections(Vector<Conn> connections) {
		this.connections = connections;
	}

	public int getGameID() {
		return gameID;
	}

	public void notifyWinners(int[][] winnerMap) { // erster Wert ist jeweils
													// der Wert und zweiter der
													// Spieler
		this.winners = winnerMap;
	}

	public int[][] getWinners() {
		return winners;
	}

	public double[][] getWinnersDouble() {
		return winnersDouble;
	}

	public void notifyWinners(double[][] winnerMapDouble) { // erster Wert ist
															// jeweils der Wert
															// und zweiter der
															// Spieler
		this.winnersDouble = winnerMapDouble;
	}

	public Boolean checkNickName(String name) {
		Boolean answer = false;
		for (Conn conn : connections) {
			if (conn.getNick().equals(name)) {
				System.out.println("Nickname in Use");
				answer = false;
			} else {
				answer = true;
			}
		}
		return answer;
	}
}