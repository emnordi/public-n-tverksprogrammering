package rpcapp.RpcApp.server.net;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.logging.Level;
import java.util.logging.Logger;
import rpcapp.RpcApp.server.model.RpcLogic;

/**
 * Handles all the in- and outputs for an individual client.
 */
public class UserHandle implements Runnable {

    private final Server server;
    public boolean gamestarted = false;
    private boolean firsttime = true;
    private int roundscore = 0;
    private int totalscore = 0;
    RpcLogic logic = new RpcLogic();
    private final SocketChannel channel;
    private final ByteBuffer fromClient = ByteBuffer.allocateDirect(1024);
    private String ts;
    private String rs;
    private String entry;
    private boolean started;
    String myPick;

    UserHandle(Server server, SocketChannel channel) throws IOException {
        this.server = server;
        this.channel = channel;
        firsttime = true;
        started = false;
    }

    /*
    * Handle all the inputs of the user and also what will be triggered when input
     */
    @Override
    public void run() {

        if (server.countPlayers() > 1 && started) {
            try {
                switch (entry) {
                    case "rock":
                        display("picked rock");
                        myPick = "rock";
                        synchronized (server) {
                            server.addSelect();
                            server.addPicks("rock");
                        }
                        break;
                    case "paper":
                        display("picked paper");
                        myPick = "paper";
                        synchronized (server) {
                            server.addSelect();
                            server.addPicks("paper");
                        }
                        break;
                    case "scissors":
                        display("picked scissors");
                        myPick = "scissors";
                        synchronized (server) {
                            server.addSelect();
                            server.addPicks("scissors");
                        }
                        break;
                    case "quit":
                        disconnectClient();
                        break;
                    default:
                        display("Invalid input; Try again.");
                        break;
                }
            } catch (IOException e) {
                System.err.println("Connection lost");

            }

        } else {
            try {
                switch (entry) {
                    case "start":
                        synchronized (server) {
                            server.addPlayers();
                            server.addChannel(channel);
                        }
                        started = true;
                        break;
                    case "quit":
                        disconnectClient();
                        break;
                    default:
                        display("Enter 'start' to start game, or 'quit' to quit");
                        break;
                }
            } catch (Exception e) {
                System.err.println("Connection lost");
                try {
                    disconnectClient();
                } catch (IOException ex) {
                    Logger.getLogger(UserHandle.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        }

    }
//Welcome message only shown on first start
    public void firstdisp() {
        
        if (firsttime) {
            rs = logic.toString(roundscore);
            ts = logic.toString(totalscore);
            display("Welcome, make a pick (rock/paper/scissors)\n" +
                    "Round Score: " + rs + " Total Score: " + ts);
            firsttime = false;
        }
    }

    public void scoring(List<String> allPicks) {

            //Get the user's score for the round and the total score for the user
            roundscore = logic.getWinner(allPicks, myPick);
            totalscore = logic.getTotal(totalscore, roundscore);

            rs = logic.toString(roundscore);
            ts = logic.toString(totalscore);
            display("Round Score: " + rs + " Total Score: " + ts + "\n" +
                    "New round, pick rock/paper/scissors");
        
    }
    //A new entry is put in <code>entry</code> and the run method will be executed
    void recvMsg() throws IOException {
        fromClient.clear();
        int bytesRead = channel.read(fromClient);
        if (bytesRead == -1) {
            throw new IOException("Disconnected.");
        }
        fromClient.flip();
        byte[] bytes = new byte[fromClient.remaining()];
        fromClient.get(bytes);
        String recvdString = new String(bytes);
        entry = recvdString;
        ForkJoinPool.commonPool().execute(this);
    }

    void sendMsg(ByteBuffer msg) throws IOException {
        channel.write(msg);
        if (msg.hasRemaining()) {
            throw new IOException("Could not send message");
        }
    }
    //Used to dispay a message to the user
    void display(String message) {
        server.showToUser(channel, message);
    }

    void disconnectClient() throws IOException {
        server.dcUser();
        channel.close();
    }

}
