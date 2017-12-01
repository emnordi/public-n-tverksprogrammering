/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fileCatalog.server.model;

import fileCatalog.all.Fclient;
import java.rmi.RemoteException;

/**
 *
 * @author Emil
 */
public class User {
    private final Fclient remoteNode;
    private final DatabaseHandle dbhandle;
    private final String username;


    public User(String username, Fclient remoteNode, DatabaseHandle mgr) {
        this.username = username;
        this.remoteNode = remoteNode;
        this.dbhandle = mgr;
    }

    public void send(String msg) {
        try {
            remoteNode.getMsg(msg);
        } catch (RemoteException re) {
            re.printStackTrace();
        }
    }

    //Send message to user
    public void broadcast(String msg) {
        dbhandle.broadcast(msg, username);
    }

  
    public boolean hasRemoteNode(Fclient remoteNode) {
        return remoteNode.equals(this.remoteNode);
    }


}
