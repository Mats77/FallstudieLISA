package tests;

import static org.junit.Assert.*;

import java.net.Socket;
import java.util.Vector;

import org.junit.Before;
import org.junit.Test;

import Server.Conn;
import Server.Handler;
import Server.Mechanics;
import Server.PlayerData;
import Server.PlayerDataCalculator;

public class WinnerTest {
	Handler handler = new Handler(3);
	Mechanics mechanics = new Mechanics(handler);
	PlayerDataCalculator pdc = new PlayerDataCalculator(mechanics);
	Vector<Conn> connections= new Vector<Conn>();
	
	@Before
	public void testGeneratePlayers() {
		connections.add(new Conn(new Socket(), handler));
		connections.get(0).setId(0);
		connections.get(0).setNick("Mats1");
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
		mechanics.generatePlayers(connections);
	}
	
	
	@Test
	public void testEndGameWithCash() {
		mechanics.getPlayers()[0].addCash(1000);
		mechanics.getPlayers()[1].addCash(900);
		mechanics.getPlayers()[2].addCash(800);
		mechanics.getPlayers()[3].addCash(700);
		
		mechanics.setEndOfGame(4, 1);
		mechanics.endGame();
		
		assertEquals(0, handler.getWinnersDouble()[0][1],0);		//winnerMap
		assertEquals(1, handler.getWinnersDouble()[1][1],0);
		assertEquals(2, handler.getWinnersDouble()[2][1],0);
		assertEquals(3, handler.getWinnersDouble()[3][1],0);
	}
	
	@Test
	public void testEndGameWithCashWithDifferentOrder() {
		mechanics.getPlayers()[0].addCash(1000);
		mechanics.getPlayers()[1].addCash(900);
		mechanics.getPlayers()[2].addCash(2000);
		mechanics.getPlayers()[3].addCash(700);
		
		mechanics.setEndOfGame(4, 1);
		mechanics.endGame();
		
		assertEquals(2, handler.getWinnersDouble()[0][1],0);
		assertEquals(0, handler.getWinnersDouble()[1][1],0);
		assertEquals(1, handler.getWinnersDouble()[2][1],0);
		assertEquals(3, handler.getWinnersDouble()[3][1],0);
	}
	
	@Test
	public void testEndGameWithCashWithOneMoreOrder() {
		mechanics.getPlayers()[0].addCash(1000);
		mechanics.getPlayers()[1].addCash(900);
		mechanics.getPlayers()[2].addCash(2000);
		mechanics.getPlayers()[3].addCash(7000);
		
		mechanics.setEndOfGame(4, 1);
		mechanics.endGame();
		
		assertEquals(3, handler.getWinnersDouble()[0][1],0);
		assertEquals(2, handler.getWinnersDouble()[1][1],0);
		assertEquals(0, handler.getWinnersDouble()[2][1],0);
		assertEquals(1, handler.getWinnersDouble()[3][1],0);
	}
	
	@Test
	public void testEndGameWithMarketshare() {
		mechanics.getPlayers()[0].getData().elementAt(0).setMarketshare(40);
		mechanics.getPlayers()[1].getData().elementAt(0).setMarketshare(30);
		mechanics.getPlayers()[2].getData().elementAt(0).setMarketshare(20);
		mechanics.getPlayers()[3].getData().elementAt(0).setMarketshare(10);
		
		mechanics.setEndOfGame(1, 1);
		mechanics.endGame();
		
		assertEquals(0, handler.getWinnersDouble()[0][1],0);
		assertEquals(1, handler.getWinnersDouble()[1][1],0);
		assertEquals(2, handler.getWinnersDouble()[2][1],0);
		assertEquals(3, handler.getWinnersDouble()[3][1],0);
	}
	
	@Test
	public void testEndGameWithNumberOfPlanes() {
		mechanics.getPlayers()[0].getData().elementAt(0).setAirplanes(40);
		mechanics.getPlayers()[1].getData().elementAt(0).setAirplanes(20);
		mechanics.getPlayers()[2].getData().elementAt(0).setAirplanes(30);
		mechanics.getPlayers()[3].getData().elementAt(0).setAirplanes(10);
		
		mechanics.getPlayers()[0].getData().add(new PlayerData(0, 1, 800));
		mechanics.getPlayers()[0].getData().elementAt(1).setAirplanes(30);
		mechanics.getPlayers()[1].getData().add(new PlayerData(1, 1, 800));
		mechanics.getPlayers()[1].getData().elementAt(1).setAirplanes(100);
		mechanics.getPlayers()[2].getData().add(new PlayerData(2, 1, 800));
		mechanics.getPlayers()[2].getData().elementAt(1).setAirplanes(20);
		mechanics.getPlayers()[3].getData().add(new PlayerData(3, 1, 800));
		mechanics.getPlayers()[3].getData().elementAt(1).setAirplanes(300);
		
		mechanics.setEndOfGame(0, 1);
		mechanics.endGame();
		
		assertEquals(3, handler.getWinners()[0][1],0);
		assertEquals(1, handler.getWinners()[1][1],0);
		assertEquals(0, handler.getWinners()[2][1],0);
		assertEquals(2, handler.getWinners()[3][1],0);
	}
	
	@Test
	public void testEndGameWithProfit() {
		mechanics.getPlayers()[0].getData().elementAt(0).setProfit(40);
		mechanics.getPlayers()[1].getData().elementAt(0).setProfit(20);
		mechanics.getPlayers()[2].getData().elementAt(0).setProfit(30);
		mechanics.getPlayers()[3].getData().elementAt(0).setProfit(10);
		
		mechanics.getPlayers()[0].getData().add(new PlayerData(0, 1, 800));
		mechanics.getPlayers()[0].getData().elementAt(1).setProfit(30);
		mechanics.getPlayers()[1].getData().add(new PlayerData(1, 1, 800));
		mechanics.getPlayers()[1].getData().elementAt(1).setProfit(100);
		mechanics.getPlayers()[2].getData().add(new PlayerData(2, 1, 800));
		mechanics.getPlayers()[2].getData().elementAt(1).setProfit(20);
		mechanics.getPlayers()[3].getData().add(new PlayerData(3, 1, 800));
		mechanics.getPlayers()[3].getData().elementAt(1).setProfit(300);
		
		mechanics.setEndOfGame(3, 1);
		mechanics.endGame();
		
		assertEquals(3, handler.getWinnersDouble()[0][1],0);
		assertEquals(1, handler.getWinnersDouble()[1][1],0);
		assertEquals(0, handler.getWinnersDouble()[2][1],0);
		assertEquals(2, handler.getWinnersDouble()[3][1],0);
	}
	
	@Test
	public void testEndGameWithTurnover() {
		
		mechanics.getPlayers()[0].getData().add(new PlayerData(0, 1, 100.0));
		mechanics.getPlayers()[1].getData().add(new PlayerData(1, 1, 200.0));
		mechanics.getPlayers()[2].getData().add(new PlayerData(2, 1, 400.0));
		mechanics.getPlayers()[3].getData().add(new PlayerData(3, 1, 500.0));
				
		mechanics.setEndOfGame(2, 1);
		mechanics.endGame();
		
		assertEquals(3, handler.getWinnersDouble()[0][1],0);
		assertEquals(2, handler.getWinnersDouble()[1][1],0);
		assertEquals(1, handler.getWinnersDouble()[2][1],0);
		assertEquals(0, handler.getWinnersDouble()[3][1],0);
	}

}
