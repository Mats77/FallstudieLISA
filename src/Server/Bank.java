package Server;

import java.util.Vector;

public class Bank {

	public ShortTimeCredit getShortTimeCredit(double cashAfterInvestments, Player player) 
	{
		return generateShortTimeCredit(cashAfterInvestments, player);
	}

	private ShortTimeCredit generateShortTimeCredit(double cashAfterInvestments,
			Player player) {
		return new ShortTimeCredit (cashAfterInvestments, player);
	}
	
	public void generateLongTimeCredit(Player player, double[] creditData) {
		player.getCredits().add(new LongTimeCredit(player, creditData[0], creditData[1], creditData[2]));
	}

	public double[] getCreditOffer(Player player, String substring) {		//Höhe, Laufzeit
		double[] dataOfCredit = new double[2];
		
		for(int i=0; i<2; i++)
		{
			try
			{
				dataOfCredit[i] = Double.parseDouble(substring.split(",")[i]);
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}

		double amount = dataOfCredit[0];
		double runtime = dataOfCredit[1];
		double creditRating = getCreditRating(player);
		
		double[] toReturn = {amount, runtime, creditRating};
		return toReturn;
	}

	private double getCreditRating(Player player) {
		double interestToReturn = 0;
		
		//Daten auslesen
		Vector<PlayerData> data = player.getData();
		double playerCash = player.getCash();
		Vector<LongTimeCredit> creditsOfPlayer = player.getCredits();
		double totalAmountOfCredits = 0;
		double debtRatio = totalAmountOfCredits/(totalAmountOfCredits + playerCash);
		
		for (LongTimeCredit credit : creditsOfPlayer) {
			totalAmountOfCredits += credit.getAmount();
		}
		
		//Vertikale Finanzierungsregeln
		if(debtRatio < 0.5)
		{
			interestToReturn = 0.09;
		} else if(0.5 < debtRatio && debtRatio < 0.66){
			interestToReturn = 0.11;
		} else if(0.66 < debtRatio && debtRatio < 0.75){
			interestToReturn = 0.13;
		} else {
			interestToReturn = 0.14;
		}
		
		return interestToReturn;
	}
}
