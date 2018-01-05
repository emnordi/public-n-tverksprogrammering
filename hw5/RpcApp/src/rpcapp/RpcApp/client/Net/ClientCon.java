package rpcapp.RpcApp.client.Net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.io.UncheckedIOException;

/**
 * @author Emil
 */
public class ClientCon {

    private PrintWriter toServ;
    Socket socket;
    String toUser;

    /*
    * A connection is started on 127.0.0.1(localhost) on port 8000, and with a lingertime of 30000ms
    * toServ PrintWriter is created to forward inputs to the server. 
     */
    public void start() throws IOException {
        try {
            socket = new Socket();
            //Socket.connect{ip/port/setsolinger}
            socket.connect(new InetSocketAddress("127.0.0.1", 8000), 30000);
            socket.setSoTimeout(1800000);
            //What is in toServ is sent to server
            toServ = new PrintWriter(socket.getOutputStream(), true);
            System.out.println("Connected");
            System.out.println("Type 'start' to start or 'quit' to quit");
            //Get from server using fromServ
            BufferedReader fromServ = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            
            while ((toUser = fromServ.readLine()) != null) {
                System.out.println(toUser);
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /*
    *Forwards an entry to the server
     */
    public void putEntry(String entry) {
        toServ.println(entry);
    }

    /*
    * Closes connection with server.
     */
    public void disconnect() throws IOException {
        System.out.println("Im here clinecon");
        toServ.println("quit");
        socket.close();
        socket = null;
    }

}
