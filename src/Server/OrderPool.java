package Server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CopyOnWriteArrayList;

public class OrderPool {
	private CopyOnWriteArrayList<Order> orderList = new CopyOnWriteArrayList<Order>();
	private int quartal;

	public void genOrdersForNewRound() {
		quartal = Mechanics.getQuartal(); // aktualisiert jede Runde das Quartal mit dem aktuellen Quartal 
		removeInvalidOrders();
		genNewOrders();
		// Zu Spielbeginn soll die Methode genNewOrders 2 mal durchgeführt
		// werden um für Runde 1 genügend Aufträge zu erstellen. Beim initialisiern wird also genNewOrders aufgerufen und beim Spielstart erneut
		if(quartal==0){
			genNewOrders();
		}
		
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
			int qty = 1 + (int) (Math.random() * 20); // Ermittelt eine
														// Stückzahl zwischen 1
														// und 21 stk. Stat.
														
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
	//eigentlich private aber aus Testzwecken public
	public void removeInvalidOrders() {

		// Löscht alle Objekte, deren validTo Date abgelaufen ist.
		for (Order order : orderList) {
			if (order.getQuartalValidTo() < quartal) {
				orderList.remove(order);
			}
		}

	}

	private void sortOrderList() { 
		CopyOnWriteArrayList<Order> orderListSort = new CopyOnWriteArrayList<Order>();

		Order tmp = null;
		while (orderList.size() > 0) {
			int orderQtyMax = 0;
			for (Order order : orderList) {
				if (order.getTotalQuantity() > orderQtyMax) {
					orderQtyMax = order.getTotalQuantity();
					tmp = order;
				}

			}
			orderListSort.add(tmp);
			orderList.remove(tmp);
		}
		orderList = orderListSort;

	}
	
	 // Nur für Testzwecke um zu beeinflussen, welche Ordes erstellt werden.
	public void delAllOrders(){
		 orderList = new CopyOnWriteArrayList<Order>();
	}

	public CopyOnWriteArrayList<Order> getOrderList() {
		return orderList;
	}
	
	

}
