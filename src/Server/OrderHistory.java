package Server;

public class OrderHistory {
	private Order[][] order;
	
	
	
	public OrderHistory() {
		order = new Order[5][99];
	}

	public Order getOrdersForRound(){
		return null;
	}
	
	public void ordersForNewRound(int roundNumber, int overAllOrders, int[] ordersForPlayer){ // Initialisiert der Bestellungen für die neue Runde
		for (int i = 0; i < 5; i++) {
			// Import der Spielerdateien ... Spieler-ID, Rundennummer (Außerdem Anzahl der Bestellungen gesamt + Anzahl der Bestellungen pro Spieler)
			order[i][roundNumber] = new Order(i, roundNumber, overAllOrders, ordersForPlayer[i]);
		}
	}
	
}
