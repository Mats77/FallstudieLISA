package Server;

import java.util.Arrays;
import java.util.Collections;

public class Market {

	private OrderPool orderPool =  new OrderPool();



	public void calcMarketShare() {
		calcDev();
		calcMarketing();
		// Zusätzlich Zugriff auf GameHistory notwendig
	}

	public void genOrdersForNewRound() {
		orderPool.genOrdersForNewRound();
	}

	public void splitOrders(Player[] player) { // Sortiert die Spieler nach der
												// Höhe ihrer companyValue und
												// verteilt dann nacheinander
												// die Orders, bis alle verteilt
												// sind
		double playerValue[] = new double[player.length];
		for (int i = 0; i < player.length; i++) { // Liest die Company Values
													// von allen Playern ein und
													// sortiert diese
			playerValue[i] = player[i].getCompanyValue();
		}
		Arrays.sort(playerValue); // Sortiert das Array aufsteigend.

		// Sortiert das Array nach Größe absteigend.
		double playerValueDescending[] = new double[playerValue.length];
		for (int i = 0; i < playerValue.length; i++) {
			playerValueDescending[i] = playerValue[playerValue.length - i-1];
		}
		
		// Sortiert die Player nach ihrer CompanyValue absteigend.
		Player playerOrdered[] = new Player[player.length];
		for (int i = 0; i < player.length; i++) {
			for (int j = 0; j < player.length; j++) {
				if(playerValueDescending[i]==player[j].getCompanyValue()){
					playerOrdered[i] = player[j];
				}
			}
		}
		
		//Verteilt die Orders nach Rangreihenfolge, bis alle aufgeteilt sind.
		int playerCount = 0;
		Order order;
		while (true){
			order= orderPool.getBestOrder();
			if (order!=null) {
				playerOrdered[playerCount%4].addNewOrder(order); //Verteilung der Orders in nach Rangreihenfolge 
			}
			else break; //Verteilung abbrechen, wenn der OrderPool leer ist.
			playerCount++;
		}
	}

	private void calcDev() {

	}

	private void calcMarketing() {

	}
	
	

}
