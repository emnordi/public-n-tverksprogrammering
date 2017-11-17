package rpcapp.RpcApp.client.View;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Scanner;
import rpcapp.RpcApp.client.Controller.Controller;

/**
 * Reads commands inputed by the user and runs a separate thread.
 */
public class View implements Runnable {

    private boolean getCommand = false;
    private Controller contr;

    /*
    * Starts reader and waits for user input. 
    */
    public void start() {
        if (getCommand) {
            return;
        }
        getCommand = true;
        contr = new Controller();
        new Thread(this).start();
    }

    /*
    * Checks whether user wants to start the application or quit, other inputs are treated
    * as a selection.
     */
    @Override
    public void run() {
        contr.connect();
        while (getCommand) {
            Scanner sc = new Scanner(System.in);
            String s = sc.nextLine();
            switch (s) {
                case "start":
                    System.out.println("Game started");
                    contr.putEntry("start");
                    break;
                case "quit":
                    contr.putEntry("quit");
                    System.out.println("Good Bye!");
                    getCommand = false;
                    try {
                        contr.disconnect();
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                default:
                    contr.putEntry(s);
                    break;
            }
        }
    }
}
