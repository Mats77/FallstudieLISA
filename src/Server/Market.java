package Server;

public class Market {

	private OrderHistory orderHistory;
	
	
	public Market() {
		orderHistory = new OrderHistory();
	}

	public void calcMarketShare(){
		calcDev();
		calcMarketing();
		// Zusätzlich Zugriff auf GameHistory notwendig
	}
	
	public Order getOrdersForNewRound(){
		orderSuccess();
		genOrders();
		splitOrders();
		return null;
	}
	
	private void splitOrders(){
		
	}
	
	private void genOrders(){
		
	}
		
	private void calcDev(){
		
	}
	
	private void calcMarketing(){
		
	}
	
	private void orderSuccess(){
		
	}
	

}
