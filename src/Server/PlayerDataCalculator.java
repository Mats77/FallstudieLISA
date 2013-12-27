package Server;

public class PlayerDataCalculator {
	private Mechanics mechanics;	//vllt statt der Beziehung zu den Playerdata eher eine zu GameHistory?!
	
	public PlayerDataCalculator(Mechanics m)
	{
		this.mechanics = m;
	}
	
	public double calcCostPerAirplane(int[] values) //Array: Produktion;Marketing;Entwicklung;Preis
	{
		double toReturn = 0;
		//TODO
		
		toReturn = (values[0]+values[1]+values[2])/30;
		
		return toReturn;
	}//calcCostPerAirplane
	
	public double calcProfit()
	{
		double toReturn = 0;
		//TODO
		return toReturn;
	}//calcProfit

}
