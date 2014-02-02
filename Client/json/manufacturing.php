<?php

/*****************************************
*   Faked Produktionsaufträge
*
*
*
*****************************************/

class ManuJob {

	public $id;
	public $airline;
	public $fixed;
	public $optional;
	public $but;
	public $label;
	public $duration;
	public $earnings;

    function __construct() {
        $this->id = mt_rand(0, 10000);
        $this->airline = "".substr(md5(rand()), 0, 20);;
        $this->fixed = mt_rand(0, 50);
        $this->optional = mt_rand(0, 30);
        $this->dueDate = @date("d.m.Y", mt_rand( @strtotime("today"), @strtotime("Dec 20 2015") ));
        $this->but = array_rand(array(true, false));
        $this->label = array_rand(array('success' => '0', 'info' => '1',  'warning' => '2', 'important' => '3' ));
        $this->duration = mt_rand(1, 8)." Periods";
        $this->earnings = mt_rand(20, 400);
    }
}

for($i = 0; $i < 20; $i++) {
    $objs[$i] = new ManuJob;
   }

$result = $objs;
echo json_encode($result);

?> 