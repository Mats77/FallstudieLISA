package Server;

import java.util.Vector;

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
	public double calcCashFlow(int[] tmpValues, Vector<PlayerData> data) {
		double cashOld = 0;
		cashOld = data.get(data.size()-1).getMoney();
		double cashNew = cashOld - tmpValues[0] - tmpValues[1] - tmpValues[2];
		return cashNew;
	}

	public double[] generateNewCompanyValues(Player[] players) {
		//ermitteln der Daten
		double[] researchData = new double[players.length];
		double[] marketingData = new double[players.length];
		double researchOverall = 0;
		double marketingOverall = 0;
		int playerCtr = 0;
		for (Player player : players) {
			Vector<PlayerData> playerData = player.getData();
			double research = 0;
			double marketing = calculateMarketing(playerData);
			marketingOverall += marketing;
			
			research = calculateResearch(playerData);
			researchOverall+= research;
			
			researchData[playerCtr] = research;
			marketingData[playerCtr] = marketing;
			playerCtr++;
		}//äußere Schleife Spieler
		
		//Berechnen der Werte
		double[] companyValues = new double[players.length];
		for(int i=0; i<companyValues.length; i++)
		{
			double research = researchData[i];
			double marketing = marketingData[i];
			double erg;
			if(marketing < research)
			{
				if(marketing/research < 0.3/0.7)
				{
					erg = (marketing+research)*(1.5 + ((marketing/research)-(0.3/0.7)));
				} else {
					erg = (marketing+research)*(1.5 - ((marketing/research)-(0.3/0.7)));
				}
			} else {
				erg = (research+marketing)*(1.5-(1-(research/marketing-0.3/0.7)));
			}
			if(erg < marketing + research){
				erg = marketing+research;
			}
			companyValues[i]=erg;
			//companyValues[i] += players[i].getReputation();
			// muss noch mit dem Preis in Verbindung gebracht werden;
		}//for Schleife, die Werte aufaddiert, hier müssen später noch die Verhältnisse rein
		return companyValues;
	}//generateNewCompanyValues

	
	//PRIVATE!!!!!!!
	public double calculateResearch(Vector<PlayerData> playerData) {
		double research =0;
		
		for (PlayerData playerDataItem : playerData) {
			research+= playerDataItem.getResearch();
		}
		
		return research;
	}
	
	//PRIVATE!!!!!!!
	public double calculateMarketing(Vector<PlayerData> playerData) {
		double marketing =0;
		if(playerData.size()>=3)
		{
			marketing = playerData.get(playerData.size()-1).getMarketing()+playerData.get(playerData.size()-2).getMarketing()+playerData.get(playerData.size()-3).getMarketing();
		} else if(playerData.size()==2){
			marketing = playerData.get(playerData.size()-1).getMarketing()+playerData.get(playerData.size()-2).getMarketing();
		} else if(playerData.size()==1) {
			marketing = playerData.get(playerData.size()-1).getMarketing();
		}
		return marketing;
	}
}//Class
