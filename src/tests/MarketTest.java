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
	
	
	@Test
	public void testCalcTotalTurnover(){
		Vector<PlayerData> player0Data = mechanics.getPlayers()[0].getData();
		Vector<PlayerData> player1Data = mechanics.getPlayers()[1].getData();
		Vector<PlayerData> player2Data = mechanics.getPlayers()[2].getData();
		Vector<PlayerData> player3Data = mechanics.getPlayers()[3].getData();
		
		player0Data.add(new PlayerData(mechanics.getPlayers()[0].getId(),1,100));
		player1Data.add(new PlayerData(mechanics.getPlayers()[1].getId(),1,100));
		player2Data.add(new PlayerData(mechanics.getPlayers()[2].getId(),1,100));
		player3Data.add(new PlayerData(mechanics.getPlayers()[3].getId(),1,100));
		
		market.calcTotalTurnover(players);
		//400 + 7500*4 (7500 ist der Init Wert des Turnover für jeden Player)
		assertEquals(30400, market.getTotalTurnover());
		
		
		player0Data.add(new PlayerData(mechanics.getPlayers()[0].getId(),2,200));
		player1Data.add(new PlayerData(mechanics.getPlayers()[1].getId(),2,200));
		player2Data.add(new PlayerData(mechanics.getPlayers()[2].getId(),2,200));
		player3Data.add(new PlayerData(mechanics.getPlayers()[3].getId(),2,200));
		
		market.calcTotalTurnover(players);
		//1200 + 7500*4 (7500 ist der Init Wert des Turnover für jeden Player)
		assertEquals(31200, market.getTotalTurnover());
		
	}
	
	@Test
	public void testCalcMarketShare(){
		Vector<PlayerData> player0Data = mechanics.getPlayers()[0].getData();
		Vector<PlayerData> player1Data = mechanics.getPlayers()[1].getData();
		Vector<PlayerData> player2Data = mechanics.getPlayers()[2].getData();
		Vector<PlayerData> player3Data = mechanics.getPlayers()[3].getData();
		
		player0Data.add(new PlayerData(mechanics.getPlayers()[0].getId(),1,100));
		player1Data.add(new PlayerData(mechanics.getPlayers()[1].getId(),1,100));
		player2Data.add(new PlayerData(mechanics.getPlayers()[2].getId(),1,100));
		player3Data.add(new PlayerData(mechanics.getPlayers()[3].getId(),1,100));
		
		market.calcTotalTurnover(players);
		market.calcMarketSharePerPlayer(players);
		assertEquals(0.25, mechanics.getPlayers()[0].getData().get(1).getMarketshare(), 0.01);
		
		
		player0Data.add(new PlayerData(mechanics.getPlayers()[0].getId(),2,2000));
		player1Data.add(new PlayerData(mechanics.getPlayers()[1].getId(),2,0));
		player2Data.add(new PlayerData(mechanics.getPlayers()[2].getId(),2,0));
		player3Data.add(new PlayerData(mechanics.getPlayers()[3].getId(),2,0));
		
		
		market.calcTotalTurnover(players);
		market.calcMarketSharePerPlayer(players);
		
		//TotalTurnover = 7500*4+400 (Runde 1) + 2000 runde 2
		// Turnover Player1 7500+100+2000 = 96000
		//9600/32400= 29,63%
		assertEquals(0.2963, mechanics.getPlayers()[0].getData().get(2).getMarketshare(), 0.1);
		
		
	}




	
}
