package Server;

import java.util.Vector;

public class Handler {

	private Vector<Conn> connections = new Vector<Conn>();
	private Mechanics mechanics;
	
	//Konstruktor, erstellt direkt Mechanics
	public Handler()
	{
		mechanics = new Mechanics(this);
	}
	
	//reiht das vom Server übergeben Conn objekt in das Array connections ein
	public void addPlayer(Conn player) {
		connections.add(player);
	}
	
	
	//veranlasst das senden einer Nachricht an alle Clients
	public void spread(String txt) { //sendet an alle
		for (Conn con : connections) {
			con.send(txt);
		}
	}
	
	//überprüft, was der Client gesendet hat und veranlasst Reaktion
	public void handleString(String txt, Conn sender) {
		
		//Chat Nachricht an alle weiterleiten
		if (txt.startsWith("CHAT ")) {
			String s = "CHAT " +  getID(sender) + " "
					+ sender.getNick() + ": " + txt.substring(5);
			spread(s);
			
		//Einer der Spieler möchte das Spiel Starten, wenn alle Ready sind, erstellt mechanics für jede
		//Conn ein Playerobjekt
		} else if(txt.startsWith("READY ")) {
			sender.setReady(true);
			if(areAllReady()){
				mechanics.startGame(connections);
				String s = "ALLREADY ";
				spread(s);
			}
			
		//Ein Client fragt einen Nickname an
		} else if (txt.startsWith("ASKFORNICK")) {
			sender.setNick(txt.substring(11));
			
		//Ein Client hat seine Rundenwerte abgegeben
		} else if (txt.startsWith("VALUES")){
			mechanics.valuesInserted(txt.substring(7), sender.getNick());
		}
	}
	
	public int getID(Conn connection) {
		int toReturn = -1;
		for (Conn con : connections) {
			if (connection == con) toReturn = (int)con.getId();
		}
		return toReturn;
	}
	
	public boolean areAllReady() {
		boolean toReturn = true;
		for (Conn con : connections) {
			if(!con.isReady()) toReturn = false;
		}
		
		return toReturn;
	}

	public void newRoundStarted() {
		spread("NEWROUND");
	}
}
