<?php

/*****************************************
*   Faked die 2te Reihe des Dashboards
*
*
*
*****************************************/

include "basic.php";

class Dash {

	public $title;
	public $icon;
	public $color;
	public $value;

    function __construct($neuer_name, $ic, $bg, $val) {
        $this->title = $neuer_name;
        $this->icon = $ic;
        $this->color = $bg;
        if ($val = "") {
            $this->value = "";
        } else {
            $this->value = "".mt_rand (5, 100);
        }
        
    }

}

$a1 = new Dash("Round","calendar","success");
$a2 = new Dash("Reliability","thumbs-up","success");
$a3 = new Dash("Active Orders","wrench","important");
$a4 = new Dash("Loans","credit-card","important");

        //var_dump($money);

$result = array($a1,$a2,$a3,$a4);

echo json_encode($result);

?>