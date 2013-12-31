package Server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;

public class Order {

	private int quantity;
	private int quartalValidTo;
	private int fixedOrders;
	private int optionalOrders;
	private int price = 300;
	private int deliveryTimeinQuart;
	private String  clientName;


	public Order(int quantity, int quartal) {
		this.quantity = quantity;		
		this.quartalValidTo = quartal + 1 + (int) (Math.random() * 2.2); // Aufträge sollen max. 3 Quartale gültig sein.
		this.deliveryTimeinQuart = 1 + (int)(Math.random()*4.1); 
		
		optionalOrders = (int)(quantity/4);
		fixedOrders= quantity-optionalOrders;
		setClient();
		calcPrice();
	}

	private void calcPrice() { //Preis mit Preisstaffeln neu berechnen
		
		switch (quantity) {
		case 10:
			price = (int) (price*0.9);
			break;
		case 20:
			price = (int) (price*0.8);
			break;
		case 30:
			price = (int) (price*0.75);
			break;
		}

	}
	
	private void setClient(){
		File file = new File("/Users/Christian/Desktop/airlines.txt"); // File mit den top 10 Airline Name 
		try {
			BufferedReader reader= new BufferedReader(new FileReader(file));
			int rnd =(int) (Math.random()*10);
			for (int i = 0; i < rnd; i++) {
				reader.readLine();
			}
			clientName= reader.readLine();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}
	
	public int getQuartalValidTo(){
		return quartalValidTo;
	}

	public int getQuantity() {
		return quantity;
	}
	
	
}
