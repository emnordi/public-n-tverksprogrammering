/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fileCatalog.server.model;

import fileCatalog.all.Fclient;
import fileCatalog.all.UserCredentials;
import fileCatalog.server.integration.FileDAO;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 *
 * @author Emil
 */
public class DatabaseHandle {
    private final Map<String, User> loggedonUsers = Collections.synchronizedMap(new HashMap<>());
    private FileDAO fdao = new FileDAO();

    public String registerUser(Fclient remoteNode, UserCredentials credentials) {
        boolean ruser;
        ruser = fdao.registerUser(credentials.getUsername(), credentials.getPassword());
        if (ruser) {
            User newUser = new User(credentials.getUsername(),
                    remoteNode, this);
            loggedonUsers.put(credentials.getUsername(), newUser);
            return credentials.getUsername();
        } else {
            return "UserAlreadyExists";
        }
    }

    public boolean loginUser(Fclient remoteNode, UserCredentials credentials) {
        boolean verified = fdao.authenticate(credentials.getUsername(), credentials.getPassword());
        if (verified) {
            User newUser = new User(credentials.getUsername(), remoteNode, this);
            loggedonUsers.put(credentials.getUsername(), newUser);
            return true;
        } else {
            return false;
        }
    }
    public void uploadFile(String filename, int access, String username, int filesize, int writable){
        fdao.uploadFile(filename, access, username, filesize, writable);
    }
    public boolean deleteFile(String filename, String username){
        return fdao.deleteFile(filename, username);
    }
    

    public void unregisterUser(String username) {
        broadcast("Unregistering " + username, username);
        loggedonUsers.remove(username);
        fdao.unregisterUser(username);

    }

    public void logoutUser(String username) {
        broadcast("Logging out " + username, username);
        loggedonUsers.remove(username);
    }

    public void broadcast(String msg, String username) {
        synchronized (loggedonUsers) {
            loggedonUsers.get(username).send(msg);
        }
    }

}
