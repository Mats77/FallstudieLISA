package Server;

import java.util.Vector;

public class Report {
	private Player player [];
	private double totalProduction=0;
	private double totalResearch=0;
	private double totalTurnover=0;
	private double totalFixCosts=0;
	private double totalVarCosts=0;
	
	public Report(Player [] player){
		this.player = player;
	}
	
	public void createReport(){
		
		//Berechnung der Gesamten Ausgaben von allen Playern.
		for (Player spieler : player) {
			Vector<PlayerData> data = spieler.getData();
			totalProduction+= data.lastElement().getProduction();
			for (PlayerData playerData : data) {
				totalResearch+= playerData.getProduction();
				totalTurnover+= playerData.getTurnover();
				totalFixCosts+= playerData.getFixCosts();
				totalVarCosts +=  playerData.getVarCosts();
			}
		}
	}

}
