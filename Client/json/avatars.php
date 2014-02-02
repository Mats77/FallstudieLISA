<?php

/*****************************************
*	Liefert die Avatar-Bilder aus 
*	dem Verzeichnis per JSON an die App
*	um die App ein wenig dynamischer zu 
* 	machen
*****************************************/


include "basic.php";



// Öffnet ein Unterverzeichnis
$pfadJSON = "/lisa/img/avatars/";
$verzeichnis = openDir("../img/avatars");
// Verzeichnis lesen
while ($file = readDir($verzeichnis)) {
 // Höhere Verzeichnisse nicht anzeigen!
 if ($file != "." && $file != ".." && $file != "@eaDir") {
 // Link erstellen
  //echo $file."\n";
    $filename = preg_replace("/\\.[^.\\s]{3,4}$/", "", $file);
  $avatars['image'][] =  $pfadJSON.$file;
 }
}
$json = json_encode($avatars);
header('Content-type: application/json');
echo indent($json);
 // Verzeichnis schließen
closeDir($verzeichnis);
?> 