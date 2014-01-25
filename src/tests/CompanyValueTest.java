package tests;

import static org.junit.Assert.*;

import java.net.Socket;
import java.util.Vector;

import org.junit.Before;
import org.junit.Test;

import Server.Conn;
import Server.Handler;
import Server.Market;
import Server.Mechanics;
import Server.Player;
import Server.PlayerDataCalculator;



//In mechanics müssen 2 Zeilen auskommentiert werden
public class CompanyValueTest {
	Handler handler = new Handler(3);
	Mechanics mechanics = new Mechanics(handler);
	PlayerDataCalculator pdc = new PlayerDataCalculator(mechanics);
	Vector<Conn> connections= new Vector<Conn>();
	Market market = new Market();
	
	@Before
	public void testGeneratePlayers() {
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
	
	@Test
	public void testCompanyValues(){
		
		mechanics.valuesInserted("1000;500;9500;2", "Mats1");
		mechanics.valuesInserted("1000;4000;6000;2", "Mats2");
		mechanics.valuesInserted("1000;5000;5000;2", "Mats3");
		System.out.println(mechanics.getQuartal());
		mechanics.valuesInserted("1000;5500;4500;2", "Mats4");
		
		double[] values = pdc.generateNewCompanyValues(mechanics.getPlayers());
		
		Player[] players= mechanics.getPlayers();
		assertEquals(12885, values[0],1);
		assertEquals(13598.2857, values[1],1);
		assertEquals(11785, values[2],1);
		assertEquals(11000, values[3],1);
	}
	
	@Test
	public void testValuesInsertedAfterOneRound(){
		
		
		//round1
		mechanics.valuesInserted("1000;1000;1111;2", "Mats1");	//??, Marketing, Research, Anzahl, Material; Spieler
		mechanics.valuesInserted("1000;2000;2222;2", "Mats2");
		mechanics.valuesInserted("1000;3000;3333;2", "Mats3");
		mechanics.valuesInserted("1000;10000;4444;2", "Mats4");
		
		Player[] players= mechanics.getPlayers();
		
		assertEquals(1500, pdc.calculateMarketing(players[0].getData()),1e-8);
		assertEquals(2500, pdc.calculateMarketing(players[1].getData()),1e-8);
		assertEquals(3500, pdc.calculateMarketing(players[2].getData()),1e-8);
		assertEquals(10500, pdc.calculateMarketing(players[3].getData()),1e-8);
		
		assertEquals(1611, pdc.calculateResearch(players[0].getData()),1e-8);
		assertEquals(2722, pdc.calculateResearch(players[1].getData()),1e-8);
		assertEquals(3833, pdc.calculateResearch(players[2].getData()),1e-8);
		assertEquals(4944, pdc.calculateResearch(players[3].getData()),1e-8);
	}
	
	@Test
	public void testValuesInsertedAfterTwoRounds(){
		//round1
		
		mechanics.valuesInserted("1000;1000;1111;2", "Mats1");
		mechanics.valuesInserted("1000;2000;2222;2", "Mats2");
		mechanics.valuesInserted("1000;3000;3333;2", "Mats3");
		mechanics.valuesInserted("1000;10000;4444;2", "Mats4");
		
		//round2
		mechanics.valuesInserted("1000;1000;1000;2", "Mats1");
		mechanics.valuesInserted("1000;1000;1000;2", "Mats2");
		mechanics.valuesInserted("1000;1000;1000;2", "Mats3");
		mechanics.valuesInserted("1000;1000;1000;2", "Mats4");
		
		Player[] players= mechanics.getPlayers();
		
		assertEquals(2500, pdc.calculateMarketing(players[0].getData()),1e-8);
		assertEquals(3500, pdc.calculateMarketing(players[1].getData()),1e-8);
		assertEquals(4500, pdc.calculateMarketing(players[2].getData()),1e-8);
		assertEquals(11500, pdc.calculateMarketing(players[3].getData()),1e-8);
		
		assertEquals(2611, pdc.calculateResearch(players[0].getData()),1e-8);
		assertEquals(3722, pdc.calculateResearch(players[1].getData()),1e-8);
		assertEquals(4833, pdc.calculateResearch(players[2].getData()),1e-8);
		assertEquals(5944, pdc.calculateResearch(players[3].getData()),1e-8);
	}
	
	@Test
	public void testValuesInsertedAfterFourRounds(){
		
		
		//round1
		mechanics.valuesInserted("1000;1000;1111;2", "Mats1");
		mechanics.valuesInserted("1000;2000;2222;2", "Mats2");
		mechanics.valuesInserted("1000;3000;3333;2", "Mats3");
		mechanics.valuesInserted("1000;10000;4444;2", "Mats4");
		
		//round2
		mechanics.valuesInserted("1000;1000;1000;2", "Mats1");
		mechanics.valuesInserted("1000;1000;1000;2", "Mats2");
		mechanics.valuesInserted("1000;1000;1000;2", "Mats3");
		mechanics.valuesInserted("1000;1000;1000;2", "Mats4");
		
		
		//round3
		mechanics.valuesInserted("1000;6754;2343;2", "Mats1");
		mechanics.valuesInserted("1000;5478;1224;2", "Mats2");
		mechanics.valuesInserted("1000;9882;23467;2", "Mats3");
		mechanics.valuesInserted("1000;23472;2347;2", "Mats4");
		
		//round4
		mechanics.valuesInserted("1000;1000;1000;2", "Mats1");
		mechanics.valuesInserted("1000;1000;1000;2", "Mats2");
		mechanics.valuesInserted("1000;1000;1000;2", "Mats3");
		mechanics.valuesInserted("1000;1000;1000;2", "Mats4");
		
		
		Player[] players= mechanics.getPlayers();

		assertEquals(8754, pdc.calculateMarketing(players[0].getData()),1e-8);
		assertEquals(7478, pdc.calculateMarketing(players[1].getData()),1e-8);
		assertEquals(11882, pdc.calculateMarketing(players[2].getData()),1e-8);
		assertEquals(25472, pdc.calculateMarketing(players[3].getData()),1e-8);
		
		assertEquals(5954, pdc.calculateResearch(players[0].getData()),1e-8);
		assertEquals(5946, pdc.calculateResearch(players[1].getData()),1e-8);
		assertEquals(29300, pdc.calculateResearch(players[2].getData()),1e-8);
		assertEquals(9291, pdc.calculateResearch(players[3].getData()),1e-8);
		
	}


}
