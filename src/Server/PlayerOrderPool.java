package Server;

import java.util.ArrayList;

public class PlayerOrderPool {

	private ArrayList<Order> acceptedOrders = new ArrayList<Order>();
	private ArrayList<Order> newOrders = new ArrayList<Order>();
	private ArrayList<Order> finischedOrders = new ArrayList<Order>();
	private OrderPool orderPool = null;
	private Player player;

	public PlayerOrderPool(Player player) {
		this.player = player;
	}

	// Wird vom Market aufgerufen beim Verteilen der Orders
	public void addNewOrder(Order order) {
		newOrders.add(order);
	}
	
	//Wird von Mechanics aufgerufen, für jede Order die der Player für das Quartal zur Herstellung markiert hat. 
	public void produceOrder(Order order) {
		
		//Wenn die CapacityLeft des Players größer ist, als die  QuantityLeft der Order, wird die Order abgeschlossen
		if(order.getQuantityLeft()<=player.getCapacityLeft()){
			//Capacity Left des Players um die produzierte Menge herabsetztn
			player.setCapacityLeft(player.getCapacityLeft()-order.getQuantityLeft());
			finischedOrders.add(order);
			acceptedOrders.remove(order);
			player.addCash(order.getQuantity()*300);
			
			//Wert setzten bezüglich pünktlicher Erfüllung des Auftrags
			int reliability	= order.getQuartalValidTo()-Mechanics.getQuartal();
			if(reliability>=0){
				reliability++;
			}
			//Für die pünktliche Erfüllung gibt einen Punkt, für jedes zu frühes Quartal einen weiteren. 
			//Zu späte Erfüllung wird mit einem Punkt  pro Quartal abgewertet 
			
			player.setReliability(player.getReliability()+reliability);
		}else{
		//Ansonsten wird die restlich verfügbare Capcity des Players zur Teilweisen Erfüllung des Auftrags genutzt.
			order.setQuantityLeft(order.getQuantityLeft()-player.getCapacityLeft());
			player.setCapacityLeft(0);
		}
		
		

		
	}

	public void unacceptOrder(Order order) {
		newOrders.remove(order);
		orderPool.addOneOrderToPool(order);
	}

	public void acceptOrder(Order order) {
		acceptedOrders.add(order);
	}

	public ArrayList<Order> getAcceptedOrders() {
		return acceptedOrders;
	}

	public ArrayList<Order> getNewOrders() {
		return newOrders;
	}
	
	public boolean equals(PlayerOrderPool order) {
		return (getClass()==	order.getClass());
		
	}

}