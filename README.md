Pong
====

The CI346 Pong Project.

##Running the Server
###TCP/IP
Run from the command line run ```java -jar Server.jar```

###Multicast
Run from the command line run ```java -jar Server.jar multiplex_mode```

##Running the Client
As a player just double click the jar or run ``` java -jar Client.jar ```

###Spectator
Run from the command line run ```java -jar Client.jar [GameNumber] ``` e.g. ``` java -jar Client.jar 0```

##Quick Game
Run the batch file ```pong_run.bat```

This will start the server and then run two clients.

##Quick Multicast Game
Run the batch file ```pong_multicast_run.bat``` 

The will start a multicast server and then start two clients and one spectator watching the game.