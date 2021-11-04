package TronLogic;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import java.util.Timer;
import java.util.TimerTask;

public class GameServer {

    private static final int TOTAL_PLAYERS = 1;
    private static final int FRAMERATE = 55;
    public static void main(String[] args) throws IOException {
        // Creates arraylist to access sockets, so it can send info to all sockets
        ArrayList<DataOutputStream> listofoutputsockets = new ArrayList<>();

        // Creates arraylist to send info
        ArrayList<String> tosend = new ArrayList<>();

        // don't need to specify a hostname, it will be the current machine
        ServerSocket ss = new ServerSocket(7777);
        System.out.println("ServerSocket awaiting connections...");

        for (int i = 0; i < TOTAL_PLAYERS; i++) {
            Socket socket = ss.accept(); // blocking call, this will wait until a connection is attempted on this port.
            System.out.println("Connection from " + socket + "!");
            // get the input stream from the connected socket
            InputStream inputStream = socket.getInputStream();
            // create a DataInputStream so we can read data from it.
            DataInputStream dataInputStream = new DataInputStream(inputStream);
            // gets a OutputStream
            OutputStream outputStream = socket.getOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(outputStream);

            // First thing it sends is the player number
            dataOutputStream.writeInt(i);

            // Second thing it sends is the total player count
            dataOutputStream.writeInt(TOTAL_PLAYERS);

            // Adds output steam to list so that all of the computers get the same output
            listofoutputsockets.add(dataOutputStream);

            // Creates a new thread for each socket to constantly check for input data
            Thread thread = new Thread() {
                public void run() {
                    // read the message from the socket
                    while (true) {
                        try {
                            String message = dataInputStream.readUTF();
                            tosend.add(message);
                        } catch (Exception e) {
                            destroy();
                            break;
                        }
                    }
                }

                private void destroy() {
                    try {
                        socket.close();
                    }
                    catch (Exception e) {
                        System.out.println(e);
                    }
                    // listofoutputsockets.remove(dataOutputStream);  // CHANGE (ADD)
                    System.out.println("Disconnected " + socket);
                }
            };
            thread.start();
        }
        System.out.println("All players connected. Total players = " + TOTAL_PLAYERS);

        // Creates an arraylist that helps remove sockets once they're disconnected // TO REMOVE IF NESSICARY
        ArrayList<DataOutputStream> toRemove = new ArrayList<>(); // CHANGE (REMOVE)

        // Creates a timer so it sends output is roughly FRAMERATE times a second
        int ms = 1000 / FRAMERATE;
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            public void run() {
                if (listofoutputsockets.size() != 0) {
                    String messagetosend = "";
                    int counter = 0;
                    // Create total message to send
                    for (String message : tosend) {
                        messagetosend += message;
                        counter++;
                    }
                    // Remove all the objects that were added
                    for (int i = 0; i < counter; i++) {
                        tosend.remove(0);
                    }
                    // Sends the same message to all of the sperate sockets
                    for (DataOutputStream sockettosend : listofoutputsockets) {
                        try {
                            sockettosend.writeUTF(messagetosend);
                        }
                        catch (Exception e) {
                            // If this exception occurs, then that means the socket has been closed, so we need to remove it from the list
                            toRemove.add(sockettosend); // CHANGE (REMOVE)
                        }
                    }
                    // REMOVE THIS IF IT DOESN'T WORK
                    for (DataOutputStream sockettoremove : toRemove) { // CHANGE (REMOVE)
                        listofoutputsockets.remove(sockettoremove);
                        System.out.println("Socket has been removed");
                    }
                    toRemove.clear();   // CHANGE (REMOVE)
                }
                // If there are no more sockets in the list, that means they were all removed, and therefore, end game
                else {
                    timer.cancel(); // Stops task from being run
                    try {
                        ss.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        timer.schedule(task, 0, ms);
        System.out.println("Begin game");
    }
}
