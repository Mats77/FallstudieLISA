package Server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class OrderPool {
	private ArrayList<Order> orderList = new ArrayList<Order>();
	private int quartal;


	public OrderPool() {
		// Zu Spielbeginn soll die Methode genNewOrders 2 mal durchgeführt
		// werden um für Runde 1 genügend Aufträge zu erstellen.
		for (int i = 0; i < 2; i++) {
			genNewOrders();
		}
		sortOrderList(); // Liste nach Qty Höhe sortieren
	}

	public void genOrdersForNewRound() {
		quartal = Mechanics.getQuartal(); // aktualisiert jede Runde das Quartal mit dem aktuellen Quartal 
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

	}

	public void addOneOrderToPool(Order order){ //Nimmt nicht-akzeptierte Orders von den Spielen zurück.
		orderList.add(order); // Mit Beginn einer neuen Runde wird geprüft ob die Order noch gültig ist und neu sortiert
	}

	public Order getBestOrder(){
		if(orderList.size()>0){ //Wenn alle Orders verteilt sind, wird null zurückgegeben.
		Order order = orderList.get(0);
		orderList.remove(order);
		return order;
		}
		else return null;
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
