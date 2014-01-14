package Server;

import java.util.Arrays;
import java.util.Collections;

public class Market {

	private OrderPool orderPool = new OrderPool();

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
		double totalValuePlayer = 0;
		double playerValue[] = new double[player.length];

		for (int i = 0; i < player.length; i++) { // Liest die Company Values
													// von allen Playern ein und
													// sortiert diese
			playerValue[i] = player[i].getCompanyValue();
			totalValuePlayer += player[i].getCompanyValue();
		}
		Arrays.sort(playerValue); // Sortiert das Array aufsteigend.

		// Sortiert das Array nach Größe absteigend.
		double playerValueDescending[] = new double[playerValue.length];
		for (int i = 0; i < playerValue.length; i++) {
			playerValueDescending[i] = playerValue[playerValue.length - i - 1];
		}

		// Sortiert die Player nach ihrer CompanyValue absteigend.
		Player playerOrdered[] = new Player[player.length];
		boolean playerUsed[] = new boolean [player.length]; // Sagt aus, ob ein Spieler schon sortiert wurde, um bei gleichen Werten nicht einen Spieler mehrfach zuzuweisen.
		for (int i = 0; i < player.length; i++) {
			for (int j = 0; j < player.length; j++) {
					if (playerValueDescending[i] == player[j].getCompanyValue() && !playerUsed[j]) {
						playerOrdered[i] = player[j];
						playerUsed[j] = true;
						break;
				}
			}
		}

		// Verteilt die Orders nach Rangreihenfolge, bis alle aufgeteilt sind.
		int playerCount = 0;
		Order order = orderPool.getBestOrder();
		while (true) {
			if (order != null) {

				// Proz anteil der PlayerValue von der aktuellen gesamten Player
				// Value aller Spieler
				double anteilVonGesPlayerValue = playerOrdered[playerCount
						% playerOrdered.length].getCompanyValue()
						/ totalValuePlayer;
				anteilVonGesPlayerValue *= playerOrdered.length; // multipliziert
				// den
				// Anteil
				// mit der
				// Spielerzahl
				// um bei 4
				// Spielen
				// und 25%
				// anteil
				// eine
				// 100%tige
				// Zuteilung
				// zu
				// erhalten.

				if (Math.random() >= (1 - anteilVonGesPlayerValue)) {
					playerOrdered[playerCount % playerOrdered.length]
							.addNewOrder(order); // Verteilung der Orders nach
													// Rangreihenfolge
					order = orderPool.getBestOrder();
				}
			} else
				break; // Verteilung abbrechen, wenn der OrderPool leer ist.
			playerCount++;
		}
	}

	private void calcDev() {

	}

	private void calcMarketing() {

	}

	// Für JUnit Test
	public OrderPool getOrderPool() {
		return orderPool;
	}

}
