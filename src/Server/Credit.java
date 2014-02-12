package Server;

public abstract class Credit {
	protected Player debtor;
	protected double amount;
	protected double interesRate;
	
	//Abstrakte Klasse, die nur das grundlegende Gerüst eines Kredits für die anderen Kredite darstellt
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
