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
    private List<String> selectedPicks = new ArrayList<>();
    private final List<String> pickChecked = new ArrayList<>();
    private Selector selector;
    RpcLogic logic = new RpcLogic();
    private ServerSocketChannel serverChannel;
    private final List<SocketChannel> allChannels = new ArrayList<>();

    public static void main(String[] args) {
        Server serv = new Server();
        serv.serve();
    }

    private void serve() {
        try {
            initialize();
            readyPlayers = 0;
            selectedPickers = 0;
            while (true) {
                selector.select();
                //Iterate over the keys to check what to do
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
    //Initialize the selector and Server socket channel
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

    //Used to display a message to a specific user
    void showToUser(SocketChannel channel, String msg) {
        SelectionKey key = channel.keyFor(selector);
        User user = (User) key.attachment();
        key.interestOps(SelectionKey.OP_WRITE);
        user.queueMsg(sendMsg(msg));
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
        synchronized (selectedPicks) {
            selectedPicks.add(pick);
            //When all players have made a selection the scores can be calculated
            if (selectedPicks.size() == allChannels.size()) {
                for (SocketChannel chan : allChannels) {
                    SelectionKey key = chan.keyFor(selector);
                    if (key.isValid()) {
                        User user = (User) key.attachment();
                        UserHandle handle = user.handler;
                        handle.scoring(selectedPicks);
                    }
                }

            }
            synchronized (pickChecked) {
                pickChecked.add(pick);
                if (pickChecked.size() == allChannels.size()) {
                    selectedPicks.clear();
                    pickChecked.clear();
                }
            }
        }
    }

    //Returns the list of all the users picks(rock/paper/scissors) for the round.
    public List<String> getPicks() {
        return selectedPicks;
    }

    //Adds the users channel when "start" is typed, if there are more than one player the game is started
    public void addChannel(SocketChannel channel) {
        synchronized (allChannels) {
            allChannels.add(channel);
            if (allChannels.size() < 2) {
                showToUser(channel, "Waiting for more player/s");
            } else if (allChannels.size() == 2) {
                for (SocketChannel chan : allChannels) {
                    SelectionKey key = chan.keyFor(selector);
                    if (key.isValid()) {
                        User user = (User) key.attachment();
                        UserHandle handle = user.handler;
                        handle.firstdisp();
                    }

                }
            } else if (allChannels.size() > 2) {
                SelectionKey key = channel.keyFor(selector);
                if (key.isValid()) {
                    User user = (User) key.attachment();
                    UserHandle handle = user.handler;
                    handle.firstdisp();
                }
            }
        }
    }

    //Remove user from list if disconnected
    public void dcUser() {
        readyPlayers--;
        if (selectedPickers > readyPlayers) {
            selectedPickers--;
        }
    }

    //Adds user to list of users who have selected a pick (rock/paper/scissors).
    public void addSelect() {
        selectedPickers++;
    }

    //Creates UserHandle
    private void startHandler(SelectionKey key) throws IOException {
        ServerSocketChannel socketChannel = (ServerSocketChannel) key.channel();
        //Establish connection
        SocketChannel clientChannel = socketChannel.accept();
        //Configure to non-blocking
        clientChannel.configureBlocking(false);
        //Creating an instace of the UserHandle
        UserHandle handler = new UserHandle(this, clientChannel);
        //UserHandle reference passed to Client 
        clientChannel.register(selector, SelectionKey.OP_WRITE, new User(handler));
        //Set linger
        clientChannel.setOption(StandardSocketOptions.SO_LINGER, 5000);

    }

    private ByteBuffer sendMsg(String msg) {
        return ByteBuffer.wrap(msg.getBytes());
    }
    //Class for each users UserHandle
    private class User {

        private final UserHandle handler;
        private final Queue<ByteBuffer> toClient = new ArrayDeque<>();

        private User(UserHandle handler) {
            this.handler = handler;

        }
        private void queueMsg(ByteBuffer msg) {
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
        User client = (User) clientKey.attachment();
        client.handler.disconnectClient();
        clientKey.cancel();
    }

    private void recvFromClient(SelectionKey key) throws IOException {
        User user = (User) key.attachment();
        try {
            user.handler.recvMsg();
        } catch (IOException e) {
            removeClient(key);
        }
    }

    private void sendToClient(SelectionKey key) throws IOException {
        //Retrieve instance of client object
        User user = (User) key.attachment();
        try {
            user.sendMsgs();
            //If message sent then change interest to read
            key.interestOps(SelectionKey.OP_READ);
        } catch (IOException e) {
            removeClient(key);
        }
    }

}
