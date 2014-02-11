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
				order.setStatus(2);
				acceptedOrders.add(order);
				newOrders.remove(order);
				order.setPrice(player.getData().lastElement().getPricePerAirplane());
				break;
			}
		}
	}
	
	/**
	 * Füg die Order in die AcceptedOrder List
	 * @param orderID
	 */
	public void produceOrder(int orderID)
	{
		for (Order order : acceptedOrders) {
			if(order.getOrderId() == orderID)
			{
				order.setStatus(1);
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

	
	/**
	 *1. Verschiebt die zu produziernden Orders in die finischedOrder List;
	 * 2.Prüft ob die zu produzierde Menge mit der Kapaziät übereinstimmt und falls die zu prod. Menge größe ist
	 *	 werden die noch zu prod. Stück in die AcceptedOrder list mit der verringerten QuanityLeft zurückgegeben;
	 * 3.Prüft ob AcceptedOrders verspätet sind und setzt dann entsprechend den Status 3 in der Order
	 * 4.Gibt alle nicht angenommenen Orders in den Makrt-OrderPool zurück zur erneuten Verteilung.;
	 */
	@SuppressWarnings("unchecked")
	public void refreshData() {
		int ctr=0;
		//Aufaddieren der Produktionsmenge
		for (Order order : toProduce) {
			ctr += order.getQuantityLeft();
		}
		//Vergleichen der zu produzierenden Menge mit der Vorheringen Kapazität
		if(ctr <= player.getData().get(player.getData().size()-2).getCapacity() )
		{
			//Verschieben der Orders von toProduce in finisched Orders
			for (Order order : toProduce) {
				finishedOrders.add(order);
				toProduce.remove(order);
			}
			//Verschieben der toProduceNextRound Orders in toProduceOrders
			toProduce = (CopyOnWriteArrayList<Order>) toProduceNextRound.clone();
			for (Order order : toProduce) {
				order.setStatus(1);
			}
			toProduceNextRound = new CopyOnWriteArrayList<Order>();
			
		//Else: Die zu produzierende Menge ist größer, als die Kapazität
		} else {
			for(int i = 0; i < toProduce.size() - 1; i++){ //Die letzte Order bleibt in der toProduce Liste stehen (For -Bedingung < & -1)
				finishedOrders.add(toProduce.get(i));
				toProduce.get(i).setQuantityLeft(0);
				toProduce.remove(i);
			}
			//Die noch zu produzierende Quantity der lezten Order wird berechnet. (Weil sie nicht komplett produziert werden kann)
			toProduce.get(0).setQuantityLeft(ctr - player.getData().lastElement().getCapacity());
			acceptedOrders.add(toProduce.get(0));//Verschieben der übrig gebliebenden Order in Accepted OrdersList
			toProduce.clear();
			toProduce = (CopyOnWriteArrayList<Order>) toProduceNextRound.clone();
			toProduceNextRound = new CopyOnWriteArrayList<Order>();
		}
		//Prüfen ob angenommene Orders, die noch nicht produziert werden verspätet sind.
		for (Order order : acceptedOrders) {
			if(Mechanics.getQuartal() > order.getQuartalValidTo())
			{
				order.setStatus(3);
			}
		}
		/*Alle nicht angenommennen Oders, die einem Player vorgeschlagen wurden, werden automarisch zurück in den OrderPool gegeben und im nächsten 
		Quartal erneut verteilt.*/
		for (Order order : newOrders) {
			newOrders.remove(order);
			orderPool.addOneOrderToPool(order);
		}
	}

	public CopyOnWriteArrayList<Order> getToProduceNextRound() {
		return toProduceNextRound;
	}

	public void setOrderPool(OrderPool orderPool) {
		this.orderPool = orderPool;
	}
	
	public CopyOnWriteArrayList<Order> getOrdersToDisplay()
	{
		CopyOnWriteArrayList<Order> toReturn = new CopyOnWriteArrayList<Order>();
		toReturn = (CopyOnWriteArrayList<Order>) toProduce.clone();
		for (Order order : toProduceNextRound) {
			toReturn.add(order);
		}		
		for (Order order : acceptedOrders) {
			toReturn.add(order);
		}
		return toReturn;
	}
}