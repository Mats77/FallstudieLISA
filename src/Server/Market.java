package Server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Vector;

import org.junit.internal.runners.model.EachTestNotifier;

public class Market {


	private OrderPool orderPool = new OrderPool();
	private int totalTurnover = 30000; //Init mit 30.000 weil der Init Wert pro Player 75000 ist.
 
	public void calcMarketSharePerPlayer(Player [] players){
		double totalTurnover = 0;
		for (Player player : players) {
			totalTurnover += player.getData().lastElement().getTurnover();
		}
		for (Player player : players) {
			double value = player.getData().lastElement().getTurnover() / totalTurnover;
			player.getData().lastElement().setMarketshare(value);
		}
	}
	
	
	public void calcTotalTurnover(Player [] players) {
		
		for (Player player : players) {
			Vector<PlayerData> data = player.getData();
				totalTurnover += data.lastElement().getTurnover();
		}
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
		boolean playerUsed[] = new boolean[player.length]; // Sagt aus, ob ein
															// Spieler schon
															// sortiert wurde,
															// um bei gleichen
															// Werten nicht
															// einen Spieler
															// mehrfach
															// zuzuweisen.
		for (int i = 0; i < player.length; i++) {
			for (int j = 0; j < player.length; j++) {
				if (playerValueDescending[i] == player[j].getCompanyValue()
						&& !playerUsed[j]) {
					playerOrdered[i] = player[j];
					playerUsed[j] = true;
					break;
				}
			}
		}

		// Verteilt die Orders nach Rangreihenfolge, bis alle aufgeteilt sind.
		int playerCount = 0;
		Order order = orderPool.getBestOrder();
		
		//Median Berechnen
		double costList [] = new double[playerOrdered.length];
		for (int i = 0; i < playerOrdered.length; i++) {
			costList[i]= playerOrdered[i].getData().lastElement().getPricePerAirplane();
		}
		
		double median = getMedian(costList);
		
		// Schaut, ob der Flugzeugpreis von manchen Spielern 1.8mal so hoch wie der Median aller Flugzeugpreise liegt.
		boolean playerPriceTooHigh [] = new boolean[playerOrdered.length];
		for (int i = 0; i < playerOrdered.length; i++) {
			if(playerOrdered[i].getData().lastElement().getPricePerAirplane()/median>1.8){
				playerPriceTooHigh[i]=true;
			}
		}
		
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
				

				
				//Nur unter der Wahrscheinlichkeit der proz Company Value und wenn der Flugzeigpreis nicht 1.8mal höher ist als der Median
				if (Math.random() >= (1 - anteilVonGesPlayerValue) && !playerPriceTooHigh[playerCount % playerOrdered.length]) {
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


	public double getMedian(double[] numberList) {
	  Arrays.sort(numberList);
	  
	  //Gerade anzahl des Arrays
	  if(numberList.length%2==0){
		 return ((numberList[(numberList.length/2-1)]+numberList[(numberList.length/2)])/2);
	  }
	//Ungerade anzahl des Arrays
	  else{
		  return numberList[(int)(numberList.length/2)];
	  }

	  
	}
	
	private void calcDev() {

	}

	private void calcMarketing() {

	}
	
	public int getTotalTurnover(){
		return totalTurnover;
	}

	// Für JUnit Test
	public OrderPool getOrderPool() {
		return orderPool;
	}

}
