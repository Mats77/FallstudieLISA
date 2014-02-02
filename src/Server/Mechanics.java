package Server;

import java.util.Vector;

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
//		if(allPlayerReadyForOrderSelection())
//		{	
//			ordersForNewRound();
//		}
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
	
	public int produceOrder(int playerID, int orderID){		//liefert true, falls mehr produziert werden dürfen
		int toReturn = -1;
		for (Player player : players) {
			if(player.getId() == playerID)
			{
				toReturn = player.produceOrder(orderID);
			}
		}
		return toReturn;
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
