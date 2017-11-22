package rpcapp.RpcApp.server.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import rpcapp.RpcApp.server.model.RpcLogic;

/**
 * Recieves inputs from user and manages clients connected.
 */
public class Server {

    private volatile int readyPlayers;
    private volatile int selectedPickers;
    private volatile int scoreCalculated;
    private volatile int connectedPlayers;
    private List<String> selectedPicks = new ArrayList<>();
    private Selector selector;
    RpcLogic logic = new RpcLogic();
    private ServerSocketChannel serverChannel;
    

    public static void main(String[] args) {
        Server serv = new Server();
        serv.serve();
    }

    private void serve() {
        try {
            initialize();
            connectedPlayers = 0;
            readyPlayers = 0;
            scoreCalculated = 0;
            selectedPickers = 0;
            while (true) {
                selector.select();

                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    //Remove keys that have been performed
                    iterator.remove();

                    if (!key.isValid()) {
                        continue;
                    }
                    if (key.isAcceptable()) {
                        startHandler(key);
                    }//Get entry from client
                    else if (key.isReadable()) {
                        recvFromClient(key);
                    }//Send message to client
                    else if (key.isWritable()) {
                        sendToClient(key);
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initialize() throws IOException {
        selector = Selector.open();
        //open channel
        serverChannel = ServerSocketChannel.open();
        //configure channel to be non-blocking
        serverChannel.configureBlocking(false);
        //Bind socket to port
        serverChannel.bind(new InetSocketAddress(8000));
        //Register selector and set interest to accepting connection request
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);
    }

    void showToUser(SocketChannel channel, String msg){
        SelectionKey key = channel.keyFor(selector);
        Client client = (Client) key.attachment();
        key.interestOps(SelectionKey.OP_WRITE);
        client.queueMsgToSend(sendMsg(msg));
        selector.wakeup();
    }
    
    //Adds a user to a list of all players that have entered "start"
    public void addPlayers() {
        readyPlayers++;
    }

    //Returns the amount of players that are ready to play
    public int countPlayers() {
       return readyPlayers;
    }

    //Adds a users choice(rock/paper/scissors) to a list 
    public void addPicks(String pick) {
        selectedPicks.add(pick);
    }

    //Returns the list of all the users picks(rock/paper/scissors) for the round.
    public List<String> getPicks() {
        return selectedPicks;
    }
    public int getConnected(){
        return connectedPlayers;
    }
    public void addConnected(){
       connectedPlayers++;
    }

    //Remove user from list if disconnected
    public void dcUser() {
        readyPlayers--;
        if (selectedPickers > readyPlayers) {
            selectedPickers--;
        }

    }

    /*
    * Clears the list of picks, users who've made selects and users with
    * with calculated scores for the next round.
     */
    public void clearer() {
        selectedPicks.clear();
        scoreCalculated = 0;
        selectedPickers = 0;
    }

    //Adds a user to the list of users who are calculated their score.
    public void addCalc(UserHandle user) {
        scoreCalculated++;
    }

    //Returns the size of the list of users who have calculated their score.
    public int countCalc() {
        return scoreCalculated;
    }

    //Adds user to list of users who have selected a pick (rock/paper/scissors).
    public void addSelect() {
        selectedPickers++;
    }

    //Returns the amount of users who have selected a pick(rock/paper/scissors).
    public int countPickers() {
        return selectedPickers;
    }

    private void startHandler(SelectionKey key) throws IOException {
        ServerSocketChannel socketChannel = (ServerSocketChannel) key.channel();
        //Establish connection
        SocketChannel clientChannel = socketChannel.accept();
        //Configure to non-blocking
        clientChannel.configureBlocking(false);
        //Creating an instace of the UserHandle
        UserHandle handler = new UserHandle(this, clientChannel);
        //UserHandle reference passed to Client 
        clientChannel.register(selector, SelectionKey.OP_WRITE, new Client(handler));
        //Set linger
        clientChannel.setOption(StandardSocketOptions.SO_LINGER, 5000);
        
    }

    private ByteBuffer sendMsg(String msg) {
        return ByteBuffer.wrap(msg.getBytes());
    }

    private class Client {

        private final UserHandle handler;
        private final Queue<ByteBuffer> toClient = new ArrayDeque<>();

        private Client(UserHandle handler) {
            this.handler = handler;

        }
        private void queueMsgToSend(ByteBuffer msg) {
            synchronized (toClient) {
                toClient.add(msg.duplicate());
            }
        }
        private void sendMsgs() throws IOException {
            ByteBuffer msg = null;
            synchronized (toClient) {
                while ((msg = toClient.peek()) != null) {
                    handler.sendMsg(msg);
                    toClient.remove();
                }
            }
        }
    }

    private void removeClient(SelectionKey clientKey) throws IOException {
        Client client = (Client) clientKey.attachment();
        client.handler.disconnectClient();
        clientKey.cancel();
    }

    private void recvFromClient(SelectionKey key) throws IOException {
        Client client = (Client) key.attachment();
        try {
            client.handler.recvMsg();
        } catch (IOException clientHasClosedConnection) {
            removeClient(key);
        }
    }
    
    private void sendToClient(SelectionKey key) throws IOException {
        //Retrieve instance of client object
        Client client = (Client) key.attachment();
        try {
            client.sendMsgs();
            //If message sent then change interest to read
            key.interestOps(SelectionKey.OP_READ);
        } catch (IOException clientHasClosedConnection) {
            removeClient(key);
        }
    }

}
