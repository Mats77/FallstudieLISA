<?php

/*****************************************
*   Faked den Handler zum Testen der
*	Funktionen
*
*
*****************************************/


$get = $_GET["reason"];
$payload = $_GET["payload"];

switch ($get) {
	case 'AUTHORIZEME':
		echo "AUTHORIZED 4 32"; //OK, PlayerID, GameID
		break;

	case 'CHATSEND':
		echo "OK ".$payload;
		break;

	case 'STARTGAME':
		echo "OK ".$payload;
		break;

	case 'BASICDASHBOARD':
		include('dashboardstats.php');
		break;
		
	case 'BASICDASHBOARD2':
		include('dash2.php');
		break;

	case 'CHATREFRESH':
		include('chat.php');
		break;

	case 'RECENTORDERS':
		include('recentorders.php');
		break;

	case 'ORDERACTION':
		echo "Aktion angekommen ".$payload;
		break;

	case 'PIPELINE':
		include('manufacturing.php');
		break;

	case 'manufactureJob':
		echo "Job angenommen ".$payload;
		break;

	case 'Financials':
		include('fin.php');
		break;

	default:
		# code...
		break;
}







?>