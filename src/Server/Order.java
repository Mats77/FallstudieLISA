package Server;


public class Order {
	private final String [] AIRLINENAMES = {"China Eastern Airlines", "Lufthansa" , "United Airlines",	"Delta Air Lines",
			"Southwest Airlines", "American Airlines", "US Airways", "Ryanair", "China Southern Airlines", "Air China"};
	private static int orderCount = 0;
	private int orderId; //Eigentlich final, wegen Tests kein final
	private int totalQuantity; // bis zu 1/4 der totalQuantity sind optionale Orders, der Rest sind fixedOrders
	private int fixedQuantity=0;
	private int optionalQuantity=0;
	private int quartalValidTo;
	private double pricePerAirplane;
	private String clientName;
	private boolean useOptinalOrders=false;
	private int quantityLeft; // Aufträge können teilweise z.B. in Q1 und zum
								// andren Teil in Q1 erfüllt werden.
	private int status = 0;  //0: inProduction
							 //1: inQueue
							 //2: accepted
							 //3: delayed
	// quantityLeft muss == 0 sein, damit ein Auftrag abgeschlossen wird.

	public Order(int totalQuantity, int quartal) {
		this.quartalValidTo = quartal + 1 + (int) (Math.random() * 2.1); // Aufträge
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
		fixedQuantity = totalQuantity-optionalQuantity;
		
		if(Math.random()<=0.3){
			useOptinalOrders = true;
		}
		
		if(useOptinalOrders){
			this.totalQuantity = totalQuantity;
			this.quantityLeft = totalQuantity;
		}else{
			this.totalQuantity = totalQuantity-optOrders;
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
		fixedQuantity = totalQuantity-optionalQuantity;
		
		useOptinalOrders = true;
		
		
		if(useOptinalOrders){
			this.totalQuantity = totalQuantity;
			this.quantityLeft = totalQuantity;
		}else{
			this.totalQuantity = totalQuantity-optOrders;
			this.quantityLeft = totalQuantity;
		}
		setClient();
	}
	
	private void setClient() {	
		int rnd = (int) (Math.random() * AIRLINENAMES.length);
		clientName = AIRLINENAMES[rnd];
	}

	public int getQuartalValidTo() {
		return quartalValidTo;
	}
	
	public int getFixedQuantity(){
		return fixedQuantity;
	}

	public int getTotalQuantity() {
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

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
	
	public void setId(int orderId){
		this.orderId = orderId;
	}
}
