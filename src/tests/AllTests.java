package tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ CompanyValueTest.class, MarketTest.class, CreditTest.class, PlayerDataCalculatorTest.class
		})
public class AllTests {

}