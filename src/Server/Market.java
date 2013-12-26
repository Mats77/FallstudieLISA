package Server;

public class Market {

	private OrderHistory orderHistory;
	private OrderPool orderPool;
	
	public Market() {
		orderHistory = new OrderHistory();
		orderPool = new OrderPool();
	}

	public void calcMarketShare(){
		calcDev();
		calcMarketing();
		// Zusätzlich Zugriff auf GameHistory notwendig
	}
	
	public void getOrdersForNewRound(){
		orderPool.ordersForNewRound();
	}
	
	private void splitOrders(){
		Order bestOrder = orderPool.getBestOrder();
	}
		
	private void calcDev(){
		
	}
	
	private void calcMarketing(){
		
	}
	
	

}
