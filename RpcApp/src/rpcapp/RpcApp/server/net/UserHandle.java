package rpcapp.RpcApp.server.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UncheckedIOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.logging.Level;
import java.util.logging.Logger;
import rpcapp.RpcApp.server.model.RpcLogic;

/**
 * Handles all the in- and outputs for an individual client.
 */
public class UserHandle implements Runnable {

    private final String username = "Player: ";
    private PrintWriter toClient;
    private final Server server;
    public boolean gamestarted = false;
    private volatile boolean connected = false;
    private boolean firsttime = true;
    private int roundscore = 0;
    private int totalscore = 0;
    private String myChoice;
    private List<String> allPicks;
    RpcLogic logic = new RpcLogic();
    private final SocketChannel channel;
    private final ByteBuffer fromClient = ByteBuffer.allocateDirect(1024);

    private String entry;

    //Creates a new instance, sets connected to true for user
    UserHandle(Server server, SocketChannel channel) throws IOException {
        this.server = server;
        this.channel = channel;
        synchronized (server) {
            server.addConnected();
        }

        connected = true;

    }

    /*
    * Handle all the inputs of the user and also what will be triggered when input
    * also handle the text and score seen by user.
     */
    @Override
    public void run() {

        while (connected) {
            if (server.countPlayers() > 1) {
                //Only display welcome message first time
                if (firsttime) {
                    String rs = logic.toString(roundscore);
                    String ts = logic.toString(totalscore);
                    display("Welcome, make a pick (rock/paper/scissors)");
                    display("Round Score: ");
                    display("Total Score: ");
                    firsttime = false;
                }

                //When all players have made a selection the scores can be calculated
                if (server.countPlayers() == server.countPickers()) {
                    synchronized (server) {
                        allPicks = server.getPicks();
                    }
                    //Get the user's score for the round and the total score for the user
                    roundscore = logic.getWinner(allPicks, myChoice);
                    totalscore = logic.getTotal(totalscore, roundscore);

                    synchronized (server) {
                        server.addCalc(this);
                    }
                    if (server.countCalc() == server.countPickers()) {
                        synchronized (server) {
                            server.clearer();
                        }
                        allPicks.clear();
                    }

                    toClient.println("Round Score: " + roundscore);
                    toClient.println("Total Score: " + totalscore);
                    toClient.println("New round, pick rock/paper/scissors");
                }
                try {
                    switch (entry) {
                        case "rock":
                            display("picked rock");
                            synchronized (server) {
                                server.addSelect();
                                server.addPicks("rock");
                            }
                            myChoice = "rock";
                            break;
                        case "paper":
                            display("picked paper");
                            synchronized (server) {
                                server.addSelect();
                                server.addPicks("paper");
                            }
                            myChoice = "paper";
                            break;
                        case "scissors":
                            display("picked scissors");
                            synchronized (server) {
                                server.addSelect();
                                server.addPicks("scissors");
                            }
                            myChoice = "scissors";
                            break;
                        case "quit":
                            disconnectClient();
                            break;
                        default:
                            display("Invalid input; Try again.");
                            break;
                    }
                } catch (Exception e) {
                    System.err.println("Connection lost");
                    e.printStackTrace();
                    connected = false;

                }

                while (server.countPlayers() > server.countPickers()) {
                }
                //Before the user has started only 'start' and 'quit' can be chosen
            } else {
                try {
                    switch (entry) {
                        case "start":
                            synchronized(server){
                            server.addPlayers();
                            }
                            if (server.countPlayers() < 2) {
                                display("Waiting for other player/s");
                            }

                            while (server.countPlayers() < 2) {
                            }
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
    }

    /*
    //Disconnect client from server.
    private void disconnectClient() {
        connected = false;
        synchronized (server) {
            server.dcUser(this);
        }
        try {
            clientSocket.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

    }
     */
    void recvMsg() throws IOException {
        fromClient.clear();
        int numOfReadBytes = channel.read(fromClient);
        if (numOfReadBytes == -1) {
            throw new IOException("Client has closed connection.");
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

    void display(String message) {
        server.showToUser(channel, message);
    }

    void disconnectClient() throws IOException {
        channel.close();
    }

}
