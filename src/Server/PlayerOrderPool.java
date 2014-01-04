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
				finischedOrders.add(order);
				acceptedOrders.remove(order);
				player.addCash(order.getQuantity()
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

	public ArrayList<Order> getAcceptedOrders() {
		return acceptedOrders;
	}

	public ArrayList<Order> getNewOrders() {
		return newOrders;
	}

}