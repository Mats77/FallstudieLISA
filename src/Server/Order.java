package Server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;

public class Order {
	private static int orderCount = 0;
	private final int orderId;
	private int totalQuantity;
	private int quartalValidTo;
	private int fixedOrders;
	private int optionalOrders;
	private double pricePerAirplane;
	private int deliveryTimeinQuart;
	private String clientName;
	private int quantityLeft; // Aufträge können teilweise z.B. in Q1 und zum
								// andren Teil in Q1 erfüllt werden.

	// quantityLeft muss == 0 sein, damit ein Auftrag abgeschlossen wird.

	public Order(int totalQuantity, int quartal) {
		this.totalQuantity = totalQuantity;
		this.quantityLeft = totalQuantity;

		this.quartalValidTo = quartal + 1 + (int) (Math.random() * 2.2); // Aufträge
																			// sollen
																			// max.
																			// 3
																			// Quartale
																			// gültig
																			// sein.
		this.deliveryTimeinQuart = 1 + (int) (Math.random() * 4.1);
		orderCount++;
		this.orderId = orderCount;

		optionalOrders = (int) (totalQuantity / 4);
		fixedOrders = totalQuantity - optionalOrders;
		setClient();
	}

	private void setClient() {
		File file = new File("/Users/Christian/Desktop/airlines.txt"); // File
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
