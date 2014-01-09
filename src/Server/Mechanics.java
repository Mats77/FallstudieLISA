package Server;

import java.util.Vector;

public class Mechanics {
	private Market market = new Market();
	private Player[] players;
	private Handler handler;
	private PlayerDataCalculator playerDataCalculator;
	private Bank bank = new Bank();
	private static int quartal = 0;
	
	
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
				handler.setStatusForInputValues(false, i); //Deaktiviert die Eingabe des Players im GUI zum Testen von Mattes deaktiviert
				break;
			}
		}
		if(allPlayerReadyForOrderSelection())
		{	
			ordersForNewRound();
		}
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
		

	private boolean allPlayerReadyForOrderSelection() {
		boolean allPlayerReadyForOrderSelection = true;
		for(int i=0; i<players.length; i++){
			if(!players[i].isReadyForOrderSelection())
			{
				allPlayerReadyForOrderSelection = false;
			}
		}
		return allPlayerReadyForOrderSelection;
	}
	
	private void endRound(){
		for (int i = 0; i < players.length; i++) {
			players[i].setReadyForNextRound(false);
			players[i].setReadyForOrderSelection(false);
		}		
		//neue Werte berechnen für die neuen Aufträge und Investitionsausgaben für F&E, Marketing
		double[] values = playerDataCalculator.generateNewCompanyValues(players);
		for(int i=0; i<players.length; i++)
		{
			players[i].setCompanyValue(values[i]);
		}
		
		//Geld wurde schon mittel player.addCash auf den Player übertragen. Geld wird übertragen sobald der Spieler
		//angibt und gesendet hat welchen Auftrag er produzieren möchte.
		playerDataCalculator.setTurnover(players);
		playerDataCalculator.calcCapacities(players);	//Kapazitäten errechnen und Produktionsinvestition
		playerDataCalculator.calcCosts(players);		//Kosten errechnen und vom Cash abziehen
		playerDataCalculator.calcProfits(players);
		playerDataCalculator.updateCreditValues(players);
		//Quartalsabschluss ---> Jemand muss noch anhand der hier schon vollsätndigen Daten die Jahresabschlüsse erstellen
		//außerdem könnte im Zuge dessen auch ein berichtswesen eingebaut werden
	}
	
	private void ordersForNewRound(){
		
		market.genOrdersForNewRound(); 
		market.splitOrders(players);
		
		//An Client die Aufträge des Players senden UND die CapacityLeft im Player erneuern
		for (int i = 0; i < players.length; i++) {
			//Jede Runde wird die noch verfügbare Capazity auf die Gesamt Cap. des Player gesetzt. 
			//Diese wird dann jeweils abgebaut durch Erfüllung eines Auftrags. Wenn die Ges. Capazity höher
			//als die angenommen Aufträge liegt geht der Cap. Überschuss in der nächsten Periode verloren.
			players[i].setCapacityLeft(players[i].getData().get(quartal-1).getCapacity());
			handler.sendPlayerOrderPool(players[i].getId(), players[i].getPlayerOrderPool());
		}
	}
	
	private void startNewRound() {
		quartal ++; //auf nächstes Quartal gehen.
		
		//Die Eingabe für den User reaktivieren
		for (int i = 0; i < players.length; i++) {
			handler.setStatusForInputValues(true, i);
		}
				
		handler.newRoundStarted(players);//hier müssen die User informiert werden und können ihre Aufträge annhemen oder ablehen
		//außerdem werden hier Berichte übermittelt etc.
	}



	//Methode wird vom Handler aufgerufen, sobald alle Connections ready sind
	public void startGame(Vector<Conn> playersCon)
	{
		generatePlayers(playersCon);
		ordersForNewRound();	//Aufträge verteilen
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

	public void newCreditOffer(String substring, String nick) {	//Höhe, Laufzeit		
		// TODO Kreditaufnahme (langfristig)
		Player player = getPlayerByNick(nick);
		double[] offer = new double[3];
		if(player != null)
		{
			offer = bank.getCreditOffer(player, substring);
		}
		handler.offerCredit(offer, nick);
	}
	
	public static int getQuartal(){
		return quartal;
	}
	//NUR FÜRS TESTEN
	
	public Market getMarket(){
		return market;
	}
	
	public Player[] getPlayers(){
		return players;
	}
}
