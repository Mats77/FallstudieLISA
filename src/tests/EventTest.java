package tests;

import static org.junit.Assert.*;

import java.net.Socket;
import java.util.Vector;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import Server.Conn;
import Server.Handler;
import Server.Mechanics;
import Server.Player;
import Server.PlayerDataCalculator;

public class EventTest {
	
	Handler handler = new Handler(3);
	Mechanics mechanics = new Mechanics(handler);
	PlayerDataCalculator pdc = new PlayerDataCalculator(mechanics);
	Vector<Conn> connections= new Vector<Conn>();
	
	String event = "";
	int random;
	
	@Before
	public void testGeneratePlayers() {
		connections.add(new Conn(new Socket(), handler));
		connections.get(0).setNick("Mats1");
		connections.get(0).setId(0);
		connections.add(new Conn(new Socket(), handler));
		connections.get(1).setId(1);
		connections.get(1).setNick("Mats2");
		connections.add(new Conn(new Socket(), handler));
		connections.get(2).setId(2);
		connections.get(2).setNick("Mats3");
		connections.add(new Conn(new Socket(), handler));
		connections.get(3).setId(3);
		connections.get(3).setNick("Mats4");
				
		mechanics.generatePlayers(connections);
	}
	
	@Test
	public void testRollEvent() {
			//Erstellen von 100 Zufallsmöglichkeiten. Die Fälle 0-09 werden mit Events belegt, wobei jedem Event eine 1% Chance zu Grunde liegt.
			//Die Möglichkeiten 0-99 sind unbelegt, da in 90% der Fälle kein Event beim Spieler auftreten soll.
			//Player player = mechanics.getPlayers()[0];
			Player player = mechanics.getPlayers()[0];
			for(int i = 0 ; i<99; i++) {
			random = i;
			switch(random) {
				case 0:
					//Event: Die Belegschaft tritt in Streik: Die aktuelle Produktion des Spielers sinkt um 2%.
					event = "Die Belegschaft tritt in Streik: Die aktuelle Produktion sinkt um 2%";
					//int capacity = player.getData().elementAt(player.getData().size()).getCapacity();
					int capacity = mechanics.getPlayers()[0].getData().lastElement().getCapacity();
					capacity = capacity - (capacity/50);
					//player.getData().elementAt(player.getData().size()).setCapacity(capacity);
					mechanics.getPlayers()[0].getData().lastElement().setCapacity(capacity);
					break;
				
				case 1:
					//Event: Sie müssen Aufwendungen für Reparaturen an Produktionsmaschinen zahlen
					event = "Sie müssen Aufwendungen für Reparaturen an Produktionsmaschinen zahlen";
					//double cash = player.getData().elementAt(player.getData().size()).getCash();
					double cash = mechanics.getPlayers()[0].getData().lastElement().getCash();
					cash = cash - (cash/20);
					//player.getData().elementAt(player.getData().size()).setCash(cash);
					mechanics.getPlayers()[0].getData().lastElement().setCash(cash);
					break;
				
				case 2:
					//Event: Zahlungsbonus für zufriedene Kunden 
					event = "Zahlungsbonus für zufriedene Kunden";
					//double cash2 = player.getData().elementAt(player.getData().size()).getCash();
					double cash2 = mechanics.getPlayers()[0].getData().lastElement().getCash();
					cash2 = cash2 + (cash2/20);
					//player.getData().elementAt(player.getData().size()).setCash(cash2);
					mechanics.getPlayers()[0].getData().lastElement().setCash(cash2);
					break;
				
				case 3:
					//Event: Turbulenzen setzen einem Flugzeug zu // Turbulenzen können ihren Flugzeugen nichts anhaben <-je nach Qualität der Materialien
					//Falls Materialqualität gering, sinkt der Ruf des Spielers. Andernfalls steigt der Ruf
						if(mechanics.getPlayers()[0].getData().lastElement().getQualityOfMaterial() == 1 ) {
							event = "Turbulenzen setzen einem Flugzeug zu. Ihr Ruf sinkt";
							int reliability = player.getReliability();
							reliability--;
							player.setReliability(reliability);
						}else{
							event = "Turbulenzen können ihren Flugzeugen nichts anhaben. Ihr Ruf steigt";
							int reliability = player.getReliability();
							reliability++;
							player.setReliability(reliability);
						}
					break;
				case 4:
					//Event: Rückstellungen müssen für gestiegene Rohstoffpreise gebildet werden
					event = "Rückstellungen müssen für gestiegene Rohstoffpreise gebildet werden";
					//double varCost = player.getData().elementAt(player.getData().size()).getVarCosts();
					double varCost = mechanics.getPlayers()[0].getData().lastElement().getVarCosts();
					varCost = varCost + varCost / 10 ;
					//player.getData().elementAt(player.getData().size()).setVarCosts(varCost);
					mechanics.getPlayers()[0].getData().lastElement().setVarCosts(varCost);
					break;
				case 5:
					//Event: Ein tragischer Terroranschlag auf dein Unternehmen fand statt.
					event = "Ein tragischer Terroranschlag auf dein Unternehmen fand statt. Du setzt eine Runde aus!";
					player.setReadyForNextRound(true);
					break;
				case 6:
					//Event: Ihre Materialien sind aufgrund schlechter Lagerhaltung in ihrer Qualität gesunken
					event = "Ihre Materialien sind aufgrund schlechter Lagerhaltung in ihrer Qualität gesunken";
					//int qualityOfMaterial2 = player.getData().elementAt(player.getData().size()).getQualityOfMaterial(); 
					int qualityOfMaterial2 = mechanics.getPlayers()[0].getData().lastElement().getQualityOfMaterial();
					if(qualityOfMaterial2 >0) qualityOfMaterial2--;
					//player.getData().elementAt(player.getData().size()).setQualityOfMaterial(qualityOfMaterial2);
					mechanics.getPlayers()[0].getData().lastElement().setQualityOfMaterial(qualityOfMaterial2);
					break;
				case 7:
					//Event: Sie entdecken bei Ihren Forschungen ein besseres Material für Ihre Flugzeuge!
					event = "Sie entdecken bei Ihren Forschungen ein besseres Material für Ihre Flugzeuge!"; 
					//int qualityOfMaterial = player.getData().elementAt(player.getData().size()).getQualityOfMaterial();
					int qualityOfMaterial = mechanics.getPlayers()[0].getData().lastElement().getQualityOfMaterial();
					if(qualityOfMaterial <= 2 ) qualityOfMaterial++;
					//player.getData().elementAt(player.getData().size()).setQualityOfMaterial(qualityOfMaterial);
					mechanics.getPlayers()[0].getData().lastElement().setQualityOfMaterial(qualityOfMaterial);
					break;
				case 8:
					//Event: Ihr Catering-Service kaufte bei ALDI ein. Aufgrund einer Lebensmittelvergiftung fallen 1 Prozent ihrer Mitarbeiter aus
					event = "Ihr Catering-Service kaufte bei ALDI ein. Aufgrund einer Lebensmittelvergiftung fallen 1 Prozent ihrer Mitarbeiter aus";
					//int capacity2 = player.getData().elementAt(player.getData().size()).getCapacity();
					int capacity2 = mechanics.getPlayers()[0].getData().lastElement().getQualityOfMaterial();
					capacity2 = capacity2 - (capacity2/50);
					//player.getData().elementAt(player.getData().size()).setCapacity(capacity2);
					mechanics.getPlayers()[0].getData().lastElement().setCapacity(capacity2);
					break;
				case 9:
					//Event: Eines Ihrer Flugzeuge wurde entführt. Tracking-Systeme waren wohl nicht notwendig, was?
					event = "Eines Ihrer Flugzeuge wurde entführt. Tracking-Systeme waren wohl nicht notwendig, was?";
					int reliability = player.getReliability();
					reliability--;
					player.setReliability(reliability);
					break;
				default:
					event = "";
					break;
				}
				
					if(random == 0) assertEquals(event,"Die Belegschaft tritt in Streik: Die aktuelle Produktion sinkt um 2%" );
					if(random == 1) assertEquals(event,"Sie müssen Aufwendungen für Reparaturen an Produktionsmaschinen zahlen" );
					if(random == 2) assertEquals(event,"Zahlungsbonus für zufriedene Kunden" );
					if(random == 3) {
						if(mechanics.getPlayers()[0].getData().lastElement().getQualityOfMaterial() == 1 ) {
						assertEquals(event,"Turbulenzen setzen einem Flugzeug zu. Ihr Ruf sinkt" );
						}
						else{
							assertEquals(event,"Turbulenzen können ihren Flugzeugen nichts anhaben. Ihr Ruf steigt");
						}
					}
					if(random == 4) assertEquals(event,"Rückstellungen müssen für gestiegene Rohstoffpreise gebildet werden" );
					if(random == 5) assertEquals(event,"Ein tragischer Terroranschlag auf dein Unternehmen fand statt. Du setzt eine Runde aus!" );
					if(random == 6) assertEquals(event,"Ihre Materialien sind aufgrund schlechter Lagerhaltung in ihrer Qualität gesunken" );
					if(random == 7) assertEquals(event,"Sie entdecken bei Ihren Forschungen ein besseres Material für Ihre Flugzeuge!" );
					if(random == 8) assertEquals(event,"Ihr Catering-Service kaufte bei ALDI ein. Aufgrund einer Lebensmittelvergiftung fallen 1 Prozent ihrer Mitarbeiter aus" );
					if(random == 9) assertEquals(event,"Eines Ihrer Flugzeuge wurde entführt. Tracking-Systeme waren wohl nicht notwendig, was?" );
					if(random >=10 && random <= 99) assertEquals(event,"" );
			
			
			}

	}
}
