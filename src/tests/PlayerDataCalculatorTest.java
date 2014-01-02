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

public class PlayerDataCalculatorTest {
	Handler handler = new Handler();
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
	public void testCalcCapacities(){
		mechanics.getPlayers()[0].getData().lastElement().setProduction(10000);
		pdc.calcCapacities(mechanics.getPlayers());
		assertEquals(20, mechanics.getPlayers()[0].getData().lastElement().getCapacity(),0.5);
	}
	
	public void testCalcCosts(){
		mechanics.getPlayers()[0].
	}

}