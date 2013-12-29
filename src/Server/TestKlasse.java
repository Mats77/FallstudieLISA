package Server;

public class TestKlasse {
	private Handler handler = new Handler();
	private Mechanics mechanics = new Mechanics(handler);
	private PlayerDataCalculator pdc = new PlayerDataCalculator(mechanics);
	private Player player = new Player(0, "Mats", pdc, mechanics);
	
	public static void main (String[] args)
	{
		TestKlasse test = new TestKlasse();
		test.player.saveNextRoundValues("10030;1200;1400;30;2;300");  //Array: Produktion;Marketing;Entwicklung;Anzahl Flgzeuge;Materialstufe;Preis
		Player[] array = {test.player};
		test.pdc.generateNewCompanyValues(array);
	}

}
