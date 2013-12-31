package Server;

import java.util.ArrayList;

public class PlayerOrderPool {
	
	private ArrayList<Order> acceptedOrders = new ArrayList<Order>();
	private ArrayList<Order> newOrders = new ArrayList<Order>();
	private OrderPool orderPool;

public PlayerOrderPool(OrderPool orderPool){
	this.orderPool = orderPool;
}


public void addNewOrder(Order order){
	newOrders.add(order);
}


public void unacceptOrder(Order order){
	newOrders.remove(order);
	orderPool.addOneOrderToPool(order);
}

public void acceptOrder(Order order){
	acceptedOrders.add(order);
}



}