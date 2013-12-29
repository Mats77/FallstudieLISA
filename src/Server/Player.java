package Server;

import java.util.Vector;

public class Player {
	private String nick;
	private int id;
	private Vector<PlayerData> data = new Vector<PlayerData>();
	private boolean readyForNextRound = false;
	private int[] tmpValues;
	private PlayerDataCalculator playerDataCalculator;
	private Mechanics mechanics;
	private Vector<Credit> credits = new Vector<Credit>();
	private double companyValue;

	//Konstruktor
	public Player(long id, String name, PlayerDataCalculator pdc, Mechanics m) {
		this.playerDataCalculator = pdc;
		this.id = (int)id;
		this.nick = name;
		data.add(new PlayerData((int)id, 0, 7500, 25, 0, 0, 0, 7500, 25, 300));
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
		data.add(new PlayerData(id, tmpValues[0], tmpValues[1], tmpValues[2], tmpValues[3]));
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

	public Vector<PlayerData> getData() {
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


	public void setCompanyValue(double d) {
		companyValue = d;
	}
	
	public double getCompanyValue(){
		return companyValue;
	}
}
