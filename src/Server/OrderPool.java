package Server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class OrderPool {
	private ArrayList<Order> orderList;
	private static int quartal = 0;
	/* Ist das aktuelle Spielquartal. Das Spiel beginnt mit Q0 und wird nach
	 * erstellung der neuen Orders auf Q1 angehoben.
	 */

	public OrderPool() {
		orderList = new ArrayList<Order>();

		// Zu Spielbeginn soll die Methode genNewOrders 2 mal durchgeführt
		// werden um für Runde 1 genügend Aufträge zu erstellen.
		for (int i = 0; i < 2; i++) {
			quartal = 0;
			genNewOrders();
		}
		quartal = 1; // stellt sicher, dass biede Durchläufe in Q0 und zu Ende
						// auf quartal auf 1 steht.
		sortOrderList(); // Liste nach Qty Höhe sortieren
	}

	public void ordersForNewRound() {
		removeInvalidOrders();
		genNewOrders();
		sortOrderList();
	}

	public void genNewOrders() { // Generiert neue Bestellungen für Quartal
		int qtyQuartal = 0; // Gesamt Stückzahl für Quartal
		double sign = Math.random() - 0.5; // 50% tige Vorzeichen + oder -
		int rndFact = (int) (Math.random() * 70); // 70% schwankende Nachfrage.
		int qtyMax = (int) (100 + Math.copySign(rndFact, sign));
		/*
		 * copySign nimmt das Vorzeichen von Sign und fügt es mit rndFaktor Es
		 * wird also mit 50:50 der rndFact von den normlMaß 100 addiert und
		 * subtrahiert
		 */

		do {
			int qty = 1 + (int) (Math.random() * 35); // Ermittelt eine
														// Stückzahl zwischen 1
														// und 71 stk. Stat.
														// Mittel => 35,5
			qtyQuartal += qty;
			orderList.add(new Order(qty, quartal));
			System.out.println(qty);

		} while (qtyQuartal < qtyMax);

		quartal++;
	}

	public void addOneOrderToPool(Order order){
		orderList.add(order);
		sortOrderList();
	}
	public Order getBestOrder(){
		Order order = orderList.get(0);
		orderList.remove(order);
		return order;
	}
	
	private void removeInvalidOrders() {

		// Löscht alle Objekte, deren validTo Date abgelaufen ist.
		for (Order order : orderList) {
			if (order.getQuartalValidTo() < quartal) {
				orderList.remove(order);
			}
		}

	}

	private void sortOrderList() {
		ArrayList<Order> orderListSort = new ArrayList<Order>();

		Order tmp = null;
		while (orderList.size() > 0) {
			int orderQtyMax = 0;
			for (Order order : orderList) {
				if (order.getQuantity() > orderQtyMax) {
					orderQtyMax = order.getQuantity();
					tmp = order;
				}

			}
			orderListSort.add(tmp);
			orderList.remove(tmp);
		}
		orderList = orderListSort;

	}

}
