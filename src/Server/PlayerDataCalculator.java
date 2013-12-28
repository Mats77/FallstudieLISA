package Server;

public class PlayerDataCalculator {
	private Mechanics mechanics;	//vllt statt der Beziehung zu den Playerdata eher eine zu GameHistory?!
	
	public PlayerDataCalculator(Mechanics m)
	{
		this.mechanics = m;
	}
	
	public double calcCostPerAirplane(int[] values) //Array: Produktion;Marketing;Entwicklung;Anzahl Flgzeuge;Materialstufe;Preis
	{
		double overheadCostsPerPlan = calcOverHeadCostsPerPlane(values);
		double individualCostsPerPlan = calcIndividualCostsPerPlan(values);
		System.out.println(overheadCostsPerPlan+"+"+individualCostsPerPlan);
		
		return overheadCostsPerPlan + individualCostsPerPlan;
	}//calcCostPerAirplane
	
	private double calcIndividualCostsPerPlan(int[] values) {
		if(values[4]==0)
		{
			return 100;
		} else if (values[4]==1){
			return 110;
		}else if(values[4]==2){
			return 120;
		} else return -1;
	}

	private double calcOverHeadCostsPerPlane(int[] values) {
		return (values[0] + values[1] + values[2])/values[3];
	}

	public double calcProfit()
	{
		double toReturn = 0;
		//TODO
		return toReturn;
	}//calcProfit
	
	
	//Die Kosten der Investitionen werden von dem alten Cash-Betrag abgezogen
	public double calcCashFlow(int[] tmpValues, PlayerData[] data) {
		double cashOld = 0;
		for (int i = 0; i < data.length; i++) {
			if(data[i] != null)
			{
				cashOld = data[i].getMoney();
			} else {
				break;
			}
		}
		double cashNew = cashOld - tmpValues[0] - tmpValues[1] - tmpValues[2];
		return cashNew;
	}

	public void generateNewCompanyValues(Player[] players) {
		for (Player player : players) {
			PlayerData[] playerData = player.getData();
			double research = 0;
			double marketing = 0;
			System.out.println(playerData.length);
			//TODO hier passiert leider noch nichts
		}
	}
}
