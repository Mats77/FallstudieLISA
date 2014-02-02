<?php

/*****************************************
*   Faked neue Aufträge
*
*
*
*****************************************/

class ROrder {

	public $id;
	public $airline;
	public $fixed;
	public $optional;
	public $duedate;

    function __construct() {
        $this->id = mt_rand(0, 10000);
        $this->airline = "".substr(md5(rand()), 0, 20);;
        $this->fixed = mt_rand(0, 50);
        $this->optional = mt_rand(0, 30);
        $this->dueDate = date("F j, Y");
    }



}

$c1 = new ROrder();
$c2 = new ROrder();
$c3 = new ROrder();
$c4 = new ROrder();
$c5 = new ROrder();
$c6 = new ROrder();
$c7 = new ROrder();

$result = array($c1,$c2,$c3,$c4,$c5,$c6,$c7);

echo json_encode($result);

?>

