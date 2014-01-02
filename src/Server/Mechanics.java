package Server;

import java.util.Vector;

public class Mechanics {
	private Market market;
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
				break;
			}
		}
		if(areAllReadyForNextRound())
		{	
			endRound();
			startNewRound();
		}
	}

	private boolean areAllReadyForNextRound() {
		boolean allReadyForNextRound = true;
		for(int i=0; i<players.length; i++){
			if(!players[i].isReadyForNextRound())
			{
				allReadyForNextRound = false;
			}
		}
		return allReadyForNextRound;
	}
	
	private void endRound(){
		for (int i = 0; i < players.length; i++) {
			players[i].setReadyForNextRound(false);
		}
		//Chris: einstellen welche Spieler welchen Auftrag annehme, bzw. welchen bearbeiten
		
		//neue Werte berechnen für die neuen Aufträge und Investitionsausgaben für F&E, Marketing
		double[] values = playerDataCalculator.generateNewCompanyValues(players);
		for(int i=0; i<players.length; i++)
		{
			players[i].setCompanyValue(values[i]);
		}
		
		
		playerDataCalculator.calcCapacities(players);	//Kapazitäten errechnen und Produktionsinvestition
		playerDataCalculator.calcCosts(players);		//Kosten errechnen und vom Cash abziehen
		
		//Geld bekommen für erfüllte Aufträge
		playerDataCalculator.setTurnover(players);
		playerDataCalculator.calcProfits(players);
		//Quartalsabschluss ---> Jemand muss noch anhand der hier schon vollsätndigen Daten die Jahresabschlüsse erstellen
		//außerdem könnte im Zuge dessen auch ein berichtswesen eingebaut werden
	}
	
	private void startNewRound() {
		quartal ++; //auf nächstes Quartal gehen.
		market.genOrdersForNewRound(); 
		market.splitOrders(players);
		
		handler.newRoundStarted();//hier müssen die User informiert werden und können ihre Aufträge annhemen oder ablehen
		//außerdem werden hier Berichte übermittelt etc.
	}



	//Methode wird vom Handler aufgerufen, sobald alle Connections ready sind
	public void startGame(Vector<Conn> playersCon)
	{
		generatePlayers(playersCon);
		startNewRound();	//Aufträge verteilen
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



	public void newCredit(String substring, String nick) {	//Höhe, Zins, Laufzeit
		// TODO Kreditaufnahme (langfristig)
		
	}
	
	public static int getQuartal(){
		return quartal;
	}
	//NUR FÜRS TESTEN
	
	public Player[] getPlayers(){
		return players;
	}
}
