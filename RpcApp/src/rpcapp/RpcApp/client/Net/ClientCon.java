package rpcapp.RpcApp.client.Net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.channels.WritableByteChannel;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import rpcapp.RpcApp.client.View.View;

/**
 * @author Emil
 */
public class ClientCon implements Runnable {
    Socket socket;
    String toUser;
    private SocketChannel socketChannel;
    private InetSocketAddress address;
    private Selector selector;
    private final List<Listener> listeners = new ArrayList<>();
    private final ByteBuffer fromServ = ByteBuffer.allocateDirect(1024);
    private final Queue<ByteBuffer> toServ = new ArrayDeque<>();
    private boolean connected;
    private boolean send = false;
    /*
    * A connection is started on 127.0.0.1(localhost) on port 8000, and with a lingertime of 30000ms
    * toServ PrintWriter is created to forward inputs to the server. 
     */
    public void run() {
 
        
        try {
            initialize();
            while(connected || !toServ.isEmpty()){
                
                 if (send) {
                    socketChannel.keyFor(selector).interestOps(SelectionKey.OP_WRITE);
                    send = false;
                }
                
            //Selector will block until connection is established
            selector.select();
            
            for(SelectionKey key : selector.selectedKeys()){
                //Remove keys that have been performed
                selector.selectedKeys().remove(key);
                 if (!key.isValid())
                     continue;
                 
                 if (key.isConnectable()) {
                     socketChannel.finishConnect();
                     //Change interest from connecting to reading
                         toClie("Connected successfully");
                     
                 }
                 else if(key.isReadable()){
                     getFromServ(key);
                 }
                 
                 else if (key.isWritable()) {
                        sendToServ(key);
                 }
                 
            }
            }
            
            
            
            
            
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        
    }
    //Add user to list of recievers
    public void addListener(Listener listener){
        listeners.add(listener);
    }
    
    private void toClie(String msg) {
        //new thread 
        Executor pool = ForkJoinPool.commonPool();
        for(Listener listener : listeners) {
            pool.execute(new Runnable(){
            @Override
            public void run(){
                listener.recvdMsg(msg);
            }
        });
        }
    }
    public void putEntry(String msg) {
        
        synchronized (toServ) {
            //wrap in bytebuffer and put onto queue
            toServ.add(ByteBuffer.wrap(msg.getBytes()));
        }
        send = true;
        //Inform selector that there is information in queue
        selector.wakeup();
    }
    
    private void sendToServ(SelectionKey key) throws IOException {
        ByteBuffer msg;
        View view = new View();
        synchronized (toServ) {
            //Check that there is a message
            while ((msg = toServ.peek()) != null) {
                socketChannel.write(msg);
                if (msg.hasRemaining()) {
                    return;
                }
                //Remove msg from queue after sent
                toServ.remove();
            }
            view.cansend = true;
            //Change interest to read
            key.interestOps(SelectionKey.OP_READ);
        }
    }
    
    private void getFromServ(SelectionKey key) throws IOException{
        //Clear buffer to write
        fromServ.clear();
        int readBytes = socketChannel.read(fromServ);
        
        if(readBytes == -1){
            throw new IOException("Disconnected");
        }
        fromServ.flip();
        byte[] bytes = new byte[fromServ.remaining()];
        fromServ.get(bytes);
        String msg = new String(bytes);
        toClie(msg);
        
    }
    
    
    private void initialize() throws IOException{
            //Open socket channel
            socketChannel = SocketChannel.open();
            //Configure the channel to be non-blocking
            socketChannel.configureBlocking(false);
            //Connect to address and port 
            socketChannel.connect(address);
            connected = true;
            //Create selector
            selector = Selector.open();
            //Notify when connect op is finished
            socketChannel.register(selector, SelectionKey.OP_CONNECT);
    }
 

    public void connect(InetSocketAddress servadd) {
        address = servadd;
        new Thread(this).start();
    }

    /*
    * Closes connection with server.
     */
    public void disconnect() throws IOException {
        toClie("Disconnectedaroo");
        connected = false;
        
    }

    private void doDisconnect() throws IOException {
        socketChannel.close();
        socketChannel.keyFor(selector).cancel();
    }

}
