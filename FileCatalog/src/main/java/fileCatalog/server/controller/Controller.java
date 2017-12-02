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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.rmi.RemoteException;

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
        //dbhandle.broadcast("Welcome!", username);
        return username;
    }
    @Override
    public String register(Fclient remote, UserCredentials cred) {
        String username = dbhandle.registerUser(remote, cred);
        dbhandle.broadcast("Welcome!", username);
        return username;
    }

    @Override
    public void logout(String username) throws RemoteException {
        dbhandle.logoutUser(username);
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
    public void updatefile(String path, String content) {

        try {
            fhandle.updateFile(path, content);
        } catch (IOException | ClassNotFoundException ioe) {
            System.err.println("Did not work");
        }
    }

    @Override
    public void uploadFile(byte[] file, String name, int access, String username, int filesize, int writable) {
        try {
            fhandle.uploadFile(file, name);
            dbhandle.uploadFile(name, access, username, filesize, writable);
        } catch (FileNotFoundException ioe) {
            System.err.println("Did not work");
        }
    }

    @Override
    public boolean deleteFile(String filename, String username) throws RemoteException {
        try {
           return dbhandle.deleteFile(filename, username);
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
    public void list(String path, String username) throws RemoteException {
        try {
            String cont = fhandle.listDir(path);
            dbhandle.broadcast(cont, username);
        } catch (IOException ex) {
            System.err.println("Could not list");
        }
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
