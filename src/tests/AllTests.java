package tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ SimulatingWholeRounds.class,CompanyValueTest.class, MarketTest.class, CreditTest.class, PlayerDataCalculatorTest.class, WinnerTest.class,
	EventTest.class, OrderTest.class
		})
public class AllTests {

}
