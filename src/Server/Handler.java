package Server;
public class Handler {

	private Conn[] players = new Conn[6];
	private Mechanics mechanics;
	
	
	public Handler()
	{
		//mechanics = new Mechanics(this);
	}

	public void addPlayer(Conn player) {
		for (int i = 0; i < players.length; i++) {
			if (players[i] == null) {
				players[i] = player;
				break;
			}
		}
	}
	
	
	
	public void spread(String txt) { //sendet an alle
		for (int i = 0; i < players.length; i++) {
			if (players[i] != null) {
				players[i].send(txt);
			}
		}
	}
	
	public void handleString(String txt, Conn sender) {
		if (txt.startsWith("CHAT ")) {
			String s = "CHAT " +  getPlayerID(sender) + " "
					+ sender.getNick() + ": " + txt.substring(5);
			spread(s);
		} else if(txt.startsWith("READY ")) {
			sender.setReady(true);
			if(areAllReady()==true){
				String s = "ALLREADY ";
				spread(s);
			}
		} else if (txt.startsWith("ASKFORNICK")) {
			sender.setNick(txt.substring(11));
		} else if (txt.startsWith("VALUES")){
			System.out.println(txt.substring(7).split(";"));
			System.out.println(sender.getId());
			System.out.println(sender.getName());
		}
	}
	
	private boolean areAllReady() { //teste in der Lobby ob alle fertig sind. Evtl markieren wer fertig ist usw.
		boolean toReturn = true;
		
		for(int i=0; i<players.length; i++)
		{
			if(players[i]==null)
			{
				break;
			} else {
				if(players[i].isReady() == false) 
				{
					toReturn = false;
				}
			}
		}
		return toReturn;
	}

	public int getPlayerID(Conn player) { // fuer den Chat
		for (int i = 0; i < players.length; i++) {
			if (players[i] == player) {
				return i;
			}
		}
		return 0;
	}



	public int getID(Conn con) {
		int toReturn = -1;
		for (int i=0; i<players.length; i++){
			if(players[i]==con) {
				toReturn = i;
			}
		}
		return toReturn;
	}
}
