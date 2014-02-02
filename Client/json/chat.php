<?php

/*****************************************
*   Faked Chatnachrichten zum Testen
*
*
*
*****************************************/

class Chat {

	public $direction;
	public $avatar;
	public $name;
	public $time;
	public $message;

    function __construct($dir, $img, $nm) {
        $this->direction = $dir;
        $this->avatar = $img;
        $this->name = $nm;
        $this->time = date("F j, Y, g:i a");
        $this->message = "".substr(md5(rand()), 0, 20);
    }

}

$c1 = new Chat("in","dehavilland.png","Player 1");
$c2 = new Chat("in","kamow.png","Player 2");
$c3 = new Chat("out","mig.gif","Player 3");
$c4 = new Chat("in","kamow.png","Player 2");
$c5 = new Chat("out","mig.gif","Player 3");

$result = array($c1,$c2,$c3,$c4,$c5);

echo json_encode($result);

?>