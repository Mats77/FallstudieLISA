package Server;

public class Bank {

	public Credit getShortTimeCredit(double cashAfterInvestments, Player player) {
		return generateShortTimeCredit(cashAfterInvestments, player);
		
	}

	private Credit generateShortTimeCredit(double cashAfterInvestments,
			Player player) {
		return new Credit (cashAfterInvestments, player, true);
	}
	
	

}
