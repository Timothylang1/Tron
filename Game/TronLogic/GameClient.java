package TronLogic;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class GameClient {

    // Output, input, and socket
    private DataOutputStream dataOutputStream;
    private DataInputStream dataInputStream;
    private Socket socket;

    // Player number
    private int playerNum, totalPlayers;

    public GameClient() throws IOException {

        // need host and port, we want to connect to the ServerSocket at port 7777
        socket = new Socket("141.140.125.28", 7777); // Host is ip address, if set to "localhost", I'm assuming it automatically sets it to closest IP address?
        System.out.println("Connected!");

        // get the output stream from the socket.
        OutputStream outputStream = socket.getOutputStream();
        // create a data output stream from the output stream so we can send data through it
        dataOutputStream = new DataOutputStream(outputStream);

        InputStream inputStream = socket.getInputStream();
        dataInputStream = new DataInputStream(inputStream);

        // First thing the server will send after connecting is the player number, starting from 0
        playerNum = dataInputStream.readInt();

        // Second thing the server sends is the total number of expected players
        totalPlayers = dataInputStream.readInt();
    }

    public String getReceived() {
        try {
            return dataInputStream.readUTF();
        }
        catch (Exception e) {
            // Will break code because the mainloop will try and convert this to a number
            return "end";
        }
    }

    public void tosend(String send) {
        try {
            dataOutputStream.writeUTF(send);
        }
        catch (Exception e) {
            System.out.println(e);
        }
    }

    public int getPlayerNum() {
        return playerNum;
    }

    public int getTotalPlayers() {
        return totalPlayers;
    }
}
