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
import Server.PlayerDataCalculator;

public class SimulatingAWholeRound {
	Handler handler = null;
	Mechanics mechanics = null;
	PlayerDataCalculator pdc = null;
	Vector<Conn> connections= null;
	Market market = null;
	
	@Before
	public void testGeneratePlayers() {
		handler = new Handler(3);
		mechanics = new Mechanics(handler);
		pdc = new PlayerDataCalculator(mechanics);
		connections = new Vector<Conn>();
		market = new Market();
		
		connections.add(new Conn(new Socket(), handler));
		connections.get(0).setNick("Mats1");
		connections.get(0).setId(0);
		connections.add(new Conn(new Socket(), handler));
		connections.get(1).setId(1);
		connections.get(1).setNick("Mats2");
		connections.add(new Conn(new Socket(), handler));
		connections.get(2).setId(1);
		connections.get(2).setNick("Mats3");
		connections.add(new Conn(new Socket(), handler));
		connections.get(3).setId(1);
		connections.get(3).setNick("Mats4");
		
		handler.setConnections(connections);
		mechanics.startGame(connections);
		
	}
	
	
	//Testet ob alle Werte korrekt eingetragen sind und sich die ArrayListen im OrderPool richtig
	//verschieben am Ende einer Runde.
	@Test
	public void areAllValuesAreInTheHistory()
	{
		mechanics.acceptOrder(0, mechanics.getPlayers()[0].getPlayerOrderPool().getNewOrders().get(0).getOrderId());

		assertEquals(1, mechanics.getPlayers()[0].getPlayerOrderPool().getAcceptedOrders().size(),0);
		
		mechanics.produceOrder(0, mechanics.getPlayers()[0].getPlayerOrderPool().getAcceptedOrders().get(0).getOrderId());

		
		assertEquals(1, mechanics.getPlayers()[0].getPlayerOrderPool().getOrdersToProduce().size(),0);
		
		mechanics.valuesInserted("200;100;100;1;300", "Mats1");
		mechanics.valuesInserted("100;100;100;1;300", "Mats2");
		mechanics.valuesInserted("100;100;100;1;300", "Mats3");
		mechanics.valuesInserted("100;100;100;1;300", "Mats4");

		assertEquals(1, mechanics.getPlayers()[0].getPlayerOrderPool().getFinishedOrders().size(),0);
		assertEquals(1, mechanics.getPlayers()[0].getPlayerOrderPool().getOrdersToProduce().size(),0);
		assertEquals(0, mechanics.getPlayers()[0].getData().lastElement().getPlayerID(),0);
		assertEquals(2600, mechanics.getPlayers()[0].getData().lastElement().getFixCosts(),0);
		assertEquals(110, mechanics.getPlayers()[0].getData().lastElement().getVarCosts(),0);
		assertEquals(5460, mechanics.getPlayers()[0].getData().lastElement().getCosts(),0);
		assertEquals(100, mechanics.getPlayers()[0].getData().lastElement().getResearch(),0);
		assertEquals(100, mechanics.getPlayers()[0].getData().lastElement().getMarketing(),0);
		assertEquals(13200, mechanics.getPlayers()[0].getData().lastElement().getProduction(),0);
		assertEquals(300, mechanics.getPlayers()[0].getData().lastElement().getPricePerAirplane(),0);
		assertEquals(6630, mechanics.getPlayers()[0].getData().lastElement().getTurnover(),0);
		assertEquals(770, mechanics.getPlayers()[0].getData().lastElement().getProfit(),0);
		assertEquals(26, mechanics.getPlayers()[0].getCapacityLeft(),0);
		
		CopyOnWriteArrayList<Order> tmp = mechanics.getPlayers()[0].getPlayerOrderPool().getToProduceNextRound();
		
		mechanics.valuesInserted("100;100;100;1;300", "Mats1");
		mechanics.valuesInserted("100;100;100;1;300", "Mats2");
		mechanics.valuesInserted("100;100;100;1;300", "Mats3");
		mechanics.valuesInserted("100;100;100;1;300", "Mats4");
		
		assertEquals(tmp, mechanics.getPlayers()[0].getPlayerOrderPool().getOrdersToProduce());
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

}
