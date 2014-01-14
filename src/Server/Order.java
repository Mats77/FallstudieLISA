package Server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;

public class Order {
	private static int orderCount = 0;
	private final int orderId;
	private int totalQuantity;
	private int optionalQuantity=0;
	private int quartalValidTo;
	private double pricePerAirplane;
	private String clientName;
	private boolean useOptinalOrders=false;
	private int quantityLeft; // Aufträge können teilweise z.B. in Q1 und zum
								// andren Teil in Q1 erfüllt werden.

	// quantityLeft muss == 0 sein, damit ein Auftrag abgeschlossen wird.

	public Order(int totalQuantity, int quartal) {
		this.totalQuantity = totalQuantity;
		this.quartalValidTo = quartal + 1 + (int) (Math.random() * 3.1); // Aufträge
																			// sollen
																			// max.
																			// 4
																			// Quartale
																			// gültig
																			// sein.
		orderCount++;
		this.orderId = orderCount;

		//Es gilt immer bis zu 1/4 der Orders als optionale Bestellungen.
		// Bei 30% der Bestellungen kommt eine optionale Bestellung hinzu!
		
		//Wird schon direkt berechnet, um dem User anzuzeigen.
		int optOrders =(int) (Math.random()*(totalQuantity / 4));
		optionalQuantity = (int) (optOrders);
		
		if(Math.random()<=0.3){
			useOptinalOrders = true;
		}
		
		if(useOptinalOrders){
			this.quantityLeft = totalQuantity + optionalQuantity;
		}else{
			this.quantityLeft = totalQuantity;
		}
		
		
		setClient();
	}

	public Order(int totalQuantity, int quartal, boolean test){
		this.totalQuantity = totalQuantity;
		this.quartalValidTo = quartal + 2;
		orderCount++;
		this.orderId = orderCount;

		
		int optOrders = (int) (totalQuantity / 4);
		optionalQuantity = (int) (optOrders);
		
		useOptinalOrders = true;
		
		
		if(useOptinalOrders){
			this.quantityLeft = totalQuantity + optionalQuantity;
		}else{
			this.quantityLeft = totalQuantity;
		}
		
		
		setClient();
	}
	
	private void setClient() {
		File file = new File("airlines.txt"); // File
																		// mit
																		// den
																		// top
																		// 10
																		// Airline
																		// Name
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			int rnd = (int) (Math.random() * 10);
			for (int i = 0; i < rnd; i++) {
				reader.readLine();
			}
			clientName = reader.readLine();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public int getQuartalValidTo() {
		return quartalValidTo;
	}
	
	

	public int getQuantity() {
		return totalQuantity;
	}
	
	public int getOptionalQuantity() {
		return optionalQuantity;
	}

	public int getQuantityLeft() {
		return quantityLeft;
	}

	public void setQuantityLeft(int quantityLeft) {
		this.quantityLeft = quantityLeft;
	}

	public String getClientName() {
		return clientName;
	}

	public int getOrderId() {
		return orderId;
	}

	// Wird aufgerufen vom PlayerOrderPool bei der Annhame eines Auftrags und
	// setzt den Preis auf von User zuletzt festgelegten Preis.
	public void setPrice(double pricePerAirplane) {

		// Natürlicher Rabatt bzw. Aufschlag für  große und kleine Aufträge, den jeder Spieler gleich hat.
		if (totalQuantity <= 10) {
			this.pricePerAirplane = pricePerAirplane * 1.1;
		} else if (totalQuantity >= 25) {
			this.pricePerAirplane = pricePerAirplane * 0.85;
		} else {
			this.pricePerAirplane = pricePerAirplane;
		}
	}

	public double getPricePerAirplane() {
		return pricePerAirplane;
	}

}
