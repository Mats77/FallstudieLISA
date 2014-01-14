package tests;

import static org.junit.Assert.*;

import java.net.Socket;
import java.util.Vector;

import org.junit.Before;
import org.junit.Test;

import Server.Conn;
import Server.Handler;
import Server.Mechanics;
import Server.PlayerDataCalculator;

public class WinnerTest {
	Handler handler = new Handler(3);
	Mechanics mechanics = new Mechanics(handler);
	PlayerDataCalculator pdc = new PlayerDataCalculator(mechanics);
	Vector<Conn> connections= new Vector<Conn>();
	
	@Before
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
	public void testEndGameWithCash() {
		mechanics.getPlayers()[0].addCash(1000);
		mechanics.getPlayers()[1].addCash(900);
		mechanics.getPlayers()[2].addCash(800);
		mechanics.getPlayers()[3].addCash(700);
		
		mechanics.setEndOfGame(4, 1);
		
		mechanics.valuesInserted("0;0;0;0;0", "Mats1");
		mechanics.valuesInserted("0;0;0;0;0", "Mats2");
		mechanics.valuesInserted("0;0;0;0;0", "Mats3");
		mechanics.valuesInserted("0;0;0;0;0", "Mats4");
	}

}
