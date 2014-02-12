package tests;

import static org.junit.Assert.*;

import java.net.Socket;
import java.util.Vector;
import java.util.concurrent.CopyOnWriteArrayList;

import org.junit.Before;
import org.junit.Test;

import Server.Conn;
import Server.Handler;
import Server.Market;
import Server.Mechanics;
import Server.Order;
import Server.OrderPool;
import Server.Player;
import Server.PlayerData;
import Server.PlayerDataCalculator;
import Server.PlayerOrderPool;

public class SimulatingWholeRounds {
	Handler handler = null;
	Mechanics mechanics = null;
	PlayerDataCalculator pdc = null;
	Vector<Conn> connections= null;
	Market market = null;
	OrderPool orderPool = null;
	Player [] player = new Player[4];
	
	@Before
	public void testGeneratePlayers() {
		handler = new Handler(3);
		mechanics = handler.getMechanics();
		pdc = new PlayerDataCalculator(mechanics);
		connections = new Vector<Conn>();
		market = new Market();
		orderPool = market.getOrderPool();
		
		connections.add(new Conn(new Socket(), handler));
		connections.get(0).setNick("Mats1");
		connections.get(0).setId(0);
		connections.add(new Conn(new Socket(), handler));
		connections.get(1).setId(1);
		connections.get(1).setNick("Mats2");
		connections.add(new Conn(new Socket(), handler));
		connections.get(2).setId(2);
		connections.get(2).setNick("Mats3");
		connections.add(new Conn(new Socket(), handler));
		connections.get(3).setId(3);
		connections.get(3).setNick("Mats4");
		
		handler.setConnections(connections);
		mechanics.startGame(connections);
		player= mechanics.getPlayers();
		
	}
	
	
	//Testet ob alle Werte korrekt eingetragen sind und sich die ArrayListen im OrderPool richtig
	//verschieben am Ende einer Runde.
	@Test
	public void testRoundOne(){
				
		//Init bei startGame Testen
		PlayerData data = player[0].getData().lastElement();
		PlayerOrderPool playerOrderPool = player[0].getPlayerOrderPool();
		
		assertEquals(0, playerOrderPool.getAcceptedOrders().size(), 0);
		assertEquals(0, playerOrderPool.getFinishedOrders().size(), 0);
		assertEquals(0, playerOrderPool.getToProduceNextRound().size(), 0);
		assertEquals(1, playerOrderPool.getOrdersToProduce().size(), 0);
		
		assertEquals(2600, data.getFixCosts(),0);
		assertEquals(2750, data.getVarCosts(),0);
		assertEquals(0, data.getCosts(),0);
		assertEquals(500, data.getResearch(),0);
		assertEquals(500, data.getMarketing(),0);
		assertEquals(13000, data.getProduction(),0);
		assertEquals(300, data.getPricePerAirplane(),0);
		assertEquals(7500, data.getTurnover(),0);
		assertEquals(650, data.getProfit(),0);
		assertEquals(26, data.getCapacity(),0);
		assertEquals(26, player[0].getCapacityLeft(),0);
		assertEquals(5650, data.getCash(), 1);
		assertEquals(45, player[0].getCompanyValue(), 1);
		assertEquals(25, data.getMarketshare(), 0.1);
		
		//Neue Orders generieren und dem User als neue Order vorschlagen
		clearAllLists();
		Order order[] = new Order[8];
		for (int i = 0; i < order.length; i++) {
			order[i]=new Order(10, 3, true);
			player[i%player.length].getPlayerOrderPool().addNewOrder(order[i]);
			order[i].setId(i);
		}
		
	
		
		//Jeder Spieler nimmt eine Order an.
		mechanics.acceptOrder(0, 0);
		mechanics.acceptOrder(1, 1);
		mechanics.acceptOrder(2, 2);
		mechanics.acceptOrder(3, 3);
		
		assertEquals(0, player[0].getPlayerOrderPool().getAcceptedOrders().get(0).getOrderId(), 0);
		assertEquals(1, player[1].getPlayerOrderPool().getAcceptedOrders().get(0).getOrderId(), 0);
		assertEquals(2, player[2].getPlayerOrderPool().getAcceptedOrders().get(0).getOrderId(), 0);
		assertEquals(3, player[3].getPlayerOrderPool().getAcceptedOrders().get(0).getOrderId(), 0);
		
		//Aufträge produzieren
		mechanics.produceOrder(0, 0);
		mechanics.produceOrder(1, 1);
		mechanics.produceOrder(2, 2);
		mechanics.produceOrder(3, 3);
		
		assertEquals(0, player[0].getPlayerOrderPool().getToProduceNextRound().get(0).getOrderId(), 0);
		assertEquals(1, player[1].getPlayerOrderPool().getToProduceNextRound().get(0).getOrderId(), 0);
		assertEquals(2, player[2].getPlayerOrderPool().getToProduceNextRound().get(0).getOrderId(), 0);
		assertEquals(3, player[3].getPlayerOrderPool().getToProduceNextRound().get(0).getOrderId(), 0);
		
		
		//Simulate Round 2
		mechanics.valuesInserted("2000;1000;1000;2;300", "Mats1");
		mechanics.valuesInserted("5000;100;100;1;200", "Mats2");
		mechanics.valuesInserted("100;100;100;1;500", "Mats3");
		mechanics.valuesInserted("100;100;100;1;3000", "Mats4");
		
		assertEquals(0, player[0].getPlayerOrderPool().getOrdersToProduce().get(0).getOrderId(), 0);
		assertEquals(1, player[1].getPlayerOrderPool().getOrdersToProduce().get(0).getOrderId(), 0);
		assertEquals(2, player[2].getPlayerOrderPool().getOrdersToProduce().get(0).getOrderId(), 0);
		assertEquals(3, player[3].getPlayerOrderPool().getOrdersToProduce().get(0).getOrderId(), 0);
		
		assertEquals(1, player[0].getPlayerOrderPool().getFinishedOrders().size(), 0);
		assertEquals(1, player[1].getPlayerOrderPool().getFinishedOrders().size(), 0);
		assertEquals(1, player[2].getPlayerOrderPool().getFinishedOrders().size(), 0);
		assertEquals(1, player[3].getPlayerOrderPool().getFinishedOrders().size(), 0);
		
		//Prüfen ob nicht angenommene Orders zurück in den OrderPool gelaufen sind und neu verteilt wurden:
		assertTrue("Nicht angenommene Orders wurden nicht erneut verteilt", searchForOrderInNewOrders(4));
		assertTrue("Nicht angenommene Orders wurden nicht erneut verteilt", searchForOrderInNewOrders(5));
		assertTrue("Nicht angenommene Orders wurden nicht erneut verteilt", searchForOrderInNewOrders(6));
		assertTrue("Nicht angenommene Orders wurden nicht erneut verteilt", searchForOrderInNewOrders(7));
		
		//Marketshare prüfen
		assertEquals(25, player[0].getData().lastElement().getMarketshare(), 0);
		assertEquals(25, player[1].getData().lastElement().getMarketshare(), 0);
		assertEquals(25, player[2].getData().lastElement().getMarketshare(), 0);
		assertEquals(25, player[3].getData().lastElement().getMarketshare(), 0);
		
		//Produktionsberechnung prüfen
		assertEquals(15000, player[0].getData().lastElement().getProduction(), 0);
		assertEquals(18000, player[1].getData().lastElement().getProduction(), 0);
		assertEquals(13100, player[2].getData().lastElement().getProduction(), 0);
		assertEquals(13100, player[3].getData().lastElement().getProduction(), 0);
		
		//Turnover Prüfen: Preis vermindet um fixen Rabatt = 300 bei 26 Stk = 300*0.85
		assertEquals(26*255, player[0].getData().lastElement().getTurnover(), 0);
		
		//Fix Kosten prüfen => Kapazität *100
		assertEquals(30*100, player[0].getData().lastElement().getFixCosts(), 0);
		assertEquals(36*100, player[1].getData().lastElement().getFixCosts(), 0);
		assertEquals(26*100, player[2].getData().lastElement().getFixCosts(), 0);
		assertEquals(26*100, player[3].getData().lastElement().getFixCosts(), 0);
		
		//Gesamtkosen prüfen => Fixe Kosten + varCOsts *Airplane + interests
		double kredit = Math.abs((5650 - 2000-1000-1000-120*26 - 30*100));
		double costsPlayer0 = 30*100 + 120*26 + (kredit*0.15)/4;
		assertEquals(costsPlayer0, player[0].getData().lastElement().getCosts(), 0.1);
		kredit = Math.abs((5650 - 5000-100-100-110*26 - 36*100));
		double costsPlayer1 = 36*100 + 110*26 + (kredit*0.15)/4;
		assertEquals(costsPlayer1, player[1].getData().lastElement().getCosts(), 0.1);
		kredit = Math.abs((5650 - 100-100-100-110*26 - 26*100));
		double costsPlayer2 = 26*100 + 110*26 + (kredit*0.15)/4;
		assertEquals(costsPlayer2, player[2].getData().lastElement().getCosts(), 0.1);
		double costsPlayer3 =costsPlayer2;
		assertEquals(costsPlayer3, player[3].getData().lastElement().getCosts(), 0.1);
		
		//Turnover berechnen (Bei allen Spielern gelich = da allge gleiche Aufträge angenommen)
		assertEquals(300*10*1.1, player[1].getData().lastElement().getTurnover(), 0);
	}
	
	@Test
	public void testRoundTwo(){

		
//		assertEquals(1, mechanics.getPlayers()[0].getPlayerOrderPool().getFinishedOrders().size(),0);
//		assertEquals(1, mechanics.getPlayers()[0].getPlayerOrderPool().getOrdersToProduce().size(),0);
//		assertEquals(0, mechanics.getPlayers()[0].getData().lastElement().getPlayerID(),0);
//		assertEquals(2600, mechanics.getPlayers()[0].getData().lastElement().getFixCosts(),0);
//		assertEquals(110, mechanics.getPlayers()[0].getData().lastElement().getVarCosts(),0);
//		assertEquals(5460, mechanics.getPlayers()[0].getData().lastElement().getCosts(),0);
//		assertEquals(100, mechanics.getPlayers()[0].getData().lastElement().getResearch(),0);
//		assertEquals(100, mechanics.getPlayers()[0].getData().lastElement().getMarketing(),0);
//		assertEquals(13200, mechanics.getPlayers()[0].getData().lastElement().getProduction(),0);
//		assertEquals(300, mechanics.getPlayers()[0].getData().lastElement().getPricePerAirplane(),0);
//		assertEquals(6630, mechanics.getPlayers()[0].getData().lastElement().getTurnover(),0);
//		assertEquals(770, mechanics.getPlayers()[0].getData().lastElement().getProfit(),0);
//		assertEquals(26, mechanics.getPlayers()[0].getCapacityLeft(),0);

	}
	
	@Test
	public void testingAcceptDeclineAndProduce()
	{
		Order order = new Order(5, 0);
		Order order4 = new Order(30,0);
		mechanics.getPlayers()[0].getPlayerOrderPool().getNewOrders().add(order);
		mechanics.getPlayers()[0].getPlayerOrderPool().getNewOrders().add(order4);
		mechanics.acceptOrder(0, order.getOrderId());
		mechanics.acceptOrder(0, order4.getOrderId());
		assertEquals(true, mechanics.getPlayers()[0].getPlayerOrderPool().getAcceptedOrders().contains(order));
		assertEquals(true, mechanics.getPlayers()[0].getPlayerOrderPool().getAcceptedOrders().contains(order4));
		
		mechanics.produceOrder(0, order.getOrderId());
		mechanics.produceOrder(0, order4.getOrderId());
		assertEquals(0, mechanics.getPlayers()[0].getCapacityLeft(),0);
		assertEquals(true, mechanics.getPlayers()[0].getPlayerOrderPool().getToProduceNextRound().contains(order));
		
		Order order2 = new Order(5, 0);
		mechanics.getPlayers()[0].getPlayerOrderPool().getNewOrders().add(order2);
		mechanics.declineOrder(0, order2.getOrderId());
		assertEquals(true, mechanics.getMarket().getOrderPool().getOrderList().contains(order2));
		
		Order order3 = new Order(5, 0);
		mechanics.getPlayers()[0].getPlayerOrderPool().getNewOrders().add(order3);
		mechanics.declineOrder(0, order3.getOrderId());
		assertEquals(true, mechanics.getMarket().getOrderPool().getOrderList().contains(order3));
		
		mechanics.valuesInserted("100;100;100;1;300", "Mats1");
		mechanics.valuesInserted("100;100;100;1;300", "Mats2");
		mechanics.valuesInserted("100;100;100;1;300", "Mats3");
		mechanics.valuesInserted("100;100;100;1;300", "Mats4");
		
		assertEquals(true, mechanics.getPlayers()[0].getPlayerOrderPool().getOrdersToProduce().contains(order));
		assertEquals(true, mechanics.getPlayers()[0].getPlayerOrderPool().getOrdersToProduce().contains(order4));
		
		mechanics.valuesInserted("100;100;100;1;300", "Mats1");
		mechanics.valuesInserted("100;100;100;1;300", "Mats2");
		mechanics.valuesInserted("100;100;100;1;300", "Mats3");
		mechanics.valuesInserted("100;100;100;1;300", "Mats4");
		
		assertEquals(true, mechanics.getPlayers()[0].getPlayerOrderPool().getFinishedOrders().contains(order));
		assertEquals(true, mechanics.getPlayers()[0].getPlayerOrderPool().getAcceptedOrders().contains(order4));
		assertEquals(9, order4.getQuantityLeft(),0);
	}

	private void clearsAcceptedOrderListOfAllPlayer(){
		for (Player i : player) {
			i.getPlayerOrderPool().getAcceptedOrders().clear();
		}
	}
	
	private void clearsNewOrderListOfAllPlayer(){
		for (Player i : player) {
			i.getPlayerOrderPool().getNewOrders().clear();
		}
	}
	
	private void clearsToProduceNextRoundOfAllPlayer(){
		for (Player i : player) {
			i.getPlayerOrderPool().getToProduceNextRound().clear();
		}
	}
	private void clearsOrdersToProduceOfAllPlayer(){
		for (Player i : player) {
			i.getPlayerOrderPool().getOrdersToProduce().clear();
		}
	}
	private void clearsOrderPool(){
			market.getOrderPool().getOrderList().clear();		
	}
	
	private void clearAllLists(){
		clearsAcceptedOrderListOfAllPlayer();
		clearsNewOrderListOfAllPlayer();
		clearsOrderPool();
		clearsToProduceNextRoundOfAllPlayer();
	}
	
	private boolean searchForOrderInNewOrders(int id){
		
		for (Player i : player) {
			CopyOnWriteArrayList<Order> newOrders = i.getPlayerOrderPool().getNewOrders();
			for (Order order : newOrders) {
				if(order.getOrderId()== id){
					return true;
				}
			}
			
		}
		return false;
	}
}
