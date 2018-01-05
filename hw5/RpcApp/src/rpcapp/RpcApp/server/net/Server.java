package rpcapp.RpcApp.server.net;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import rpcapp.RpcApp.server.model.RpcLogic;

/**
 * Recieves inputs from user and manages clients connected.
 */
public class Server {

    private volatile ArrayList<UserHandle> readyPlayers = new ArrayList<>();
    private volatile ArrayList<UserHandle> selectedPickers = new ArrayList<>();
    private volatile List<UserHandle> scoreCalculated = new ArrayList<>();
    private List<String> selectedPicks = new ArrayList<>();

    RpcLogic logic = new RpcLogic();
    private int rp = 0;

    public static void main(String[] args) {
        Server serv = new Server();
        serv.serve();
    }

    private void serve() {
        try {
            ServerSocket listeningSocket = new ServerSocket(8000);
            while (true) {
                //connected to client 
                Socket clientSocket = listeningSocket.accept();
                startHandler(clientSocket);
            }
        } catch (IOException e) {
            System.err.println("Server failure.");
        }
    }

    //Adds a user to a list of all players that have entered "start"
    public void addPlayers(UserHandle user) {
        readyPlayers.add(user);
    }

    //Returns the amount of players that are ready to play
    public int countPlayers() {
        return readyPlayers.size();
    }

    //Adds a users choice(rock/paper/scissors) to a list 
    public void addPicks(String pick) {
        selectedPicks.add(pick);
    }

    //Returns the list of all the users picks(rock/paper/scissors) for the round.
    public List<String> getPicks() {
        return selectedPicks;
    }
    //Remove user from list if disconnected
    public void dcUser(UserHandle user){
        readyPlayers.remove(user);
        readyPlayers.trimToSize();
        if(selectedPickers.contains(user)){
            selectedPickers.remove(user);
        }

    }

    /*
    * Clears the list of picks, users who've made selects and users with
    * with calculated scores for the next round.
     */
    public void clearer() {
        selectedPicks.clear();
        scoreCalculated.clear();
        selectedPickers.clear();
    }

    //Adds a user to the list of users who are calculated their score.
    public void addCalc(UserHandle user) {
        scoreCalculated.add(user);
    }

    //Returns the size of the list of users who have calculated their score.
    public int countCalc() {
        return scoreCalculated.size();
    }

    //Adds user to list of users who have selected a pick (rock/paper/scissors).
    public void addSelect(UserHandle user) {
        selectedPickers.add(user);
    }

    //Returns the amount of users who have selected a pick(rock/paper/scissors).
    public int countPickers() {
        return selectedPickers.size();
    }
 
    private void startHandler(Socket clientSocket) throws SocketException {

        clientSocket.setSoLinger(true, 5000);
        clientSocket.setSoTimeout(1800000);
        // Create client
        UserHandle handler = new UserHandle(this, clientSocket);
        
        //Create a thread running handler
        Thread hthread = new Thread(handler);
        hthread.start();
        
    }

}
