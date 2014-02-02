<?php
include "basic.php";

/*****************************************
*   Faked die farbigen Tiles des 
*   Dashboards
*
*
*****************************************/


class BasicDashboard {

	public $title;
	public $icon;
	public $color;
	public $value;
	public $percent;

    function __construct($neuer_name, $ic, $bg) {
        $this->title = $neuer_name;
        $this->icon = $ic;
        $this->color = $bg;
        $this->value = "".mt_rand (5, 50000);
        $this->percent = "".mt_rand (1, 100);
    }

}

$money = new BasicDashboard("Cash","usd","green");
$share = new BasicDashboard("Market Share","globe","turquoise");
$capacity = new BasicDashboard("Capacity","wrench","gray");
$marketing = new BasicDashboard("Marketing","bullhorn","purple");
$rd = new BasicDashboard("R&D","flask","blue");
$revenue = new BasicDashboard("Earnings","money","green");
        //var_dump($money);

$result = array($money,$share,$capacity,$marketing,$rd,$revenue);

echo json_encode($result);

?>