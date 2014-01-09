package tests;

import static org.junit.Assert.*;

import java.net.Socket;
import java.util.Vector;

import org.junit.BeforeClass;
import org.junit.Test;

import Server.*;


public class MarketTest {
	static Handler handler = new Handler(3);
	static Mechanics mechanics = new Mechanics(handler);
	static Vector<Conn> connections= new Vector<Conn>();
	static Market market;
	static OrderPool orderPool;
	
	@BeforeClass
	public static void testGeneratePlayers() {
		connections.add(new Conn(new Socket(), handler));
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
		mechanics.generatePlayers(connections);
		market= mechanics.getMarket();
		
	}
	
	@Test
	public void testSplitOrders(){
		orderPool = market.getOrderPool();
		orderPool.delAllOrders(); 
		
		//8mal identische Aufträge erstellt
		for (int i = 0; i < 8; i++) {
			orderPool.addOneOrderToPool(new Order(100,0));
		}
		
		//Aufträge verteilen. Da Q0 jeder spieler die glieche CompanyValue hat muss gleich verteilt werden
		market.splitOrders(mechanics.getPlayers());
		
		//Der OrderPool von jedem Spieler sollte gleich mit diesem OrderPool sein.
		PlayerOrderPool playerOrderPool = new PlayerOrderPool(null);
		playerOrderPool.addNewOrder(new Order(500,0));
		playerOrderPool.addNewOrder(new Order(100,0));
		
		assertEquals(playerOrderPool, mechanics.getPlayers()[0].getPlayerOrderPool());
		assertEquals(playerOrderPool, mechanics.getPlayers()[1].getPlayerOrderPool());
		assertEquals(playerOrderPool, mechanics.getPlayers()[2].getPlayerOrderPool());
		assertEquals(playerOrderPool, mechanics.getPlayers()[3].getPlayerOrderPool());
	}



	
}
