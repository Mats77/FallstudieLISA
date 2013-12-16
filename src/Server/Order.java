package Server;


public class Order {

	private int playerID;
	private int roundNumber;
	private int overAllOrders;
	private int ordersForPlayer;
	private int smallOrders;
	private int smallPrice;
	private int smallTime;
	private int medOrders;
	private int medPrice;
	private int medTime;
	private int largeOrders;
	private int largePrice;
	private int largeTime;
	
	
	public Order(int playerID, int roundNumber, int overAllOrders, int ordersForPlayer) {
		this.playerID = playerID;
		this.roundNumber = roundNumber;
		this.overAllOrders = overAllOrders;
		this.ordersForPlayer = ordersForPlayer;
		calcOrders();
	}
	
	public int getOrdersForPlayer(int playerID, int category){
		if (category == 0) { // small Orders
			return smallOrders;
		}else if (category == 1) { // medium Orders
			return medOrders; 
		}else if (category == 2) { // large Orders
			return largeOrders;
		}else{
			return 99;
		}

	}
	
	private void calcOrders(){
		calcSmallOrders();
		calcMidOrder();
		calcLargeOrder();
	}
	
	private void calcSmallOrders(){
		
	}
	
	private void calcMidOrder(){
		
	}
	
	private void calcLargeOrder(){
		
	}
	
	
	
}
