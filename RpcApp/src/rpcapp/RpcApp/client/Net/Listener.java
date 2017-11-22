/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rpcapp.RpcApp.client.Net;

import java.net.InetSocketAddress;

/**
 *
 * @author Emil
 */
public interface Listener {
    /**
     * Called when a broadcast message from the server has been received. That message originates
     * from one of the clients.
     *
     * @param msg The message from the server.
     */
    public void recvdMsg(String msg);

    /**
     * Called when the local client is successfully connected to the server.
     *
     * @param serverAddress The address of the server to which connection is established.
     */
 //   public void connected(InetSocketAddress serverAddress);

    /**
     * Called when the local client is successfully disconnected from the server.
     */
    public void disconnected();
}
