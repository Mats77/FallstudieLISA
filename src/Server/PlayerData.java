package Server;

public class PlayerData {
	private int playerID;
	private int round;
	private double money;
	private double marketshare; // TODO im Klassendiagramm aendern
	private int produktion;
	private int forschung;
	private int marketing;
	private double umsatz;
	private int flugzeuge;
	private double flugzeugpreis;

	public PlayerData(int pid, int r, double m, double ms, int p, int f,
			int mg, double u, int fg, double fp) {
		this.playerID = pid;
		this.round = r;
		this.money = m;
		this.marketshare = ms;
		this.produktion = p;
		this.forschung = f;
		this.marketing = mg;
		this.umsatz = u;
		this.flugzeuge = fg;
		this.flugzeugpreis = fp;
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

	public int getProduktion() {
		return produktion;
	}

	public int getForschung() {
		return forschung;
	}

	public int getMarketing() {
		return marketing;
	}

	public double getUmsatz() {
		return umsatz;
	}

	public int getFlugzeuge() {
		return flugzeuge;
	}

	public double getFlugzeugpreis() {
		return flugzeugpreis;
	}

	
}//class
