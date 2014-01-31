package Server;

public class ShortTimeCredit extends Credit {

	public ShortTimeCredit(double cashAfterInvestments, Player player) {
		debtor = player;
		amount = cashAfterInvestments;
		interesRate = 0.15;
		debtor.addAmountOfShortTimeCredit(cashAfterInvestments);
		debtor.addCash(cashAfterInvestments);
	}
	
	public void addAmount(double amount)
	{
		this.amount += amount;
		debtor.addDebtCapital(amount);
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
}
