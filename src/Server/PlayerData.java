package Server;

public class PlayerData {
	private int playerID;
	private int round;
	private double money;
	private double marketshare; // TODO im Klassendiagramm aendern
	private double production;
	private double research;
	private double marketing;
	private double turnover;
	private int airplanes;
	private double pricePerAirplane;

	public PlayerData(int pid, int round, double money, double marketshare, double production, double research,
			double marketing, double turnover, int airplanes, double pricePerAirplane) {
		this.playerID = pid;
		this.round = round;
		this.money = money;
		this.marketshare = marketshare;
		this.production = production;
		this.research = research;
		this.marketing = marketing;
		this.turnover = turnover;
		this.airplanes = airplanes;
		this.pricePerAirplane = pricePerAirplane;
	}//Konstruktor

	public int getPlayerID() {
		return playerID;
	}

	public int getRound() {
		return round;
	}

	public double getMoney() {
		return money;
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

	
}//class
