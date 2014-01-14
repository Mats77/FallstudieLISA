package tests;

import static org.junit.Assert.*;

import org.junit.Test;

import Server.Order;

public class OrderTest {
	

	@Test
	public void testSetPrice() {
		Order order1 = new Order(10, 1);
		Order order2 = new Order(20, 1);
		Order order3 = new Order(25, 1);
		
		order1.setPrice(100);
		order2.setPrice(100);
		order3.setPrice(100);
		
		assertEquals(110, order1.getPricePerAirplane(), 0.1);
		assertEquals(100, order2.getPricePerAirplane(), 0.1);
		assertEquals(85, order3.getPricePerAirplane(), 0.1);
		
	}
	
	@Test
	public void testValidToDate(){
		
		for (int i = 0; i < 100; i++) {
			Order order1 = new Order(10,1);
			Order order2 = new Order(10,3);
			
			assertTrue("Das fällige Quartal ist zu hoch", order1.getQuartalValidTo()<=4);
			assertTrue("Das fällige Quartal ist zu niedrig", order1.getQuartalValidTo()>=2);
			
			assertTrue("Das fällige Quartal ist zu hoch", order2.getQuartalValidTo()<=6);
			assertTrue("Das fällige Quartal ist zu niedrig", order2.getQuartalValidTo()>=4);
		}		
		
	}

}
