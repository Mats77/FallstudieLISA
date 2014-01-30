package Server;

public class LongTimeCredit extends Credit {
	private double runtime;
	private double runtimeLeft;

	public LongTimeCredit(Player player, double amount, double runtime, double interestRate) {
		debtor = player;
		this.amount = amount;
		this.runtime = runtime;
		this.interesRate = interestRate;
		debtor.addDebtCapital(amount);
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

	public double getRuntime() {
		return runtime;
	}

}
