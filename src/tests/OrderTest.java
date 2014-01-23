package tests;

import static org.junit.Assert.*;

import org.junit.Test;

import Server.Order;

public class OrderTest {
	

	@Test
	public void testSetPrice() {
		Order order1 = new Order(10, 1, true);
		Order order2 = new Order(20, 1, true);
		Order order3 = new Order(50, 1, true);
		
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
			Order order1 = new Order(100,1);
			Order order2 = new Order(100,3);
			
			assertTrue("Das fällige Quartal ist zu hoch", order1.getQuartalValidTo()<=4);
			assertTrue("Das fällige Quartal ist zu niedrig", order1.getQuartalValidTo()>=2);
			
			assertTrue("Das fällige Quartal ist zu hoch", order2.getQuartalValidTo()<=6);
			assertTrue("Das fällige Quartal ist zu niedrig", order2.getQuartalValidTo()>=4);
			
			assertTrue("Der optionale Anteil der Orders ist größer als max. 1/4 der totalQuantity", order1.getOptionalQuantity()<=25);
			assertTrue("Der optionale Anteil der Orders ist kleiner als max. 1/4 der totalQuantity", order1.getOptionalQuantity()>=0.0);
			
			assertEquals(order1.getTotalQuantity(), order1.getOptionalQuantity() + order1.getFixedQuantity(), 0.01);
		}		
		
	}

}
