package Server;

import java.util.ArrayList;
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
	private PlayerOrderPool orderPool = new PlayerOrderPool(null);
	private double cash;

	//Konstruktor
	public Player(long id, String name, PlayerDataCalculator pdc, Mechanics m) {
		this.playerDataCalculator = pdc;
		this.id = (int)id;
		this.nick = name;
		data.add(new PlayerData((int)id, 0
				, 5000, 25, 500, 500, 500, 7500, 25, 300));	//5000 ist Startbetrag
		mechanics = m;
		this.cash = data.lastElement().getCash();
	}
	
	public void endRound(){
		
	}

	
	public void saveNextRoundValues(String values, int quartal) {	//String: Produktion;Marketing;Entwicklung;Anzahl Flugzeuge;Materialstufe;Preis
		readyForNextRound = true;
		int[] insertedValues = new int[values.split(";").length];
		for(int i=0; i< insertedValues.length; i++)
		{
			insertedValues[i]=Integer.parseInt(values.split(";")[i]);
		}
		tmpValues = insertedValues;
		int tmpProduction = (int)data.lastElement().getProduction()+tmpValues[0];	//Produktion ist fortlaufend
		if(tmpValues[3]<=data.lastElement().getCapacity())
		{
			data.add(new PlayerData(id/*,cash*/, tmpProduction, tmpValues[1], tmpValues[2], tmpValues[3],/*tmpValues[4],*/ quartal));
		} else {//Falls der Spieler mehr produzieren möchte, als er Kapazitäten hat
			data.add(new PlayerData(id/*,cash*/, tmpProduction, tmpValues[1], tmpValues[2], data.lastElement().getCapacity(),/*tmpValues[4],*/ quartal));
		}
		//wird initialisiert mit dem Cash, das am Anfang der runde zur Verfügung stand.
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
	
	public void setMoney(){
		
	}
	
	public void addNewOrder(Order order){
		orderPool.addNewOrder(order);
	}
	
	public double getCompanyValue(){
		return companyValue;
	}
	
	public void addCash(double amount)
	{
		this.cash += amount;
	}
	
	public void spendMoney(double amount)
	{
		this.cash -= amount;
		if(this.cash < 0)
		{
			credits.add(mechanics.getBank().getShortTimeCredit(-cash, this));
		}
	}

	public Vector<Credit> getCredits() {
		return credits;
	}
}
