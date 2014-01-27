package Server;

import java.util.concurrent.CopyOnWriteArrayList;

public class PlayerOrderPool {

	private CopyOnWriteArrayList<Order> acceptedOrders = new CopyOnWriteArrayList<Order>();
	private CopyOnWriteArrayList<Order> newOrders = new CopyOnWriteArrayList<Order>();
	private CopyOnWriteArrayList<Order> finishedOrders = new CopyOnWriteArrayList<Order>();
	private OrderPool orderPool = null;
	private Player player;
	private CopyOnWriteArrayList<Order> toProduce = new CopyOnWriteArrayList<Order>();
	private CopyOnWriteArrayList<Order> toProduceNextRound = new CopyOnWriteArrayList<Order>();

	public PlayerOrderPool(Player player) {
		this.player = player;
	}

	// Wird vom Market aufgerufen beim Verteilen der Orders aus dem OrderPool an
	// die Player
	public void addNewOrder(Order order) {
		newOrders.add(order);
	}

	// Wird von Mechanics aufgerufen und übergibt alle OrderIDs die für das
	// Quartal zu Produktion markiert wurden.
	public void newOrdersToProduce(int[] orderId) {

		Order order;
		for (int i = 0; i < orderId.length; i++) {
			order = getOrderWithId(orderId[i]);

			// Wenn die CapacityLeft des Players größer ist, als die
			// QuantityLeft der Order, wird die Order abgeschlossen
			if (order.getQuantityLeft() <= player.getCapacityLeft()) {
				// Capacity Left des Players um die produzierte Menge
				// herabsetztn
				player.setCapacityLeft(player.getCapacityLeft()
						- order.getQuantityLeft());
				finishedOrders.add(order);
				acceptedOrders.remove(order);
				player.addCash(order.getTotalQuantity()
						* order.getPricePerAirplane());

				// Wert setzten bezüglich pünktlicher Erfüllung des Auftrags
				int reliability = order.getQuartalValidTo()
						- Mechanics.getQuartal();
				if (reliability >= 0) {
					reliability++;
				}
				// Für die pünktliche Erfüllung gibt einen Punkt, für jedes zu
				// frühes Quartal einen weiteren.
				// Zu späte Erfüllung wird mit einem Punkt pro Quartal
				// abgewertet

				player.setReliability(player.getReliability() + reliability);
			} else {
				// Ansonsten wird die restlich verfügbare Capcity des Players
				// zur Teilweisen Erfüllung des Auftrags genutzt.
				order.setQuantityLeft(order.getQuantityLeft()
						- player.getCapacityLeft());
				player.setCapacityLeft(0);
			}

		}

	}

	// Sucht in den OrderPools nach der Order mit der entsprechenden OrderId
	private Order getOrderWithId(int orderId) {
		for (Order order : acceptedOrders) {
			if (order.getOrderId() == orderId)
				return order;
		}
		for (Order order : newOrders) {
			if (order.getOrderId() == orderId)
				return order;
		}

		// Es wurde keine Order in dem OrderPool gefunden, die mit der OrderId
		// vom Spieler übereinstimmt.
		System.err
				.println("Die OrderId der Order ist nicht im PlayerOrderPool vorhanden");
		return null;
	}

	public void unacceptOrder(int orderId) {
		Order order = getOrderWithId(orderId);

		newOrders.remove(order);
		orderPool.addOneOrderToPool(order);
	}

	// Wird vom Player aufgerufen und übergibt alle neuen Orders, die akzeptiert
	// wurden.
	public void newOrdersAccepted(int[] orderId) {
		Order order;

		for (int i = 0; i < orderId.length; i++) {
			order = getOrderWithId(orderId[i]);
			acceptedOrders.add(order);
			newOrders.remove(order);
			order.setPrice(player.getData().get(Mechanics.getQuartal())
					.getPricePerAirplane()); // Setzt den Flugzeugpreis auf die
		} // letzt Eingabe des Players
		
	}
	
	public void acceptOrder(int orderID){
		for (Order order : newOrders) {
			if(order.getOrderId() == orderID){
				acceptedOrders.add(order);
				newOrders.remove(order);
				order.setPrice(player.getData().lastElement().getPricePerAirplane());
				break;
			}
		}
	}
	
	public void produceOrder(int orderID)
	{
		for (Order order : acceptedOrders) {
			if(order.getOrderId() == orderID)
			{
				toProduceNextRound.add(order);
				acceptedOrders.remove(order);
				break;
			}
		}
	}
	
	public CopyOnWriteArrayList<Order> getAcceptedOrders() {
		return acceptedOrders;
	}

	public CopyOnWriteArrayList<Order> getNewOrders() {
		return newOrders;
	}

	public CopyOnWriteArrayList<Order> getFinishedOrders() {
		return finishedOrders;
	}
	
	public CopyOnWriteArrayList<Order> getOrdersToProduce() {
		return toProduce;
	}

	@SuppressWarnings("unchecked")
	public void refreshData() {
		int ctr=0;
		for (Order order : toProduce) {
			ctr += order.getQuantityLeft();
		}
		if(ctr <= player.getData().get(player.getData().size()-2).getCapacity() )
		{
			for (Order order : toProduce) {
				finishedOrders.add(order);
				toProduce.remove(order);
			}
			toProduce = (CopyOnWriteArrayList<Order>) toProduceNextRound.clone();
			toProduceNextRound = new CopyOnWriteArrayList<Order>();
		} else {
			for(int i = 0; i < toProduce.size() - 1; i++){
				finishedOrders.add(toProduce.get(i));
				toProduce.get(i).setQuantityLeft(0);
				toProduce.remove(i);
			}
			toProduce.get(0).setQuantityLeft(ctr - player.getData().lastElement().getCapacity());
			acceptedOrders.add(toProduce.get(0));
			toProduce.clear();
			toProduce = (CopyOnWriteArrayList<Order>) toProduceNextRound.clone();
			toProduceNextRound = new CopyOnWriteArrayList<Order>();
		}	
	}

	public CopyOnWriteArrayList<Order> getToProduceNextRound() {
		return toProduceNextRound;
	}

	public void setOrderPool(OrderPool orderPool) {
		this.orderPool = orderPool;
	}
}