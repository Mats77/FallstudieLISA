package Server;

import java.util.Vector;

public class Bank {

	public Credit getShortTimeCredit(double cashAfterInvestments, Player player) {
		return generateShortTimeCredit(cashAfterInvestments, player);
		
	}

	private Credit generateShortTimeCredit(double cashAfterInvestments,
			Player player) {
		return new Credit (cashAfterInvestments, player, true);
	}

	public void getCreditOffer(Player player, String substring) {		//Höhe, Laufzeit
		double[] dataOfCredit = new double[2];
		
		for(int i=0; i<2; i++)
		{
			try
			{
				dataOfCredit[i] = Double.parseDouble(substring.split(";")[i]);
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}

		double amount = dataOfCredit[0];
		double runtime = dataOfCredit[1];
		double creditRating = getCreditRating(player);
	}

	private double getCreditRating(Player player) {
		double interestToReturn = 0.1;
		
		Vector<PlayerData> data = player.getData();
		double playerCash = player.getCash();
		Vector<Credit> creditsOfPlayer = player.getCredits();
		double amountOfShortTimeCredits = 0;
		double amountOfLongTimeCredits = 0;
		
		for (Credit credit : creditsOfPlayer) {
			if(credit.isShortTime())
			{
				amountOfShortTimeCredits += credit.getAmount();
			} else {
				amountOfLongTimeCredits += credit.getAmount();
			}
		}
		
		if(true)
		{
		}
		return interestToReturn;
	}
}
