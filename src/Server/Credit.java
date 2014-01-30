package Server;

public abstract class Credit {
	protected Player debtor;
	protected double amount;
	protected double interesRate;
	

	public double getInterestsForQuarter()
	{
		return amount*(0.25*interesRate);
	}

	public double getAmount() 
	{
		return amount;
	}

	public double getInterestRate() {
		return interesRate;
	}

}
