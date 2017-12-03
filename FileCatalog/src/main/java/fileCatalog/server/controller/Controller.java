/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fileCatalog.server.controller;

import java.rmi.server.UnicastRemoteObject;
import fileCatalog.all.Fserver;
import fileCatalog.all.Fclient;
import fileCatalog.all.UserCredentials;
import fileCatalog.server.fileHandler.FileHandle;
import fileCatalog.server.integration.FileDAO;
import fileCatalog.server.model.DatabaseHandle;
import fileCatalog.server.model.UserFile;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import fileCatalog.all.FileDTO;

/**
 *
 * @author Emil
 */
public class Controller extends UnicastRemoteObject implements Fserver {
    private final FileDAO filedb;
    public Controller() throws RemoteException {
        filedb = new FileDAO();
    }
    
    private final DatabaseHandle dbhandle = new DatabaseHandle();
    private final FileHandle fhandle = new FileHandle();

    @Override
    public boolean login(Fclient remote, UserCredentials cred) {
        boolean username = dbhandle.loginUser(remote, cred);
        return username;
    }
    @Override
    public boolean register(Fclient remote, UserCredentials cred) {
        boolean registered  = dbhandle.registerUser(remote, cred);
        if(registered){
        dbhandle.broadcast("Welcome " + cred.getUsername() + "!", cred.getUsername());
        return true;
        }else{
            return false;
        }
    }

    @Override
    public void logout(String username) throws RemoteException {
        dbhandle.logoutUser(username);
    }
    
    @Override
    public boolean notifyaccess(String filename, String username) throws RemoteException {
        return dbhandle.addNotify(filename, username);
    }

    @Override
    public void unregister(String username) throws RemoteException {
       dbhandle.unregisterUser(username);
    }

    @Override
    public void sendMsg(String username, String msg) throws RemoteException {
        dbhandle.broadcast(msg, username);
    }

    @Override
    public boolean updateFileContent(String path, String content, String username) {
        boolean updatecontent = dbhandle.updateFileContent(path, username);
        if(updatecontent){
        try {
            int size = fhandle.updateFileContent(path, content);
            dbhandle.updateSize(path, size);
            dbhandle.notifyUser(username, path);
            return true;
        } catch (SQLException | IOException | ClassNotFoundException ioe) {
            System.err.println("Did not work");
        }
        }
        return false;
    }

    @Override
    public boolean uploadFile(byte[] file, String name, int access, String username, int filesize, int writable) {
        try {
            boolean uploaded = dbhandle.uploadFile(name, access, username, filesize, writable);
            if(uploaded){
            fhandle.uploadFile(file, name);
            }
            return uploaded;
        } catch (FileNotFoundException ioe) {
            return false;
        }
    }

    @Override
    public boolean updateFileAccess(String filename, int access, String username, int writable) throws RemoteException {
        try {
            return dbhandle.updateFileAccess(filename, access, username, writable);
        } catch (Exception ex) {
            return false;
        }
    }
    @Override
    public boolean deleteFile(String filename, String username) throws RemoteException {
        try {
            boolean filedelete = dbhandle.deleteFile(filename, username);
            if(filedelete){
                fhandle.deleteFile(filename);
            }
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
    @Override
    public boolean downloadFile(String filename, String username) throws RemoteException {
        try {
            boolean downloaded = dbhandle.downloadFile(filename, username);
            if(downloaded){
                fhandle.downloadFile(filename);
            }
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
    @Override
    public void read(String path, String username) throws RemoteException {
        try {
            String cont = fhandle.read(path);
            dbhandle.broadcast(cont, username);
        } catch (IOException ioe) {
            throw new UncheckedIOException(ioe);
        } catch (ClassNotFoundException ex) {
            System.err.println("Classnotfound");
        }
    }

    @Override
    public List<? extends FileDTO> listFiles(String username) throws RemoteException {
        try {
            return dbhandle.listFiles(username);
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
        return null;
    }

    @Override
    public void createDir(String path, String username) throws RemoteException {
        try {
            fhandle.makeDirectory(path);
        } catch (IOException ioe) {
            throw new UncheckedIOException(ioe);
        }
    }

    @Override
    public void deleteDir(String path, String username) throws RemoteException {
        try {
            fhandle.deleteDirectory(path);
        } catch (IOException ioe) {
            throw new UncheckedIOException(ioe);
        }
        new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
