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
		market =  mechanics.getMarket();
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
		mechanics.acceptOrder(0, 4);
		mechanics.acceptOrder(1, 1);
		mechanics.acceptOrder(2, 2);
		mechanics.acceptOrder(3, 3);
		
		assertEquals(0, player[0].getPlayerOrderPool().getAcceptedOrders().get(0).getOrderId(), 0);
		assertEquals(1, player[1].getPlayerOrderPool().getAcceptedOrders().get(0).getOrderId(), 0);
		assertEquals(2, player[2].getPlayerOrderPool().getAcceptedOrders().get(0).getOrderId(), 0);
		assertEquals(3, player[3].getPlayerOrderPool().getAcceptedOrders().get(0).getOrderId(), 0);
		
		//Aufträge produzieren
		mechanics.produceOrder(0, 0);
		mechanics.produceOrder(0, 4);
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
		assertEquals(4, player[0].getPlayerOrderPool().getOrdersToProduce().get(1).getOrderId(), 0);
		assertEquals(1, player[1].getPlayerOrderPool().getOrdersToProduce().get(0).getOrderId(), 0);
		assertEquals(2, player[2].getPlayerOrderPool().getOrdersToProduce().get(0).getOrderId(), 0);
		assertEquals(3, player[3].getPlayerOrderPool().getOrdersToProduce().get(0).getOrderId(), 0);
		
		assertEquals(1, player[0].getPlayerOrderPool().getFinishedOrders().size(), 0);
		assertEquals(1, player[1].getPlayerOrderPool().getFinishedOrders().size(), 0);
		assertEquals(1, player[2].getPlayerOrderPool().getFinishedOrders().size(), 0);
		assertEquals(1, player[3].getPlayerOrderPool().getFinishedOrders().size(), 0);
		
		//Prüfen ob Player4 trotz überhöhte Preis Aufträge erhalen hat:
		assertTrue(player[0].getPlayerOrderPool().getNewOrders().size()>3);
		assertEquals("Spieler 4 hat trotz überteurtem Preis neue Orders zugeteilt bekommen", 0, player[3].getPlayerOrderPool().getNewOrders().size(), 0);
		
		//Prüfen ob nicht angenommene Orders zurück in den OrderPool gelaufen sind und neu verteilt wurden:
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
		double kredit0 = Math.abs((5650 - 2000-1000-1000-120*26 - 30*100));
		double costsPlayer0 = 30*100 + 120*26 + (kredit0*0.15)/4;
		assertEquals(costsPlayer0, player[0].getData().lastElement().getCosts(), 0.1);
		double kredit1 = Math.abs((5650 - 5000-100-100-110*26 - 36*100));
		double costsPlayer1 = 36*100 + 110*26 + (kredit1*0.15)/4;
		assertEquals(costsPlayer1, player[1].getData().lastElement().getCosts(), 0.1);
		double kredit2 = Math.abs((5650 - 100-100-100-110*26 - 26*100));
		double costsPlayer2 = 26*100 + 110*26 + (kredit2*0.15)/4;
		assertEquals(costsPlayer2, player[2].getData().lastElement().getCosts(), 0.1);
		double costsPlayer3 =costsPlayer2;
		assertEquals(costsPlayer3, player[3].getData().lastElement().getCosts(), 0.1);
		
		//Turnover berechnen (Bei allen Spielern gelich = da allge gleiche Aufträge zu Beginn haben!)
		double turnover = 300*26*0.85;
		assertEquals(turnover, player[0].getData().lastElement().getTurnover(), 0);

		
		//Profit berechnen
		double profit0 = turnover - costsPlayer0 - 2000-1000-1000;
		assertEquals(profit0, player[0].getData().lastElement().getProfit(), 0);
		double profit1 = turnover - costsPlayer1 - 5000-100-100;
		assertEquals(profit1, player[1].getData().lastElement().getProfit(), 0);
		double profit2 = turnover - costsPlayer2 - 100-100-100;
		assertEquals(profit2, player[2].getData().lastElement().getProfit(), 0);
		double profit3 = turnover - costsPlayer3 - 100-100-100;
		assertEquals(profit3, player[3].getData().lastElement().getProfit(), 0);
		
		// + 30000 => Turnover aus dem Vorjahr
		double totalTurnover = turnover*4 + 30000;
		assertEquals(totalTurnover, market.getTotalTurnover(), 0);
		
		assertEquals(25, player[0].getData().lastElement().getMarketshare(), 0);
		
		//Cash prüfen => cash = Kredit tilgen + Kreditzinsen zurückzahlen 
		double cash0 = turnover - kredit0 - (kredit0*0.15)/4;
		assertEquals(cash0, player[0].getCash(), 0);
		double cash1 = turnover - kredit1 - (kredit1*0.15)/4;
		assertEquals(cash1, player[1].getCash(), 0);
		double cash2 = turnover - kredit2 - (kredit2*0.15)/4;
		assertEquals(cash2, player[2].getCash(), 0);
		assertEquals(cash2, player[3].getCash(), 0);
		
		//Runde 3
		
		//Neue Orders generieren und dem Spielre zuweisen
		Order orderRound3 [] = new Order[8];
		for (int i = 0; i < order.length; i++) {
			orderRound3[i] = new Order(10, 4, true);
			orderRound3[i].setId(300+i);
			player[i%4].addNewOrder(orderRound3[i]);
		}
		
		//Neue Orders akzeptieren:
		mechanics.acceptOrder(0, 300);
		mechanics.acceptOrder(1, 301);
		mechanics.acceptOrder(1, 305);
		mechanics.acceptOrder(2, 302);
		mechanics.acceptOrder(3, 303);
		
		mechanics.produceOrder(1, 301);
		mechanics.produceOrder(1, 305);
		mechanics.produceOrder(2, 302);
				
		
		mechanics.valuesInserted("2000;1000;1000;2;250", "Mats1");
		mechanics.valuesInserted("5000;100;100;1;200", "Mats2");
		mechanics.valuesInserted("100;100;100;1;500", "Mats3");
		mechanics.valuesInserted("10000;100;100;1;300", "Mats4");
		
		//Prüfen ob nicht angenommene Orders zurück in den OrderPool gelaufen sind und neu verteilt wurden:
		assertTrue("Nicht angenommene Orders wurden nicht erneut verteilt", searchForOrderInNewOrders(304));
		assertTrue("Nicht angenommene Orders wurden nicht erneut verteilt", searchForOrderInNewOrders(306));
		assertTrue("Nicht angenommene Orders wurden nicht erneut verteilt", searchForOrderInNewOrders(307));
		
		assertEquals(0, player[0].getPlayerOrderPool().getOrdersToProduce().size(), 0);
		assertEquals(301, player[1].getPlayerOrderPool().getOrdersToProduce().get(0).getOrderId(), 0);
		assertEquals(305, player[1].getPlayerOrderPool().getOrdersToProduce().get(1).getOrderId(), 0);
		assertEquals(302, player[2].getPlayerOrderPool().getOrdersToProduce().get(0).getOrderId(), 0);
		assertEquals(0, player[3].getPlayerOrderPool().getOrdersToProduce().size(), 0);
		
		assertEquals(3, player[0].getPlayerOrderPool().getFinishedOrders().size(), 0);
		assertEquals(2, player[1].getPlayerOrderPool().getFinishedOrders().size(), 0);
		assertEquals(2, player[2].getPlayerOrderPool().getFinishedOrders().size(), 0);
		assertEquals(2, player[3].getPlayerOrderPool().getFinishedOrders().size(), 0);

		
//		//Produktionsberechnung prüfen
		assertEquals(17000, player[0].getData().lastElement().getProduction(), 0);
		assertEquals(23000, player[1].getData().lastElement().getProduction(), 0);
		assertEquals(13200, player[2].getData().lastElement().getProduction(), 0);
		assertEquals(23100, player[3].getData().lastElement().getProduction(), 0);
		
//		//Turnover Prüfen: Preis verändert um Rabatt oder Aufschlag * Stückzahl (10 Stk = *1.1)
		assertEquals(20*300*1.1, player[0].getData().lastElement().getTurnover(), 0.1);
		assertEquals(10*300*1.1, player[1].getData().lastElement().getTurnover(), 0.1);
		assertEquals(10*300*1.1, player[2].getData().lastElement().getTurnover(), 0.1);
		assertEquals(10*300*1.1, player[3].getData().lastElement().getTurnover(), 0.1);
		
		
//		//Fix Kosten prüfen => Kapazität *100
		assertEquals(34*100, player[0].getData().lastElement().getFixCosts(), 0);
		assertEquals(46*100, player[1].getData().lastElement().getFixCosts(), 0);
		assertEquals(26*100, player[2].getData().lastElement().getFixCosts(), 0);
		assertEquals(46*100, player[3].getData().lastElement().getFixCosts(), 0);
		
//		//Gesamtkosten prüfen => Fixe Kosten + varCOsts *Airplane + interests
		double kredit0round3 = Math.abs((cash0 - 2000-1000-1000-120*20 - 34*100));
		double costsPlayer0round3 = 34*100 + 120*20 + (kredit0round3*0.15)/4;
		assertEquals(costsPlayer0round3, player[0].getData().lastElement().getCosts(), 0.1);
		double kredit1round3 = Math.abs((cash1 - 5000-100-100-110*10 - 46*100));
		double costsPlayer1round3 = 46*100 + 110*10 + (kredit1round3*0.15)/4;
		assertEquals(costsPlayer1round3, player[1].getData().lastElement().getCosts(), 0.1);
		double costsPlayer2round3 = 26*100 + 110*10;
		assertEquals(costsPlayer2round3, player[2].getData().lastElement().getCosts(), 0.1);
		double kredit3round3 = Math.abs((cash2 - 10000-100-100-110*10 - 46*100));
		double costsPlayer3round3 =46*100 + 110*10 + (kredit3round3*0.15)/4;
		assertEquals(costsPlayer3round3, player[3].getData().lastElement().getCosts(), 0.1);
		
//		//Turnover berechnen (Bei allen Spielern gelich = da allge gleiche Aufträge zu Beginn haben!)
		double turnoverPlayer0Round3 = 300*20*1.1;
		assertEquals(turnoverPlayer0Round3, player[0].getData().lastElement().getTurnover(), 0.1);
		double turnoverPlayer1Round3 = 300*10*1.1;
		assertEquals(turnoverPlayer1Round3, player[1].getData().lastElement().getTurnover(), 0.1);
		double turnoverPlayer2Round3 = 300*10*1.1;
		assertEquals(turnoverPlayer2Round3, player[2].getData().lastElement().getTurnover(), 0.1);
		double turnoverPlayer3Round3 = 300*10*1.1;
		assertEquals(turnoverPlayer3Round3, player[3].getData().lastElement().getTurnover(), 0.1);
		

		
//		//Profit berechnen
		double profit0Round3 = turnoverPlayer0Round3 - costsPlayer0round3 - 2000-1000-1000;
		assertEquals(profit0Round3, player[0].getData().lastElement().getProfit(), 0.1);
		double profit1Round3 = turnoverPlayer1Round3 - costsPlayer1round3 - 5000-100-100;
		assertEquals(profit1Round3, player[1].getData().lastElement().getProfit(), 0.1);
		double profit2Round3 = turnoverPlayer2Round3 - costsPlayer2round3 - 100-100-100;
		assertEquals(profit2Round3, player[2].getData().lastElement().getProfit(), 0.1);
		double profit3Round3 = turnoverPlayer3Round3 - costsPlayer3round3 - 10000-100-100;
		assertEquals(profit3Round3, player[3].getData().lastElement().getProfit(), 0.1);
//		
//		// Turnover berechnen
		double totalTurnoverRound3 = turnoverPlayer0Round3 +  turnoverPlayer1Round3 + turnoverPlayer2Round3+ turnoverPlayer3Round3+  totalTurnover;
		assertEquals(totalTurnoverRound3, market.getTotalTurnover(), 0.1);
//		
		//Market Share berechnen:
		assertEquals(40, player[0].getData().lastElement().getMarketshare(), 0.1);
		assertEquals(20, player[1].getData().lastElement().getMarketshare(), 0.1);
		assertEquals(20, player[2].getData().lastElement().getMarketshare(), 0.1);
		assertEquals(20, player[3].getData().lastElement().getMarketshare(), 0.1);
		
//		//Cash prüfen => cash = Kredit tilgen + Kreditzinsen zurückzahlen 
		double cash0Round3 = turnoverPlayer0Round3 - kredit0round3 - (kredit0round3*0.15)/4;
		if(cash0Round3>0){
		assertEquals(cash0Round3, player[0].getCash(), 0.1);
		}else{
			assertEquals(Math.abs(cash0Round3), player[0].getShortTimeCredit().getAmount(), 0.1);
		}
		double cash1Round3 = turnoverPlayer1Round3 - kredit1round3 - (kredit1round3*0.15)/4;
		if(cash1Round3>0){
		assertEquals(cash1Round3, player[1].getCash(), 0.1);
		}else{
			assertEquals(Math.abs(cash1Round3), player[1].getShortTimeCredit().getAmount(), 0.1);
		}
		double cash2Round3 = turnoverPlayer2Round3;
		if(cash2Round3>0){
		assertEquals(cash2Round3, player[2].getCash(), 0.1);
		}else{
			assertEquals(Math.abs(cash2Round3), player[2].getShortTimeCredit().getAmount(), 0.1);
		}
		double cash3Round3 = turnoverPlayer3Round3 - kredit3round3 - (kredit3round3*0.15)/4;
		if(cash3Round3>0){
		assertEquals(cash3Round3, player[3].getCash(), 0.1);
		}else{
			assertEquals(Math.abs(cash3Round3), player[3].getShortTimeCredit().getAmount(), 0.1);
		}
		
		
		
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
