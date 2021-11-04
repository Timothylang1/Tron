# Tron
Bike Tron game. Based on the movie.

Welcome to Tron, a fast paced multiplayer game where you control a bike and try to be the last person standing! Using the left and right arrow keys, you can turn your bike to avoid colliding with any objects, including your line that you create when you drive!

Setup: if you're attempting to play the game, then there is a couple of parts that need to be setup. First, determine the number of players playing, and which computer will be the server. Whoever controls the server computer (refered to as the host), go to the package TronLogic, and locate the file titled GameServer.java. Replace the TOTAL_PLAYERS variable with the total number of players you expect. The host must also give their IP address to everyone who wishes to participate. Everyone else must get the IP address, and locate the file GameCLient.java under the package TronLogic. At the line that states socket = new Socket("141.140.125.28", 7777); at line 23, replace the given IP address with the host's one.

To run the game, the host must run GameServer.java. When you see "ServerSocket awaiting connection..." then all the players can simply run Run.java. The game will not begin until all players are connected. Once all players are connected, you use the left and right arrow keys to change the direction of the bike. Avoid all obstacles at all costs! Upon death, you can still move the screen around to follow other players. It is also safe to close your window at any time, but problems may occur if the server disconnects unexpectedly.

The host can also be a player. To do so, simply duplicate the workspace, and run GameServer.java in one workspace, then run Run.java on another. Don't forget to change the IP address in GameClient.java to connect to the server on your computer!

A brief note: this game works really well if the server computer is connected to the router via ethernet. Because of the use of sockets, it is unpredictable whether you will be able to connect or not depending on how the router and wifi is setup at your location. Best performace occurs if everyone is connected via ethernet to the router, and if you use the most powerful computer as the host.

Good luck and have fun in Tron!
