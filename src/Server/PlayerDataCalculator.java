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
			player.spendMoney(research+marketing);
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
	
	
	public void calcProfits(Player[] players) {
		for (Player player : players) {
			PlayerData quartalData = player.getData().lastElement();
			double tmpProduction = quartalData.getProduction() - player.getData().elementAt(mechanics.getQuartal()-1).getProduction();
			quartalData.setProfit(quartalData.getTurnover() - quartalData.getCosts() - quartalData.getMarketing() - quartalData.getResearch() - tmpProduction);
		}
	}
	
	public void setTurnover (Player[] players) {
		for (Player player : players) {
			player.getData().add(new PlayerData(player.getId(),mechanics.getQuartal(),turnover));	//Hier muss Chris das einbauen, dass die Spieler Geld für Ihre Aufträge bekommen
			player.addCash(turnover);//geld erhöhen
		}
	}
	
	public void calcCapacities(Player[] players) {
		for (Player player : players) {
			PlayerData quartalData = player.getData().elementAt(mechanics.getQuartal());
			int capacity = (int)quartalData.getProduction()/500;
			quartalData.setCapacity(capacity);
			double productionInvestment = 0;
			if(player.getData().size()>1)
			{
				productionInvestment = quartalData.getProduction() - player.getData().elementAt(mechanics.getQuartal()-1).getProduction();
			}
			player.spendMoney(productionInvestment);
		}
	}
	
	public void calcCosts(Player[] players) {
		for (Player player : players) {
			PlayerData quartalData = player.getData().elementAt(mechanics.getQuartal());
			quartalData.setFixCosts(quartalData.getCapacity()*100);
			int tmp = quartalData.getQualityOfMaterial();
			if(tmp == 0)
			{
				quartalData.setVarCosts(100);
			} else if (tmp == 1) {
				quartalData.setVarCosts(110);
			} else if (tmp == 2) {
				quartalData.setVarCosts(120);
			}
			double interests = calcInterestCosts(player);
			quartalData.setCosts(quartalData.getFixCosts() + quartalData.getVarCosts()*quartalData.getAirplanes() + interests);
			player.spendMoney(quartalData.getCosts());
		}
	}
	
	private double calcInterestCosts(Player player)
	{
		double toReturn = 0;
		Vector<Credit> credits = player.getCredits();
		for (Credit credit : credits) {
			toReturn += credit.getInterestsForQuarter();
		}
		return toReturn;
	}

	
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
