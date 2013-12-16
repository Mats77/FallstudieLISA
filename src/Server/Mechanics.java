package Server;

public class Mechanics {
	private String[] playerValues;
	private Market market;
	
	
	
	public Mechanics() {
		market = new Market();
	}



	public void setPlayerValues (String values, int playerNumber){
		playerValues[playerNumber] = values;
	}

	
}
