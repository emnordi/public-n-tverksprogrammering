/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fileCatalog.server.model;

import fileCatalog.all.Fclient;
import fileCatalog.all.FileDTO;
import fileCatalog.all.UserCredentials;
import fileCatalog.server.integration.FileDAO;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 *
 * @author Emil
 */
public class DatabaseHandle {
    private final Map<String, User> loggedonUsers = Collections.synchronizedMap(new HashMap<>());
    private FileDAO fdao = new FileDAO();

    public boolean registerUser(Fclient remoteNode, UserCredentials credentials) {
        boolean ruser;
        ruser = fdao.registerUser(credentials.getUsername(), credentials.getPassword());
        if (ruser) {
            User newUser = new User(credentials.getUsername(),
                    remoteNode, this);
            loggedonUsers.put(credentials.getUsername(), newUser);
            return true;
        } else {
            return false;
        }
    }
       public void updateSize(String filename, int size) throws SQLException {
        fdao.updateSize(filename, size);
    }
       public List<? extends FileDTO> listFiles(String username) throws SQLException {
       return fdao.listFiles(username);
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
    public boolean uploadFile(String filename, int access, String username, int filesize, int writable){
        return fdao.uploadFile(filename, access, username, filesize, writable);
    }
    public boolean updateFileAccess(String filename, int access, String username, int writable){
        return fdao.updateFileAccess(filename, access, username, writable);
    }
    public boolean updateFileContent(String filename, String username){
        return fdao.updateFileContent(filename, username);
    }
    public boolean deleteFile(String filename, String username){
        return fdao.deleteFile(filename, username);
    }
    public boolean downloadFile(String filename, String username){
        return fdao.downloadFile(filename, username);
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
