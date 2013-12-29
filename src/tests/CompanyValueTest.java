package tests;

import static org.junit.Assert.*;

import java.net.Socket;
import java.util.Vector;

import org.junit.Test;

import Server.Conn;
import Server.Handler;
import Server.Mechanics;
import Server.Player;
import Server.PlayerDataCalculator;

public class CompanyValueTest {
	Handler handler = new Handler();
	Mechanics mechanics = new Mechanics(handler);
	PlayerDataCalculator pdc = new PlayerDataCalculator(mechanics);
	Vector<Conn> connections= new Vector<Conn>();
	
	@Test
	public void testGeneratePlayers() {
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
	}
	
	@Test
	public void testCompanyValues(){
		testGeneratePlayers();
		mechanics.valuesInserted("1000;1000;1000;30;2", "Mats1");
		mechanics.valuesInserted("1000;1000;1000;30;2", "Mats2");
		mechanics.valuesInserted("1000;1000;1000;30;2", "Mats3");
		mechanics.valuesInserted("1000;1000;1000;30;2", "Mats4");
		
		Player[] players= mechanics.getPlayers();
		assertEquals(0.25, players[0].getCompanyValue(),1e-8);
		assertEquals(0.25, players[1].getCompanyValue(),1e-8);
		assertEquals(0.25, players[2].getCompanyValue(),1e-8);
		assertEquals(0.25, players[3].getCompanyValue(),1e-8);
	}
	
	@Test
	public void testValuesInsertedAfterOneRound(){
		testGeneratePlayers();
		
		//round1
		mechanics.valuesInserted("1000;1000;1111;30;2", "Mats1");	//??, Marketing, Research, Anzahl, Material; Spieler
		mechanics.valuesInserted("1000;2000;2222;30;2", "Mats2");
		mechanics.valuesInserted("1000;3000;3333;30;2", "Mats3");
		mechanics.valuesInserted("1000;10000;4444;30;2", "Mats4");
		
		Player[] players= mechanics.getPlayers();
		
		assertEquals(1000, pdc.calculateMarketing(players[0].getData()),1e-8);
		assertEquals(2000, pdc.calculateMarketing(players[1].getData()),1e-8);
		assertEquals(3000, pdc.calculateMarketing(players[2].getData()),1e-8);
		assertEquals(10000, pdc.calculateMarketing(players[3].getData()),1e-8);
		
		assertEquals(1111, pdc.calculateResearch(players[0].getData()),1e-8);
		assertEquals(2222, pdc.calculateResearch(players[1].getData()),1e-8);
		assertEquals(3333, pdc.calculateResearch(players[2].getData()),1e-8);
		assertEquals(4444, pdc.calculateResearch(players[3].getData()),1e-8);
	}
	
	@Test
	public void testValuesInsertedAfterTwoRounds(){
		//round1
		testGeneratePlayers();
		mechanics.valuesInserted("1000;1000;1111;30;2", "Mats1");
		mechanics.valuesInserted("1000;2000;2222;30;2", "Mats2");
		mechanics.valuesInserted("1000;3000;3333;30;2", "Mats3");
		mechanics.valuesInserted("1000;10000;4444;30;2", "Mats4");
		
		//round2
		mechanics.valuesInserted("1000;1000;1000;30;2", "Mats1");
		mechanics.valuesInserted("1000;1000;1000;30;2", "Mats2");
		mechanics.valuesInserted("1000;1000;1000;30;2", "Mats3");
		mechanics.valuesInserted("1000;1000;1000;30;2", "Mats4");
		
		Player[] players= mechanics.getPlayers();
		
		assertEquals(2000, pdc.calculateMarketing(players[0].getData()),1e-8);
		assertEquals(3000, pdc.calculateMarketing(players[1].getData()),1e-8);
		assertEquals(4000, pdc.calculateMarketing(players[2].getData()),1e-8);
		assertEquals(11000, pdc.calculateMarketing(players[3].getData()),1e-8);
		
		assertEquals(2111, pdc.calculateResearch(players[0].getData()),1e-8);
		assertEquals(3222, pdc.calculateResearch(players[1].getData()),1e-8);
		assertEquals(4333, pdc.calculateResearch(players[2].getData()),1e-8);
		assertEquals(5444, pdc.calculateResearch(players[3].getData()),1e-8);
	}
	
	@Test
	public void testValuesInsertedAfterFourRounds(){
		testGeneratePlayers();
		
		//round1
		mechanics.valuesInserted("1000;1000;1111;30;2", "Mats1");
		mechanics.valuesInserted("1000;2000;2222;30;2", "Mats2");
		mechanics.valuesInserted("1000;3000;3333;30;2", "Mats3");
		mechanics.valuesInserted("1000;10000;4444;30;2", "Mats4");
		
		//round2
		mechanics.valuesInserted("1000;1000;1000;30;2", "Mats1");
		mechanics.valuesInserted("1000;1000;1000;30;2", "Mats2");
		mechanics.valuesInserted("1000;1000;1000;30;2", "Mats3");
		mechanics.valuesInserted("1000;1000;1000;30;2", "Mats4");
		
		
		//round3
		mechanics.valuesInserted("1000;6754;2343;30;2", "Mats1");
		mechanics.valuesInserted("1000;5478;1224;30;2", "Mats2");
		mechanics.valuesInserted("1000;9882;23467;30;2", "Mats3");
		mechanics.valuesInserted("1000;23472;2347;30;2", "Mats4");
		
		//round4
		mechanics.valuesInserted("1000;1000;1000;30;2", "Mats1");
		mechanics.valuesInserted("1000;1000;1000;30;2", "Mats2");
		mechanics.valuesInserted("1000;1000;1000;30;2", "Mats3");
		mechanics.valuesInserted("1000;1000;1000;30;2", "Mats4");
		
		
		Player[] players= mechanics.getPlayers();

		assertEquals(8754, pdc.calculateMarketing(players[0].getData()),1e-8);
		assertEquals(7478, pdc.calculateMarketing(players[1].getData()),1e-8);
		assertEquals(11882, pdc.calculateMarketing(players[2].getData()),1e-8);
		assertEquals(25472, pdc.calculateMarketing(players[3].getData()),1e-8);
		
		assertEquals(5454, pdc.calculateResearch(players[0].getData()),1e-8);
		assertEquals(5446, pdc.calculateResearch(players[1].getData()),1e-8);
		assertEquals(28800, pdc.calculateResearch(players[2].getData()),1e-8);
		assertEquals(8791, pdc.calculateResearch(players[3].getData()),1e-8);
		
	}


}
