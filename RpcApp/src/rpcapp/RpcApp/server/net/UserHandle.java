package rpcapp.RpcApp.server.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UncheckedIOException;
import java.net.Socket;
import java.util.List;
import rpcapp.RpcApp.server.model.RpcLogic;

/**
 * Handles all the in- and outputs for an individual client.
 */
public class UserHandle implements Runnable {

    private final String username = "Player: ";
    private BufferedReader fromClient;
    private PrintWriter toClient;
    private final Server server;
    private final Socket clientSocket;
    public boolean gamestarted = false;
    private volatile boolean connected = false;
    private boolean firsttime = true;
    private int roundscore = 0;
    private int totalscore = 0;
    private String myChoice;
    private List<String> allPicks;
    RpcLogic logic = new RpcLogic();

    //Creates a new instance, sets connected to true for user
    UserHandle(Server server, Socket clientSocket) {
        this.server = server;
        this.clientSocket = clientSocket;
        connected = true;

    }

    /*
    * Handle all the inputs of the user and also what will be triggered when input
    * also handle the text and score seen by user.
     */
    @Override
    public void run() {

        try {
            boolean autoFlush = true;
            fromClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            toClient = new PrintWriter(clientSocket.getOutputStream(), autoFlush);
        } catch (IOException ioe) {
            throw new UncheckedIOException(ioe);
        }

        while (connected) {

            if (server.countPlayers() > 1) {
                //Only display welcome message first time
                if (firsttime) {
                    toClient.println("Welcome, make a pick (rock/paper/scissors)");
                    toClient.println("Round Score: " + roundscore);
                    toClient.println("Total Score: " + totalscore);
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
                    switch (fromClient.readLine()) {
                        case "rock":
                            toClient.println(username + "picked rock");
                            synchronized (server) {
                                server.addSelect(this);
                                server.addPicks("rock");
                            }
                            myChoice = "rock";
                            break;
                        case "paper":
                            toClient.println(username + "picked paper");
                            synchronized (server) {
                                server.addSelect(this);
                                server.addPicks("paper");
                            }
                            myChoice = "paper";
                            break;
                        case "scissors":
                            toClient.println(username + "picked scissors");
                            synchronized (server) {
                                server.addSelect(this);
                                server.addPicks("scissors");
                            }
                            myChoice = "scissors";
                            break;
                        case "quit":
                            disconnectClient();
                            break;
                        default:
                            toClient.println("Invalid input; Try again.");
                            break;
                    }
                } catch (IOException e) {
                    System.err.println("Connection lost");
                    connected = false;

                }

                while (server.countPlayers() > server.countPickers()) {
                }
                //Before the user has started only 'start' and 'quit' can be chosen
            } else {
                try {
                    switch (fromClient.readLine()) {
                        case "start":
                            server.addPlayers(this);
                            if (server.countPlayers() < 2) {
                                toClient.println("Waiting for other player/s");
                            }

                            while (server.countPlayers() < 2) {
                            }
                            break;
                        case "quit":
                            disconnectClient();

                            break;
                        default:
                            toClient.println("Enter 'start' to start game, or 'quit' to quit");
                            break;
                    }
                } catch (IOException e) {
                    System.err.println("Connection lost");
                    disconnectClient();

                }
            }
        }
    }

    //Duscinnect client from server.
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
}
