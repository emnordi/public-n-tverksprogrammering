package rpcapp.RpcApp.client.View;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;
import java.util.Scanner;
import rpcapp.RpcApp.client.Net.ClientCon;
import rpcapp.RpcApp.client.Net.Listener;
/**
 * Reads commands inputed by the user and runs a separate thread.
 */
public class View implements Runnable {

    private boolean getCommand = false;
    private ClientCon server;
    public boolean cansend;
    

    /*
    * Starts reader and waits for user input. 
    */
    public void start() {
        if (getCommand) {
            return;
        }
        getCommand = true;
        cansend = true;
        server = new ClientCon();
        new Thread(this).start();
    }

    /*
    * Checks whether user wants to start the application or quit, other inputs are treated
    * as a selection.
     */
    @Override
    public void run() {
        server.connect(new InetSocketAddress("127.0.0.1", 8000));
        server.addListener(new ConsoleOutput());
        while (getCommand) {
            Scanner sc = new Scanner(System.in);
            String s = sc.nextLine();
            if(!cansend && !s.equals("quit")){
                toClient("You cannot write anyting else right now (except quit)");
                continue;
            }
            switch (s) {
                case "start":
                    System.out.println("Game started");
                    server.putEntry("start");
                    break;
                case "quit":
                    server.putEntry("quit");
                    toClient("Good Bye!");
                    getCommand = false;
                    try {
                        server.disconnect();
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                default:
                    server.putEntry(s);
                    break;
            }
        }
    }
    
        
        synchronized void toClient(String output) {
            System.out.println(output);
        }
        
        private class ConsoleOutput implements Listener {
        @Override
        public void recvdMsg(String msg) {
            toClient(msg);
        }

        @Override
        public void disconnected() {
            printToConsole("Disconnected from server.");
        }

        private void printToConsole(String output) {
            toClient(output);
        }
    }
    
}
