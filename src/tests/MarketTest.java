package tests;

import static org.junit.Assert.*;

import java.net.Socket;
import java.util.Vector;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import Server.*;


public class MarketTest {
	static Handler handler;
	static Mechanics mechanics;
	static Vector<Conn> connections;
	static Market market;
	static OrderPool orderPool;
	static Player[] players;
	
	@Before
	public void setUpGame() {
		handler  = new Handler(3);
		mechanics = new Mechanics(handler);
		connections = new Vector<Conn>();
		connections.add(new Conn(new Socket(), handler));
		connections.get(0).setId(0);
		connections.get(0).setNick("Mats1");
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
		mechanics.generatePlayers(connections);
		market= mechanics.getMarket();
		players = mechanics.getPlayers();
		
		
		orderPool = market.getOrderPool();
		orderPool.delAllOrders(); 
	}
	
	@Test
	public void testSplitOrdersStartGame(){
		
		for (int i = 0; i < players.length; i++) {
			players[i].setCompanyValue(100);
		}

		//8mal identische Aufträge erstellt
		for (int i = 0; i < 8; i++) {
			orderPool.addOneOrderToPool(new Order(100,0,true));
		}
		
		
		//Aufträge verteilen. Da Q0 jeder spieler die gleiche CompanyValue hat muss gleich verteilt werden
		market.splitOrders(players);
		
	
		
		assertEquals(2, mechanics.getPlayers()[0].getPlayerOrderPool().getNewOrders().size());
		assertEquals(2, mechanics.getPlayers()[1].getPlayerOrderPool().getNewOrders().size());
		assertEquals(2, mechanics.getPlayers()[2].getPlayerOrderPool().getNewOrders().size());
		assertEquals(2, mechanics.getPlayers()[3].getPlayerOrderPool().getNewOrders().size());
	}
	
	@Test
	public void testSplitOrdersFor2Player(){
		players[0].setCompanyValue(100);
		players[1].setCompanyValue(100);
		players[2].setCompanyValue(1);
		players[3].setCompanyValue(1);
		
		Order order[] = new Order[4];
		
		for (int i = 0; i < order.length; i++) {
			order[i] = new Order(100, 0);
			orderPool.addOneOrderToPool(order[i]);
		}
		
		market.splitOrders(players);
		
		assertEquals(2, mechanics.getPlayers()[0].getPlayerOrderPool().getNewOrders().size());
		assertEquals(2, mechanics.getPlayers()[1].getPlayerOrderPool().getNewOrders().size());
		assertEquals(0, mechanics.getPlayers()[2].getPlayerOrderPool().getNewOrders().size());
		assertEquals(0, mechanics.getPlayers()[3].getPlayerOrderPool().getNewOrders().size());
		
		assertEquals(order[0], mechanics.getPlayers()[0].getPlayerOrderPool().getNewOrders().get(0));
		assertEquals(order[1], mechanics.getPlayers()[1].getPlayerOrderPool().getNewOrders().get(0));
		assertEquals(order[2], mechanics.getPlayers()[0].getPlayerOrderPool().getNewOrders().get(1));
		assertEquals(order[3], mechanics.getPlayers()[1].getPlayerOrderPool().getNewOrders().get(1));

		
		
	}
	
	




	
}
