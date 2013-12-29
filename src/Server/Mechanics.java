package Server;

import java.util.Vector;

public class Mechanics {
	private String[] playerValues;
	private Market market;
	private Player[] players;
	private Handler handler;
	private PlayerDataCalculator playerDataCalculator;
	private Bank bank = new Bank();
	
	
	public Mechanics(Handler h) {
		market = new Market();
		playerDataCalculator = new PlayerDataCalculator(this);
		this.handler = h;
	}



	public void setPlayerValues (String values, int playerNumber){
		playerValues[playerNumber] = values;
	}

	//wird vom Handler aufgerufen, sobald ein Spieler seine Werte eingegeben hat
	public void valuesInserted(String values, String nick) {
		for(int i=0; i<players.length; i++)
		{
			String tmp = players[i].getNick();
			if(tmp.equalsIgnoreCase(nick))
			{
				players[i].saveNextRoundValues(values);
				break;
			}
		}
		if(areAllReadyForNextRound())
		{
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
	
	private void startNewRound() {
		//Spieler erhalten Geld für erfüllte Aufträge
		//Periodenabschluss	(Bilanz, GuV, Berichte)
		for (int i = 0; i < players.length; i++) {	//Ausgabe für neue Investitionen, ggf. decken durch kurzfristige Kredite
			players[i].setReadyForNextRound(false);
			players[i].calculateRoundValues();
		}
		double[] values = playerDataCalculator.generateNewCompanyValues(players);	//neue Werte berechnen für die neuen Aufträge
		for(int i=0; i<players.length; i++)
		{
			players[i].setCompanyValue(values[i]);
		}
		//Verteilung neue Aufträge
		handler.newRoundStarted();//hier müssen die User informiert werden und können ihre Aufträge annhemen oder ablehen
	}



	//Methode wird vom Handler aufgerufen, sobald alle Connections ready sind
	public void startGame(Vector<Conn> playersCon)
	{
		generatePlayers(playersCon);
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
	
	//NUR FÜRS TESTEN
	
	public Player[] getPlayers(){
		return players;
	}
}
