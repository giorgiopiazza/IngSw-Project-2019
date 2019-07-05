Progetto Ingegneria del Software 2019

Componenti del gruppo
- Piazza Giorgio
- Piro Francesco
- Tosetti Lorenzo

Documentazione
UML
- UML Iniziali : https://github.com/giorgiopiazza/ing-sw-2019-27/tree/master/Deliveries/uml/initial
- UML Finali : https://github.com/giorgiopiazza/ing-sw-2019-27/tree/master/Deliveries/uml/final
JavaDoc
- Javadoc : https://github.com/giorgiopiazza/ing-sw-2019-27/tree/master/docs
Jars
- Jars : https://github.com/giorgiopiazza/ing-sw-2019-27/tree/master/Deliveries/jar
Screenshot Sonarqube 
- Screenshots : https://github.com/giorgiopiazza/ing-sw-2019-27/tree/master/Deliveries/sonar

Funzionalità
Funzionalità sviluppate
- Regole Complete
- CLI
- GUI
- Socket
- RMI

Funzionalità aggiuntive sviluppate
- Terminator
- Persistenza

Esecuzione dei JAR
Client
Il client può essere eseguito utilzzando la modalità CLI e la modalità GUI
CLI
E' necessario eseguire il client con un terminale che supporti UTF-8 e gli ANSI escape codes per una migliore esperienza.
Il comando per eseguire il client in modalità CLI è il seguente.

java -jar client.jar cli

GUI
Per eseguire il client in modalità GUI è necessario importare le dipende di JavaFX. 
Posizionare la cartella estratta dell'SDK di JavaFX del proprio sistema operativo e eseguire il seguente comando.
L'SDK può essere scaricato da https://gluonhq.com/products/javafx/.

java --module-path javafx-sdk-11.0.2/lib --add-modules javafx.controls --add-modules javafx.fxml -jar client.jar

Server
Per eseguire il server è necessario creare un file di configurazione.
Un esempio è il seguente.

{
  "start_time": 10,
  "move_time": 180,
  "socket_port": 2727,
  "rmi_port": 7272
}

Options
- `start_time`: tempo di attesa prima che la partita inizi una volta aver raggiunto 3 giocatori; 
- `move_time`: tempo di risposta tra un messaggio e l'altro prima che il server espella il giocatore;
- `socket_port`: porta del server Socket;
- `rmi_port`: porta del server RMI. 

L'esecuzione del server avviene attraverso il seguente comando.

java -jar server.jar [-l configFilePath] [-b true/false] [-s numSkulls] [-r]

Options
- `-l configFilePath`: permette di specificare il percorso del file di configurazione. Se non specificato il valore di default è "conf.json";
- `-b true/false`: permette di aggiungere il bot alla partita. Se non specificato il valore di default è false;
- `-s numSkulls`: permette di specificare con quanti teschi giocare la partita. Se non specificato il valore di default è 5;
- `-r`: permette di caricare una partita precedentemente salvata. 