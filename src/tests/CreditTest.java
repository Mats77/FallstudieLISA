package tests;

import static org.junit.Assert.*;

import java.net.Socket;
import java.util.Vector;

import org.junit.Before;
import org.junit.Test;

import Server.Bank;
import Server.Conn;
import Server.Credit;
import Server.Handler;
import Server.Mechanics;
import Server.Player;
import Server.PlayerDataCalculator;

public class CreditTest {
	Handler handler = new Handler(1);
	Mechanics mechanics = new Mechanics(handler);
	PlayerDataCalculator pdc = new PlayerDataCalculator(mechanics);
	Vector<Conn> connections= new Vector<Conn>();
	
	@Before
	public void initializePlayers() 
	{
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
	
	
	//Short Time Credits
	
	@Test
	public void gettingMoney()
	{
		Player player = mechanics.getPlayers()[0];
		double tmpCash = player.getCash();
		double tmpDebtCapital = player.getDebtCapital();
		
		player.addCash(500);
		
		assertEquals(tmpCash+500, player.getCash(),0.1);
		assertEquals(tmpDebtCapital, player.getDebtCapital(),0.1);
	}
	
	@Test
	public void testSpendingTooMuchMoney()
	{
		Player player = mechanics.getPlayers()[0];
		player.spendMoney(player.getCash());
		
		player.spendMoney(1000);
		
		assertEquals(0, player.getCash(),0.1);
		assertEquals(1000, player.getCredits().elementAt(0).getAmount(),0.1);
	}
	
	@Test
	public void testGettingMoneyWhileHavingAShortTimeCredit()
	{
		Player player = mechanics.getPlayers()[0];
		player.spendMoney(player.getCash()+500);
		
		player.addCash(600);
		
		assertEquals(100, player.getCash(), 0.1);
		assertEquals(0, player.getCredits().size());
	}
	
	@Test
	public void testSpendingMoneyWhileHavingAShortTimeCredit()
	{
		Player player = mechanics.getPlayers()[0];
		player.spendMoney(player.getCash() + 1000);
		
		player.spendMoney(1000);
		
		assertEquals(1, player.getCredits().size(),0.1);
		assertEquals(2000, player.getCredits().elementAt(0).getAmount(), 0.1);
		assertEquals(0, player.getCash(),0.1);
	}
	
	@Test
	public void testSpendingMoney()
	{
		Player player = mechanics.getPlayers()[0];
		double tmpDebtCapital = player.getDebtCapital();
		
		player.spendMoney(player.getCash()-1000);
		
		assertEquals(1000, player.getCash(),0.1);
		assertEquals(tmpDebtCapital, player.getDebtCapital(), 0.1);
	}
	
	@Test
	public void testGettingNotEnoughMoneyToPaybackShortTimeCredit()
	{
		Player player = mechanics.getPlayers()[0];
		player.spendMoney(player.getCash()+1000);
		
		player.addCash(500);
		
		assertEquals(0, player.getCash(),0.1);
		assertEquals(500, player.getCredits().elementAt(0).getAmount(),0.1);
		assertEquals(1, player.getCredits().size(),0.1);
	}
	
	//Longtime Credits
	
	@Test
	public void testCreditOffers()
	{
		Player player0 = mechanics.getPlayers()[0];
		Player player1 = mechanics.getPlayers()[1];
		Player player2 = mechanics.getPlayers()[2];
		Player player3 = mechanics.getPlayers()[3];
		
		player1.spendMoney(1000);
		player2.spendMoney(2500);
		player3.spendMoney(100000000);
		
		double[] results0 = mechanics.getBank().getCreditOffer(player0,"1000;5");
		double[] results1 = mechanics.getBank().getCreditOffer(player1,"1000;5");
		double[] results2 = mechanics.getBank().getCreditOffer(player2,"1000;5");
		double[] results3 = mechanics.getBank().getCreditOffer(player3, "1000;5");
		
		assertEquals(0.09, results0[2],0.1);
		assertEquals(0.11, results1[2],0.1);
		assertEquals(0.13, results2[2],0.1);
		assertEquals(0.14,results3[2],0.1);
	}
	
	@Test
	public void testPaybackLongTimeCredit()
	{
		Player player = mechanics.getPlayers()[0];
	}

}
