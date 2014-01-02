package Server;

public class Credit {
	private Player debtor;
	private boolean shortTime;
	private double amount;
	private double interesRate;
	
	public Credit(double cashAfterInvestments, Player player, boolean shortTime) {
		if(shortTime)
		{
			debtor = player;
			this.shortTime = true;
			amount = cashAfterInvestments;
			interesRate = 0.15;
		}
	}
	
	public double getInterestsForQuarter()
	{
		return amount*(0.25*interesRate);
	}

}
