package Server;

import java.util.Vector;

public class Player {
	private String nick;
	private int id;
	private Vector<PlayerData> data = new Vector<PlayerData>();
	private boolean readyForNextRound = false;
	private int[] tmpValues;
	private PlayerDataCalculator playerDataCalculator;
	private Mechanics mechanics;
	private Vector<LongTimeCredit> credits = new Vector<LongTimeCredit>();
	private ShortTimeCredit shortTimeCredit = null;
	private double companyValue;
	private PlayerOrderPool orderPool = new PlayerOrderPool(this);
	private double debtCapital;
	private double cash;
	private int capacityLeft;
	private int reliability=0;	//Pro Quartal 1
	
	//Konstruktor
	public Player(long id, String name, PlayerDataCalculator pdc, Mechanics m) {
		this.playerDataCalculator = pdc;
		this.id = (int)id;
		this.nick = name;
		data.add(new PlayerData((int)id, 0
				, 5000, 25, 500, 500, 500, 7500, 25, 300));	//5000 ist Startbetrag
		mechanics = m;
		this.cash = data.lastElement().getCash();
		
		//jeder beginnt mit einem Auftrag, der die komplette Kapazität ausschöpft, 
		//da in der ersten Runde nur Aufträge für die Zweite 
		//Runde angenommen werden können.
		orderPool.getOrdersToProduce().add(new Order(26, 1));
	}

	
	public void saveNextRoundValues(String values, int quartal) {	//String: Produktion;Marketing;Entwicklung;Materialstufe;Preis
		readyForNextRound = true;
		int[] insertedValues = new int[values.split(";").length];
		for(int i=0; i< insertedValues.length; i++)
		{
			insertedValues[i]=Integer.parseInt(values.split(";")[i]);
		}
		tmpValues = insertedValues;
		int tmpProduction = (int)data.lastElement().getProduction()+tmpValues[0];	//Produktion ist fortlaufend
		data.add(new PlayerData(id, tmpProduction, insertedValues[1], insertedValues[2], mechanics.getQuartal()));
		spendMoney(insertedValues[0] + insertedValues[1] + insertedValues[2]);
		//wird initialisiert mit dem Cash, das am Anfang der runde zur Verfügung stand.
	}
	
	//Getter und Setter

	public Vector<PlayerData> getData() {
		return data;
	}


	public String getNick() {
		return nick;
	}

	public void setNick(String nick) {
		this.nick = nick;
	}
	
	public void setReadyForNextRound(boolean readyForNextRound) {
		this.readyForNextRound = readyForNextRound;
	}
	
	public boolean isReadyForNextRound() {
		return readyForNextRound;
	}
	
//	public void setReadyForOrderSelection(boolean readyForOrderSelection) {
//		this.readyForOrderSelection = readyForOrderSelection;
//	}
//	
//	public boolean isReadyForOrderSelection() {
//		return readyForOrderSelection;
//	}
	
	public int getId()
	{
		return id;
	}

	public void setCompanyValue(double d) {
		companyValue = d;
	}
	
	public void addNewOrder(Order order){
		orderPool.addNewOrder(order);
	}
	
	public double getCompanyValue(){
		return companyValue;
	}
	
	public void newOrdersToProduce(int[] orderId){
		orderPool.newOrdersToProduce(orderId);
	}
	
	public void newOrdersAccepted(int [] orderId){
		orderPool.newOrdersAccepted(orderId);
	}
	
	public void addCash(double amount)
	{
		if(shortTimeCredit != null)
		{
			double tmp = shortTimeCredit.payBackShortTimeCredit(amount);
			if(tmp > 0)
			{
				cash += tmp;
				shortTimeCredit = null;
			}
		} else {
			cash += amount;
		}
	}
	
	public void setCapacityLeft(int capacity){
		this.capacityLeft=capacity;
	}
	
	public int getCapacityLeft(){
		return capacityLeft;
	}
	
	public void setReliability(int value){
		reliability = value;
	}
	
	public int getReliability(){
		return reliability;
	}
	
	public void spendMoney(double amount)
	{
		this.cash -= amount;
		if(this.cash < 0)
		{
			if(shortTimeCredit != null)
			{
				shortTimeCredit.addAmount(amount);
			} else {
				shortTimeCredit = mechanics.getBank().getShortTimeCredit(-cash, this);
			}
		}
	}
	
	public double getCash()
	{
		return this.cash;
	}

	public Vector<LongTimeCredit> getCredits() {
		return credits;
	}	
	
	public void paybackCredit(Credit credit)		//TODO ganz wichtig 
	{
		reduceDeptCapital(credit.getAmount());
		cash -= credit.getAmount();
		this.credits.remove(credit);
	}

	public void reduceDeptCapital(double amount){
		this.debtCapital -= amount;
	}

	public void addDebtCapital(double amount) {
		this.debtCapital += amount;
		this.cash +=amount;
	}
	
	public void addAmountOfShortTimeCredit(double amount)
	{
		this.debtCapital += amount;
	}

	public PlayerOrderPool getPlayerOrderPool(){
		return orderPool;
	}


	public double getDebtCapital() {
		return debtCapital;
	}
	
	public ShortTimeCredit getShortTimeCredit(){
		return this.shortTimeCredit;
	}
	
	public boolean produceOrder(int orderID){
		for (Order order : orderPool.getAcceptedOrders()) {
			if(order.getOrderId()==orderID){
				if(this.capacityLeft > order.getQuantityLeft()){
					orderPool.produceOrder(orderID);
					this.capacityLeft -= order.getQuantityLeft();
					return true;
				} else {
					orderPool.produceOrder(orderID);
					this.capacityLeft = 0;
					return false;
				}
			}
		}
		return false;
	}


	public void insertNewTurnover(double turnover) {
		data.get(data.size()-1).setTurnover(turnover);
	}
}
