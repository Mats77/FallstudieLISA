package Server;

import org.webbitserver.WebSocketConnection;

public class Handler {

	private Conn[] player = new Conn[4];
	private Mechanics mechanics;
	private Conn sender;
	
	
	public Handler(){
		//mechanics = new Mechanics(this);
	}

	public void addPlayer(Conn play) {
		for (int i = 0; i < player.length; i++) {
			if (player[i] == null) {
				player[i] = play;
				break;
			}
		}
	}
	
	
	
	public void spread(String txt) { //sendet an alle
		for (int i = 0; i < player.length; i++) {
			if (player[i] != null) {
				player[i].send(txt);
			}
		}
	}
	
	public void handleString(String txt, WebSocketConnection connection) {
		for (int i = 0; i < player.length; i++) {				// Sender der Nachricht wird ermittelt (Über die Verbindung/WebSocketConnection)
			if (player[i].getConnection().equals(connection)) {
				sender = player[i];
				break;
			}else{
				sender = null;
			}
		}
		if (sender == null) {
			//get player ID
			// txt Datei nach Spieler-ID durchsuchen, wenn eine gefunden wurde: 
			// player[i] mit Spieler-ID wird ausgelesen und die WebSocketConnection neu gesetzt
			
		}
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
		} else if (txt.startsWith("PLAYERNAME ")){
			
		}
	}
	
	private boolean areAllReady() { //teste in der Lobby ob alle fertig sind. Evtl markieren wer fertig ist usw.
		boolean toReturn = true;
		
		for(int i=0; i<player.length; i++)
		{
			if(player[i]==null)
			{
				break;
			} else {
				if(player[i].isReady() == false) 
				{
					toReturn = false;
				}
			}
		}
		return toReturn;
	}

	public int getPlayerID(Conn play) { // fuer den Chat
		for (int i = 0; i < player.length; i++) {
			if (player[i] == play) {
				return i;
			}
		}
		return 0;
	}



	public int getID(Conn con) {
		int toReturn = -1;
		for (int i=0; i<player.length; i++){
			if(player[i]==con) {
				toReturn = i;
			}
		}
		return toReturn;
	}
}
