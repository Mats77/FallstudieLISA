package Server;

public class GameHistory {
	private Mechanics mechanics;
	private PlayerData[][] playerData;//doppelt fuer spieler / Runde , evtl. als Attrobute entfernen --> Redundanz
	
	public GameHistory(Mechanics m)
	{
		this.mechanics = m;
	}//Konstruktor
	
	public void setPlayerData(int pid, int r, double m, double ms, int p, int f,
			int mg, double u, int fg, double fp)
	{
		playerData[pid][r] = new PlayerData(pid, r, m, ms, p, f, mg, u, fg, fp);
	}//playerData-setter

}//class