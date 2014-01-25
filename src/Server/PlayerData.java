package Server;

/**
 * @author Mats
 *
 */
public class PlayerData {
	private int playerID;
	private int quartal;
	private double cash;
	private double marketshare; // TODO im Klassendiagramm aendern
	private double production;	//Fortlaufend
	private double research;
	private double marketing;
	private double turnover;
	private int airplanes;
	private double pricePerAirplane;
	private double fixCosts;
	private double varCosts;
	private int capacity;
	private int qualityOfMaterial;
	private double profit;
	private double costs;

	public PlayerData(int pid, int quartal, double money, double marketshare, double production, double research,
			double marketing, double turnover, int airplanes, double pricePerAirplane) {
		this.playerID = pid;
		this.quartal = quartal;
		this.cash = money;
		this.marketshare = marketshare;
		this.production += production;
		this.research = research;
		this.marketing = marketing;
		this.turnover = turnover;
		this.airplanes = airplanes;
		this.pricePerAirplane = pricePerAirplane;
		this.qualityOfMaterial = 1;
		
		//init für Kapazität und fixkosten
		this.production+=12500;
		this.capacity = (int)this.production/500;
		this.fixCosts = capacity*100;
		this.varCosts= airplanes*110;
		this.profit = turnover - fixCosts - varCosts - marketing - production - research;
		this.cash += profit;

	}//Konstruktor init

	public PlayerData(int id, int production, int marketing, int research, int airplanes, int quartal) {
		this.playerID = id;
		this.production = production;
		this.research = research;
		this.marketing = marketing;
		this.airplanes = airplanes;
		this.quartal = quartal;
	}

	public PlayerData(int id, int quartal, double turnover) {	//Dieser Konstruktor wird jede Runde für jeden Spieler aufgerufen,
		this.playerID = id;										//anschließend werden nur noch über Setter Werte eingefügt
		this.quartal = quartal;
		this.turnover = turnover;
	}

	public int getPlayerID() {
		return playerID;
	}

	public int getQuartal() {
		return quartal;
	}

	public double getCash() {
		return cash;
	}

	public double getMarketshare() {
		return marketshare;
	}

	public double getProduction() {
		return production;
	}

	public double getResearch() {
		return research;
	}

	public double getMarketing() {
		return marketing;
	}

	public double getTurnover() {
		return turnover;
	}

	public int getAirplanes() {
		return airplanes;
	}

	public double getPricePerAirplane() {
		return pricePerAirplane;
	}
	
	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int capacitiy) {
		this.capacity = capacitiy;
	}

	public double getVarCosts() {
		return varCosts;
	}

	public void setVarCosts(int varCosts) {
		this.varCosts = varCosts;
	}

	public int getQualityOfMaterial() {
		return qualityOfMaterial;
	}

	public double getFixCosts() {
		return fixCosts;
	}

	public void setFixCosts(double fixCosts) {
		this.fixCosts = fixCosts;
	}

	public double getCosts() {
		return costs;
	}

	public void setCosts(double costs) {
		this.costs = costs;
	}

	public double getProfit() {
		return profit;
	}

	public void setProfit(double profit) {
		this.profit = profit;
	}

	public void setCash(double money) {
		this.cash = money;
	}
	
	//Zum Testen
	
	public void setProduction(int production)
	{
		this.production = production;
	}

	public void setQualityOfMaterial(int qualityOfMaterial) {
		this.qualityOfMaterial = qualityOfMaterial;
	}

	public void setAirplanes(int airplanes) {
		this.airplanes = airplanes;
	}

	public void setMarketshare(double marketshare) {
		this.marketshare = marketshare;
	} 

	
}//class
