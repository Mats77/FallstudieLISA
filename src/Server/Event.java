package Server;

public abstract class Event {
	
	private static String event = "";

	public static String rollEvent(Player player){
		//Erstellen von 100 Zufallsmöglichkeiten. Die Fälle 0-09 werden mit Events belegt, wobei jedem Event eine 1% Chance zu Grunde liegt.
		//Die Möglichkeiten 10-99 sind unbelegt, da in 90% der Fälle kein Event beim Spieler auftreten soll.
		int random = (int) (Math.random() * 100);
		
		switch(random) {
			case 0:
				//Event: Die Belegschaft tritt in Streik: Die aktuelle Produktion des Spielers sinkt um 2%.
				event = "Die Belegschaft tritt in Streik: Die aktuelle Produktion sinkt um 2%";
				int capacity = player.getData().elementAt(player.getData().size()).getCapacity();
				capacity = capacity - (capacity/50);
				player.getData().elementAt(player.getData().size()).setCapacity(capacity);
				break;
			
			case 1:
				//Event: Sie müssen Aufwendungen für Reparaturen an Produktionsmaschinen zahlen
				event = "Sie müssen Aufwendungen für Reparaturen an Produktionsmaschinen zahlen";
				double cash = player.getData().elementAt(player.getData().size()).getCash();
				cash = cash - (cash/20);
				player.getData().elementAt(player.getData().size()).setCash(cash);
				break;
			
			case 2:
				//Event: Zahlungsbonus für zufriedene Kunden 
				event = "Zahlungsbonus für zufriedene Kunden";
				double cash2 = player.getData().elementAt(player.getData().size()).getCash();
				cash2 = cash2 + (cash2/20);
				player.getData().elementAt(player.getData().size()).setCash(cash2);
				break;
			
			case 3:
				//Event: Turbulenzen setzen einem Flugzeug zu // Turbulenzen können ihren Flugzeugen nichts anhaben <-je nach Qualität der Materialien
				//Falls Materialqualität gering, sinkt der Ruf des Spielers. Andernfalls steigt der Ruf
					if(player.getData().elementAt(player.getData().size()).getQualityOfMaterial() == 1 ) {
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
				double varCost = player.getData().elementAt(player.getData().size()).getVarCosts();
				varCost = varCost + varCost / 10 ;
				player.getData().elementAt(player.getData().size()).setVarCosts(varCost);
				break;
			case 5:
				//Event: Ein tragischer Terroranschlag auf dein Unternehmen fand statt.
				event = "Ein tragischer Terroranschlag auf dein Unternehmen fand statt. Du setzt eine Runde aus!";
				player.setReadyForNextRound(true);
				break;
			case 6:
				//Event: Ihre Materialien sind aufgrund schlechter Lagerhaltung in ihrer Qualität gesunken
				event = "Ihre Materialien sind aufgrund schlechter Lagerhaltung in ihrer Qualität gesunken";
				int qualityOfMaterial2 = player.getData().elementAt(player.getData().size()).getQualityOfMaterial(); 
				if(qualityOfMaterial2 >0) qualityOfMaterial2--;
				player.getData().elementAt(player.getData().size()).setQualityOfMaterial(qualityOfMaterial2);
				break;
			case 7:
				//Event: Sie entdecken bei Ihren Forschungen ein besseres Material für Ihre Flugzeuge!
				event = "Sie entdecken bei Ihren Forschungen ein besseres Material für Ihre Flugzeuge!"; 
				int qualityOfMaterial = player.getData().elementAt(player.getData().size()).getQualityOfMaterial();
				if(qualityOfMaterial <= 2 ) qualityOfMaterial++;
				player.getData().elementAt(player.getData().size()).setQualityOfMaterial(qualityOfMaterial);
				break;
			case 8:
				//Event: Ihr Catering-Service kaufte bei ALDI ein. Aufgrund einer Lebensmittelvergiftung fallen 1 Prozent ihrer Mitarbeiter aus
				event = "Ihr Catering-Service kaufte bei ALDI ein. Aufgrund einer Lebensmittelvergiftung fallen 1 Prozent ihrer Mitarbeiter aus";
				int capacity2 = player.getData().elementAt(player.getData().size()).getCapacity();
				capacity2 = capacity2 - (capacity2/50);
				player.getData().elementAt(player.getData().size()).setCapacity(capacity2);
				break;
			case 9:
				//Event: Eines Ihrer Flugzeuge wurde entführt. Tracking-Systeme waren wohl nicht notwendig, was?
				event = "Eines Ihrer Flugzeuge wurde entführt. Tracking-Systeme waren wohl nicht notwendig, was?";
				int reliability = player.getReliability();
				reliability--;
				player.setReliability(reliability);
				break;
			default:
				event = null;	
		}
		return event;
	}
}
