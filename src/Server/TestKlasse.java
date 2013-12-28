package Server;

public class TestKlasse {
	private static PlayerDataCalculator pdc = new PlayerDataCalculator(new Mechanics(new Handler()));
	private static Player player = new Player(1, "Mats", pdc);
	
	public static void main (String[] args)
	{
		player.saveNextRoundValues("1000;2000;3000;4000");
	}

}
