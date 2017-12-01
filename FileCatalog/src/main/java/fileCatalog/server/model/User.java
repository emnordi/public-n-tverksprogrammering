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
    private static final String DEFAULT_USERNAME = "anonymous";
    private final long id;
    private final Fclient remoteNode;
    private final DatabaseHandle dbhandle;
    private String username;


    public User(long id, String username, Fclient remoteNode, DatabaseHandle mgr) {
        this.id = id;
        this.username = username;
        this.remoteNode = remoteNode;
        this.dbhandle = mgr;
    }


    public User(long id, Fclient remoteNode, DatabaseHandle mgr) {
        this(id, DEFAULT_USERNAME, remoteNode, mgr);
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
        dbhandle.broadcast(msg, id);
    }

  
    public boolean hasRemoteNode(Fclient remoteNode) {
        return remoteNode.equals(this.remoteNode);
    }


}
