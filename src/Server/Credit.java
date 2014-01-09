package Server;

public class Credit {
	private Player debtor;
	private boolean shortTime;
	private double amount;
	private double interesRate;
	private double runtime;
	private double runtimeLeft;
	
	public Credit(double cashAfterInvestments, Player player, boolean shortTime) {
		if(shortTime)
		{
			debtor = player;
			this.shortTime = true;
			amount = cashAfterInvestments;
			interesRate = 0.15;
			debtor.addAmountOfShortTimeCredit(cashAfterInvestments);
			debtor.addCash(cashAfterInvestments);
		} else {
			
		}
	}
	
	public double getInterestsForQuarter()
	{
		return amount*(0.25*interesRate);
	}

	public boolean isShortTime() 
	{
		return shortTime;
	}

	public double getAmount() 
	{
		return amount;
	}
	
	public double payBackShortTimeCredit(double amount)
	{
		if(amount > this.amount)
		{
			double tmp = this.amount;
			this.amount = 0;
			debtor.reduceDeptCapital(tmp);
			return amount - tmp;
		} else {
			this.amount -= amount;
			debtor.reduceDeptCapital(amount);
			return 0;
		}
	}
	
	public void addAmount(double amount)
	{
		if(isShortTime())
		{
			this.amount += amount;
			debtor.addDebtCapital(amount);
		}
	}
	
	public boolean reduceRuntimeLeft()
	{
		runtime -= 0.25;
		if(runtime <= 0)
		{
			return true;
		} else {
			return false;
		}
	}

}
