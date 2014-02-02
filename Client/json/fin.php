<?php

/*****************************************
*   Faked die Finanzsektion des Spiels
*
*
*
*****************************************/

include "basic.php";

class FinDash {

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


class Loan {

    public $period;
    public $rate;
    public $sum;
    
    function __construct()
    {
        $this->sum = mt_rand(0, 100000);
        $this->rate = mt_rand(1, 30);
        $this->period = array_rand(array('long-term' => '0', 'short-term' => '1'));
    }
}

$a1 = new FinDash("variable costs","align-left","turquoise");
$a4 = new FinDash("costs per Airplane","plane","gray");
$a2 = new FinDash("overhead costs","align-justify","purple");
$a3 = new FinDash("cumulative costs","sort-alpha-asc","red");

$b1 = new FinDash("Cash","money","green");
$b4 = new FinDash("Earnings per Airplane","plane","gray");
$b2 = new FinDash("Revenue","repeat","blue");
$b3 = new FinDash("Earnings","usd","green");

for($i = 0; $i < 9; $i++) {
    $objs[$i] = new Loan;
   }

$result['costens'] = array($a1,$a4,$a2,$a3);
$result['moneys']  = array($b1,$b4,$b2,$b3);
$result['loans']  = $objs;


echo json_encode($result);

?>