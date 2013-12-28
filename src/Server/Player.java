package Server;

import java.util.Vector;

public class Player {
	private String nick;
	private int id;
	private PlayerData[] data = new PlayerData[10];		//TODO Variabel machen für anzahl der Runden
	private boolean readyForNextRound = false;
	private int[] tmpValues;
	private PlayerDataCalculator playerDataCalculator;
	private Mechanics mechanics;
	private Vector<Credit> credits = new Vector<Credit>();

	//Konstruktor
	public Player(long id, String name, PlayerDataCalculator pdc, Mechanics m) {
		this.playerDataCalculator = pdc;
		this.id = (int)id;
		this.nick = name;
		data[0] = new PlayerData((int)id, 0, 7500, 25, 0, 0, 0, 7500, 25, 300);
		mechanics = m;
	}

	
	public void saveNextRoundValues(String values) {	//String: Produktion;Marketing;Entwicklung;Anzahl Flugzeuge;Materialstufe;Preis
		readyForNextRound = true;
		int[] insertedValues = new int[values.split(";").length];
		for(int i=0; i< insertedValues.length; i++)
		{
			insertedValues[i]=Integer.parseInt(values.split(";")[i]);
		}
		tmpValues = insertedValues;
	}


	public void calculateRoundValues() {
		double cashAfterInvestments = playerDataCalculator.calcCashFlow(tmpValues, data);
		if(cashAfterInvestments < 0 ) 
		{
			credits.add(mechanics.getBank().getShortTimeCredit(cashAfterInvestments, this));
			System.out.println(credits.get(0));
		}
		//Hier muss er das Geld für die Aufträge aus der Vorperiode bekommen und die neuen Aufträge annehmen können
		double costPerAirPlane = playerDataCalculator.calcCostPerAirplane(tmpValues);
		System.out.println("Meine Flugzeuge kosten "+costPerAirPlane);
	}
	
	//Getter und Setter

	public PlayerData[] getData() {
		return data;
	}


	public String getNick() {
		return nick;
	}

	public void setNick(String nick) {
		this.nick = nick;
	}
	
	public void setReadyForNextRound(boolean readyForNextRound) {
		this.readyForNextRound = readyForNextRound;
	}


	public boolean isReadyForNextRound() {
		return readyForNextRound;
	}
	
	public int getId()
	{
		return id;
	}
}
