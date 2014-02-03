package Server;

import java.util.ArrayList;
import java.util.Vector;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;

public class Mechanics {
	private Market market = new Market();
	private Player[] players;
	private Handler handler;
	private PlayerDataCalculator playerDataCalculator;
	private Bank bank = new Bank();
	private static int quartal = 0;
	private int roundsToPlay;
	private int conditionToWin;	// 	0: die meisten Flugzeuge gewinnen	1: Der höchste Marktanteil gewinnt
								//	2: der höchste Umsatz gewinnt		3: der höchste Gewinn gewinnt
								//	4: die meiste Kohle gewinnt
	
	
	public Mechanics(Handler h) {
		market = new Market();
		playerDataCalculator = new PlayerDataCalculator(this);
		this.handler = h;	
	}

	//wird vom Handler aufgerufen, sobald ein Spieler seine Werte eingegeben hat
	public void valuesInserted(String values, String nick) {
		for(int i=0; i<players.length; i++)
		{
			String tmp = players[i].getNick();
			if(tmp.equalsIgnoreCase(nick))
			{
				players[i].saveNextRoundValues(values, quartal);
				//handler.setStatusForInputValues(false, i); //Deaktiviert die Eingabe des Players im GUI 
				break;
			}
		}
		if(allPlayerReadyForNextRound()){
			endRound();
			startNewRound();
		}
	}
	
	private String getDashboardValues(Conn conn) {
		// activen Player bekommen
		Player player = players[conn.getId()+1];
		// objekt für Geld
		DashboardIcon cash = new DashboardIcon();
		cash.setTitle("Cash");
		cash.setColor("green");
		cash.setIcon("usd");
		try {
			cash.setValue(Double.toString(player.getCash()) + " in mio.");
		} catch (Exception e) {
			return "PLAYERDONTEXIST";
		}
		// objekt für MarketShare
		DashboardIcon marketShare = new DashboardIcon();
		marketShare.setTitle("Market Share");
		marketShare.setIcon("globe");
		marketShare.setColor("turquoise");
		try {
			marketShare.setValue(Double.toString(player.getData().lastElement().getMarketshare()) + " %");
			marketShare.setPercent(Double.toString(player.getData().lastElement().getMarketshare()));
			System.out.println("Marketshare = " + Double.toString(player.getData().lastElement().getMarketshare()));
		} catch (Exception e) {
			return "PLAYERDONTEXIST";
		}
		// objekt für Capacity
		DashboardIcon capacity = new DashboardIcon();
		capacity.setTitle("Capacity");
		capacity.setIcon("wrench");
		capacity.setColor("gray");
		try {
			capacity.setValue(Double.toString(player.getData().lastElement()
					.getCapacity()));
			System.out.println("Capacity = " + Double.toString(player.getData().lastElement().getCapacity()));
		} catch (Exception e) {
			return "PLAYERDONTEXIST";
		}
		// objekt für marketing
		DashboardIcon marketing = new DashboardIcon();
		marketing.setTitle("Marketing");
		marketing.setIcon("bullhorn");
		marketing.setColor("purple");
		try {
			marketing.setValue(Double.toString(player.getData().lastElement().getMarketing()) + " in mio.");
			System.out.println("Cash = " + Double.toString(player.getData().lastElement().getMarketing()) + " in mio.");
		} catch (Exception e) {
			return "PLAYERDONTEXIST";
		}
		// objekt für R&D
		DashboardIcon research = new DashboardIcon();
		research.setTitle("R&D");
		research.setIcon("flask");
		research.setColor("blue");
		try {
			research.setValue(Double.toString(player.getData().lastElement().getResearch()) + " in mio.");
			System.out.println("R&D = " + Double.toString(player.getData().lastElement().getResearch()) + " in mio.");
		} catch (Exception e) {
			return "PLAYERDONTEXIST";
		}
		// objekt für earnings
		DashboardIcon earnings = new DashboardIcon();
		earnings.setTitle("Earnings");
		earnings.setIcon("money");
		if (player.getData().lastElement().getProfit() < 0) {
			earnings.setColor("red");
		}else if (player.getData().lastElement().getProfit() == 0) {
			earnings.setColor("yellow");
		}else{
		earnings.setColor("green");
		}
		try {
			earnings.setValue(Double.toString(player.getData().lastElement().getProfit()));
			System.out.println("Profit = " + Double.toString(player.getData().lastElement().getProfit()));
		} catch (Exception e) {
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
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		try {
			s = ow.writeValueAsString(tmp);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}
	
	private Vector<DashboardIcon> createStringBasicdashboarForNewRound(
			Player player) {
		// object round
		DashboardIcon round = new DashboardIcon();
		round.setTitle("Round");
		round.setIcon("calendar");
		round.setColor("success");
		round.setValue(Integer.toString(quartal));

		// object reliability
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
		acOrd.setValue(Integer.toString(player.getPlayerOrderPool()
				.getAcceptedOrders().size()));

		// object Loans
		DashboardIcon loans = new DashboardIcon();
		try {
			loans.setTitle("Loans");
			loans.setIcon("credit-card");
			loans.setColor("important");
			loans.setValue(Double.toString(player.getShortTimeCredit()
					.getAmount()));
		} catch (Exception e) {
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
			variableCosts.setValue(Double.toString(player.getData()
					.lastElement().getVarCosts()));
		} catch (Exception e) {
		}
		// object fix costs
		DashboardIcon cumulativeCosts = new DashboardIcon();
		cumulativeCosts.setTitle("fix costs");
		cumulativeCosts.setIcon("sort-alpha-asc");
		cumulativeCosts.setColor("red");
		try {
			cumulativeCosts.setValue(Double.toString(player.getData()
					.lastElement().getFixCosts()));
		} catch (Exception e) {
		}
		// object price per Airplane
		DashboardIcon costsPerPlane = new DashboardIcon();
		costsPerPlane.setTitle("price per Airplane");
		costsPerPlane.setIcon("plane");
		costsPerPlane.setColor("gray");
		try {
			costsPerPlane.setValue(Double.toString(player.getData()
					.lastElement().getPricePerAirplane()));
		} catch (Exception e) {
		}
		// object overhead costs
		DashboardIcon overheadCosts = new DashboardIcon();
		overheadCosts.setTitle("overhead costs");
		overheadCosts.setIcon("align-justify");
		overheadCosts.setColor("purple");
		try {
			overheadCosts.setValue(Double.toString(player.getData()
					.lastElement().getCosts()));
		} catch (Exception e) {
		}

		Vector<DashboardIcon> dashboard = new Vector<DashboardIcon>();
		dashboard.add(variableCosts);
		dashboard.add(cumulativeCosts);
		dashboard.add(costsPerPlane);
		dashboard.add(overheadCosts);

		return dashboard;
	}
	
	private Vector<DashboardLoans> getLoans(Player player) {
		Vector<DashboardLoans> dashboard = new Vector<DashboardLoans>();
		DashboardLoans short1 = new DashboardLoans();
		try {
			short1.setPeriod("short-term");
			short1.setRate(Double.toString(player.getShortTimeCredit()
					.getInterestRate()));
			short1.setSum(Double.toString(player.getShortTimeCredit()
					.getAmount()));
			short1.setInterestsForQuarter(Double.toString(player
					.getShortTimeCredit().getInterestsForQuarter()));
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
			long1.setInterestsForQuarter(Double.toString(credit
					.getInterestsForQuarter()));
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
		revenue.setValue(Double.toString(player.getData().lastElement()
				.getProfit()));

		DashboardIcon price = new DashboardIcon();
		price.setTitle("Price per Airplane");
		price.setIcon("usd");
		price.setColor("red");
		price.setValue(Double.toString(player.getData().lastElement()
				.getPricePerAirplane()));

		Vector<DashboardIcon> tmp = new Vector<DashboardIcon>();
		tmp.add(cash);
		tmp.add(revenue);
		tmp.add(price);
		return tmp;
	}

	
	
	private boolean allPlayerReadyForNextRound() {
		boolean allPlayerReadyForNextRound = true;
		for(int i=0; i<players.length; i++){
			if(!players[i].isReadyForNextRound())
			{
				allPlayerReadyForNextRound = false;
			}
		}
		return allPlayerReadyForNextRound;
	}
		

//	private boolean allPlayerReadyForOrderSelection() {
//		boolean allPlayerReadyForOrderSelection = true;
//		for(int i=0; i<players.length; i++){
//			if(!players[i].isReadyForOrderSelection())
//			{
//				allPlayerReadyForOrderSelection = false;
//			}
//		}
//		return allPlayerReadyForOrderSelection;
//	}
	
	public void endRound(){	
		
		//neue Werte berechnen für die neuen Aufträge und Investitionsausgaben für F&E, Marketing
		double[] values = playerDataCalculator.generateNewCompanyValues(players);
		for(int i=0; i<players.length; i++)
		{
			players[i].setCompanyValue(values[i]);
		}

		for (int i = 0; i < players.length; i++) {
			players[i].setReadyForNextRound(false);
	//		players[i].setReadyForOrderSelection(false);
		}		

		
		playerDataCalculator.calcCapacities(players);	//Kapazitäten errechnen und Produktionsinvestition
		playerDataCalculator.calcCosts(players);		//Kosten errechnen und vom Cash abziehen
		playerDataCalculator.setTurnover(players);
		playerDataCalculator.calcProfits(players);
		playerDataCalculator.updateCreditValues(players);
		market.calcTotalTurnover(players); // Berechnet den gesamten Marktumsatz
		market.calcMarketSharePerPlayer(players); //Berechnet für jeden Spieler den Marketshare und schreiben es in die PlayerData
		//Quartalsabschluss ---> Jemand muss noch anhand der hier schon vollsätndigen Daten die Jahresabschlüsse erstellen
		//außerdem könnte im Zuge dessen auch ein berichtswesen eingebaut werden
		for (Conn conn : handler.getConnections()) {
			conn.setDashboard(this.getDashboardValues(conn));
		}
		
		if(roundsToPlay == quartal)
		{
			endGame();
		}
	}
	
	public void endGame() {
		int[][] winnerMap = new int[4][2];
		double[][]winnerMapDouble = new double[4][2];
		boolean integerValues = false;
		if(conditionToWin == 0)				//die meisten Flugzeuge
		{
			integerValues = true;
			for (Player player : players) {
				int value = 0;
				Vector<PlayerData> data = player.getData();
				for (PlayerData playerData : data) {
					value += playerData.getAirplanes();
				}
				rankPlayer(winnerMap,player, value);
			}
		} else if (conditionToWin == 1) {	//den größten Marktanteil
			double value = 0;
			for (Player player : players) {
				Vector<PlayerData> data = player.getData();
				for (PlayerData playerData : data) {
					value = playerData.getMarketshare();
				}
				rankPlayer(winnerMapDouble,player, value);
			}
		} else if (conditionToWin == 2) {	//den größten Umsatz
			for (Player player : players) {
				double value = 0;
				Vector<PlayerData> data = player.getData();
				for (PlayerData playerData : data) {
					value += playerData.getTurnover();
				}
				rankPlayer(winnerMapDouble,player, value);
			}
		} else if (conditionToWin == 3) {	//den größten Gewinn
			for (Player player : players) {
				double value = 0;
				Vector<PlayerData> data = player.getData();
				for (PlayerData playerData : data) {
					value += playerData.getProfit();
				}
				rankPlayer(winnerMapDouble,player, value);
			}
		} else if (conditionToWin == 4) {	//die meiste Kohle
			double value = 0;
			for (Player player : players) {
				value = player.getCash() - player.getDebtCapital();
				rankPlayer(winnerMapDouble,player, value);
			}
		}
		
		if(integerValues)
		{
			handler.notifyWinners(winnerMap);
		} else {
			handler.notifyWinners(winnerMapDouble);
		}
	}
	

	private void rankPlayer(double[][] winnerMapDouble, Player player,
			double value) {
		for(int i=0; i<winnerMapDouble.length; i++)
		{
			if(value > winnerMapDouble[i][0])
			{
				for(int j = 3; j>i; j--)
				{
					winnerMapDouble [j][0] = winnerMapDouble [j-1][0];
					winnerMapDouble [j][1] = winnerMapDouble [j-1][1];
				}
				winnerMapDouble[i][0] = value;
				winnerMapDouble[i][1] = player.getId();
				break;
			}
		}
		
	}

	private void rankPlayer(int[][] winnerMap, Player player,
			int value) {
		for(int i=0; i<winnerMap.length; i++)
		{
			if(value > winnerMap[i][0])
			{
				for(int j = 3; j>i; j--)
				{
					winnerMap [j][0] = winnerMap [j-1][0];
					winnerMap [j][1] = winnerMap [j-1][1];
				}
				winnerMap[i][0] = value;
				winnerMap[i][1] = player.getId();
				break;
			}
		}
	}

	public void ordersForNewRound()
	{	
		market.genOrdersForNewRound(); 
		market.splitOrders(players);
		
		//An Client die Aufträge des Players senden UND die CapacityLeft im Player erneuern
//		for (int i = 0; i < players.length; i++) {
//			//Jede Runde wird die noch verfügbare Capazity auf die Gesamt Cap. des Player gesetzt. 
//			//Diese wird dann jeweils abgebaut durch Erfüllung eines Auftrags. Wenn die Ges. Capazity höher
//			//als die angenommen Aufträge liegt geht der Cap. Überschuss in der nächsten Periode verloren.
//			players[i].setCapacityLeft(players[i].getData().get(quartal).getCapacity());
//			// die Daten müssen in der Conn Klasse zwischengespeichert werden
//			handler.sendPlayerOrderPool(players[i].getId(), players[i].getPlayerOrderPool());
//		}
	}
	
	private void startNewRound() {
		quartal ++; //auf nächstes Quartal gehen.
		
		//Die Eingabe für den User reaktivieren
//		for (int i = 0; i < players.length; i++) {
//			handler.setStatusForInputValues(true, i);
//		}
		// Events aufrufen
		ordersForNewRound();		
		handler.newRoundStarted();//hier müssen die User informiert werden und können ihre Aufträge annhemen oder ablehen
		//außerdem werden hier Berichte übermittelt etc.
	}



	//Methode wird vom Handler aufgerufen, sobald alle Connections ready sind
	public void startGame(Vector<Conn> playersCon)
	{
		generatePlayers(playersCon);
		
		// CompanyValues errechnen, da dies normal immer zum Spielende gemacht wird => wird aber benötigt für OrderSplit schon zu Beginn! 
		double[] values = playerDataCalculator.generateNewCompanyValues(players);
		for(int i=0; i<players.length; i++)
		{
			players[i].setCompanyValue(values[i]);
		}
		
		ordersForNewRound();	//Aufträge verteilen
		quartal++;
		for (Conn conn : handler.getConnections()) {
			conn.setDashboard(this.getDashboardValues(conn));
		}
	}

	//wird aufgerufen, sobald ein Spiel gestartet wird, erstellt die Spieler
	public void generatePlayers(Vector<Conn> playersCon) {
		players = new Player[playersCon.size()];
		int ctr = 0;
		for (Conn conn : playersCon) {
			players[ctr] = new Player(conn.getId(), conn.getNick(), playerDataCalculator, this);
			ctr++;
		}
	}

	public Bank getBank() {
		return bank;
	}
	
	private Player getPlayerByNick(String nick)
	{
		for (Player player : players) {
			if(player.getNick().equalsIgnoreCase(nick))
			{
				return player;
			}
		}
		return null;
	}
	
	//Wird vom Handler aufgerufen wenn der Client mit der Selektion seiner Orders fertig ist.
	public void refreshPlayerOrderPool(int playerID, int [] orderByIdToProduce, int [] orderByIdAccepted){
		players[playerID].setReadyForNextRound(true);
		players[playerID].newOrdersToProduce(orderByIdToProduce);
		players[playerID].newOrdersAccepted(orderByIdAccepted);
		
		//Wenn alle Spieler mit ihrer Auftragsbearbeitung fertig sind, wird die Runde ausgwertet und eine neue gestartet.
		if(allPlayerReadyForNextRound())
		{	
			endRound();
			startNewRound();
		}
		
}
	
	public void acceptOrder(int playerID, int orderID)
	{
		for (Player player : players) {
			if(player.getId() == playerID)
			{
				player.getPlayerOrderPool().acceptOrder(orderID);
				break;
			}
		}
	}
	
	public void declineOrder(int playerID, int orderID)
	{
		for(Player player : players) {
			if(player.getId() == playerID){
				player.getPlayerOrderPool().unacceptOrder(orderID);
				break;
			}
		}
	}
	
	public boolean produceOrder(int playerID, int orderID){		//liefert true, falls mehr produziert werden dürfen
		for (Player player : players) {
			if(player.getId() == playerID)
			{
				if(player.produceOrder(orderID)){
					return true;
				} else {
					return false;
				}
			}
		}
		return false;
	}

	public double[] newCreditOffer(String substring, String nick) {	//Höhe, Laufzeit		
		// TODO Kreditaufnahme (langfristig)
		Player player = getPlayerByNick(nick);
		double[] offer = new double[3];
		if(player != null)
		{
			offer = bank.getCreditOffer(player, substring);
		}
		return offer;
	}
	
	public static int getQuartal(){
		return quartal;
	}
	//NUR FÜRS TESTEN
	
	public Market getMarket(){
		return market;
	}
	
	public PlayerDataCalculator getPlayerDataCalculator(){
		return playerDataCalculator;
	}
	
	public Player[] getPlayers(){
		return players;
	}

	public void creditOfferAccepted(String substring, String nick) {
		Player player = getPlayerByNick(nick);
		double[] creditData = new double[3];
		boolean dataAccepted = true;
		for(int i=0; i< substring.split(";").length; i++)
		{
			try
			{
				creditData[i] = Double.parseDouble(substring.split(";")[i]);
			} catch (NumberFormatException e) {
				dataAccepted = false;
				System.out.println("Fehler bei den Kreditdaten");
			}
		}
		if(dataAccepted)
		{
			bank.generateLongTimeCredit(player, creditData);	//creditData: Höhe, Laufzeit, Zins
		}
	}
	
	public void setEndOfGame(int condition, int numberOfRounds)
	{
		this.roundsToPlay = numberOfRounds;
		this.conditionToWin = condition;
	}
}
