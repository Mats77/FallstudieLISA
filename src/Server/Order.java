package Server;

public class Order {

	private int quantity;
	private int quartalValidTo;
	private int fixedOrders;
	private int optionalOrders;
	private int price = 300*10^6;
	private int deliveryTimeinQuart;


	public Order(int quantity, int quartal) {
		this.quantity = quantity;		
		this.quartalValidTo = quartal + 1 + (int) (Math.random() * 2.2); // Aufträge sollen max. 3 Quartale gültig sein.
		this.deliveryTimeinQuart = 1 + (int)(Math.random()*4.1); 
		
		optionalOrders = (int)(quantity/4);
		fixedOrders= quantity-optionalOrders;
		
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


}
