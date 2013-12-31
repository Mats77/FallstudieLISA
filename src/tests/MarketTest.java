package tests;

import static org.junit.Assert.*;

import org.junit.Test;

import Server.Handler;
import Server.Mechanics;
import Server.Player;

public class MarketTest {
	Handler handler = new Handler();
	Mechanics mechanics = new Mechanics(handler);
	Player [] player;
	
	public MarketTest(){
		CompanyValueTest compValueTest = new CompanyValueTest();
		compValueTest.testGeneratePlayers();
		Player[] player= mechanics.getPlayers();
	}
	
	@Test
	public void testSplitOrders(){
		
	}

}
