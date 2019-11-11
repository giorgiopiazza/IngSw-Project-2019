# Prova Finale di Ingegneria del Software - a.a. 2019-2020
![alt text](https://cf.geekdo-images.com/opengraph/img/ac8k6cuJA8cf6jnRlPjzkDd_YuQ=/fit-in/1200x630/pic3476604.jpg)

Scopo del progetto è quello di implementare il gioco da tavola [Adrenalina](https://czechgames.com/en/adrenaline/) seguendo il pattern architetturale Model View Controller per la realizzazione del modello secondo il paradigma di programmazione orientato agli oggetti. Il risultato finale copre completamente le regole definite dal gioco e permette di interagirci sia con una interfaccia da linea di comando (CLI) che grafica (GUI), la rete è stata gestita sia con il tradizionale approccio delle socket che con una tecnologia specifica di Java (RMI).

## Documentazione
La seguente documentazione comprende i documenti realizzati per la progettazione del problema, verranno prima elencati i diagrammi delle classi in UML poi la documentazione del codice (javadoc).

### UML
I seguenti diagrammi delle classi rappresentano il primo, il modello secondo il quale il gioco dovrebbe essere stato implementato, il secondo contiene invece i diagrammi del prodotto finale nelle parti critiche riscontrate.
- [UML Iniziali](https://github.com/giorgiopiazza/ing-sw-2019-27/tree/master/Deliveries/uml/initial)
- [UML Finali](https://github.com/giorgiopiazza/ing-sw-2019-27/tree/master/Deliveries/uml/final)

### JavaDoc
La seguente documentazione include una descrizione per la maggiore parte delle classi e dei metodi utilizzati, segue le tecniche di documentazione di Java e può essere consultata al seguente indirizzo: [Javadoc](https://github.com/giorgiopiazza/ing-sw-2019-27/tree/master/docs)

### Librerie e Plugins
|Libreria/Plugin|Descrizione|
|---------------|-----------|
|__maven__|strumento di gestione per software basati su Java e build automation|
|__junit__|framework dedicato a Java per unit testing|
|__jacoco__|strumento di supporto al testing per evidenziare le linne di codice coperte dagli unit test|
|__mockito__|strumento di supporto al unit testing per realizzare oggetti "dummy"|
|__gson__|libreria per il supporto al parsing di file in formato json|
|__JavaFx__|libreria grafica di Java|

### Jars
I seguenti jar sono stati utilizzati per la consegna del progetto, permettono quindi il lancio del gioco secondo le funzionalità descritte nell'introduzione. Le funzionalità realizzate secondo la specifica del progetto sono elencate nella prossima sezione mentre i dettagli per come lanciare il sistema saranno definiti nella sezione chiamata __Esecuzione dei jar__. La cartella in cui si trovano il software del client e del server si trova al seguente indirizzo: [Jars](https://github.com/giorgiopiazza/ing-sw-2019-27/tree/master/Deliveries/jar).

## Funzionalità
### Funzionalità Sviluppate
- Regole Complete
- CLI
- GUI
- Socket
- RMI

### Funzionalità Sggiuntive Sviluppate
- Terminator
- Persistenza

## Esecuzione dei JAR
### Client
Il client viene eseguito scegliendo l'interfaccia con cui giocare, le possibili scelte sono da linea di comando o interfaccia grafica. Le seguenti sezioni descrivono come eseguire il client in un modo o nell'altro.
#### CLI
Per una migliore esperienza di gioco da linea di comando è necessario lanciare il client con un terminale che supporti la codifica UTF-8 e gli ANSI escape. 
Per lanciare il client in modalità CLI digitare il seguente comando:
```
java -jar client.jar cli
```
#### GUI
Per poter lanciare il client con l'interfaccia grafica è necessario importare le dipendenze di JavaFx. Per poter lanciare la modalità GUI è quindi necessario: scaricare l'SDK relativo al proprio sistema operativo da https://gluonhq.com/products/javafx/ e posizionare la relativa cartella estratta nella stessa posizione del client.jar.

A questo punto si deve digitare il seguente comando che importa le dipendenze e lancia il client:
```
java --module-path javafx-sdk-11.0.2/lib --add-modules javafx.controls --add-modules javafx.fxml -jar client.jar
```

### Server
Per eseguire il server è solamente necessario configurare alcune delle sue caratteristiche attraverso un file di configurazione in formato json.
Un esempio di questo file, di cui poi verranno spiegati i campi, è il seguente:
```
{
  "start_time": 10,
  "move_time": 180,
  "socket_port": 2727,
  "rmi_port": 7272
}
```
#### Options
- `start_time`: tempo di attesa prima che la partita inizi una volta aver raggiunto il numero minimo di 3 giocatori; 
- `move_time`: tempo di cui dispone ciascun giocatore per eseguire un'azione, se viene superato il giocatore è espulso dalla partita;
- `socket_port`: porta del server che usa le socket;
- `rmi_port`: porta del server che usa il servizio RMI. 

L'esecuzione del server avviene quindi attraverso il seguente comando, di cui verranno poi definiti i parametri:
```
java -jar server.jar [-l configFilePath] [-b true/false] [-s numSkulls] [-r]
```
#### Parameters
- `-l configFilePath`: permette di specificare il percorso del file di configurazione. Se non specificato il valore di default è __conf.json__;
- `-b true/false`: permette di aggiungere il terminator alla partita. Se non specificato il valore di default è false;
- `-s numSkulls`: permette di specificare con quanti teschi giocare la partita. Se non specificato il valore di default è 5;
- `-r`: permette di caricare una partita precedentemente salvata il cui file di salvataggio dovrà essere posizionato nella stessa posizione del server.jar.

## Componenti del gruppo
- [__Giorgio Piazza__](https://github.com/giorgiopiazza)
- [__Francesco Piro__](https://github.com/Megapiro)
- [__Lorenzo Tosetti__](https://github.com/tosettil-polimi)
