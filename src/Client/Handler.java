package Client;

import java.awt.Color;



public class Handler {
	
	private Conn conn;
	private GUI_Main gui;
	private String[] players ;
	private boolean active = false;
	String[][] stat= new String[5][];
	int []stat_max = new int[5];
	
	
	public Handler ( GUI_Main gui){
		this.gui = gui;
	}
	
	public String getNick(int id){
		return players[id].substring(2);
	}
	
	public String getPlayer(int id){
		return players[id];
	}

	public void setConn(Conn conn){
		this.conn = conn;
	}
	
	public Conn getConn(){
		return this.conn;
	}

	public void sendChat(String txt){	//bisher nur Lobby, evtl. auch in-game Chat
		conn.send("CHAT " + txt);
	}
	
	public void sendReady(){	//übermittelt dem server, dass der Spieler rdy ist und button in der Lobby gedrückt hat
		conn.send("READY ");
	}
	
	public void handleString(String txt){
		if(txt.startsWith("CONNECTED")){	//verändert das GUI
			gui.connected();
		} else if(txt.startsWith("CHAT"))	
		{
			try{
				((GUI_Lobby) gui.getCurrentFrame()).setChat(txt.substring(7));
			} catch (Exception e){
				
			}
		} else if (txt.startsWith("ALLREADY")) {	//startet Spiel
			gui.gamestarted();
		} else if (txt.startsWith("ASKFORNICK")) {	// sendet nick
			conn.send(conn.getNick());
		}
	}
}

