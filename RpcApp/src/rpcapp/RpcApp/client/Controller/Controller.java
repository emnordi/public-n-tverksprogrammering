package rpcapp.RpcApp.client.Controller;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.concurrent.CompletableFuture;
import rpcapp.RpcApp.client.Net.ClientCon;

public class Controller {

    private final ClientCon connection = new ClientCon();

    public void connect() {
        //Submit task of establishing a connection to threadpool.
        CompletableFuture.runAsync(() -> {
            try {
                connection.start();
            } catch (IOException ioe) {
                throw new UncheckedIOException(ioe);
            }
        });
    }

    /* 
    * Calls for the user to disconnect from the server and quit application. 
     */
    public void disconnect() throws IOException {
        connection.disconnect();

    }

    /*
    * Submit task to threadpool of sending an entry to server.
     */
    public void putEntry(String entry) {
        CompletableFuture.runAsync(() -> connection.putEntry(entry));
    }

}
