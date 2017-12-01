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
import fileCatalog.server.model.DatabaseHandle;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.rmi.RemoteException;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Emil
 */
public class Controller extends UnicastRemoteObject implements Fserver {

    public Controller() throws RemoteException {
    }
    private final DatabaseHandle dbhandle = new DatabaseHandle();
    private final FileHandle fhandle = new FileHandle();

    @Override
    public long login(Fclient remote, UserCredentials cred) {
        long userId = dbhandle.createParticipant(remote, cred);
        dbhandle.broadcast("Welcome!", userId);
        return userId;
    }

    @Override
    public void logout(long id) throws RemoteException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public long register(Fclient remote, UserCredentials cred) throws RemoteException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public long unregister(Fclient remote, UserCredentials cred) throws RemoteException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void sendMsg(long id, String msg) throws RemoteException {
        dbhandle.broadcast(msg, id);
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
    public void uploadFile(byte[] file, String name) {
        try {
            fhandle.uploadFile(file, name);
        } catch (FileNotFoundException ioe) {
            System.err.println("Did not work");
        }
    }

    @Override
    public void read(String path, long id) throws RemoteException {
        try {
            String cont = fhandle.read(path);
            dbhandle.broadcast(cont, id);
        } catch (IOException ioe) {
            throw new UncheckedIOException(ioe);
        } catch (ClassNotFoundException ex) {
            System.err.println("Classnotfound");
        }
    }

    @Override
    public void list(String path, long id) throws RemoteException {
        try {
            String cont = fhandle.listDir(path);
            dbhandle.broadcast(cont, id);
        } catch (IOException ex) {
            System.err.println("Could not list");
        }
    }

    @Override
    public void createDir(String path, long id) throws RemoteException {
        try {
            fhandle.makeDirectory(path);
        } catch (IOException ioe) {
            throw new UncheckedIOException(ioe);
        }
    }

    @Override
    public void deleteDir(String path, long id) throws RemoteException {
        try {
            fhandle.deleteDirectory(path);
        } catch (IOException ioe) {
            throw new UncheckedIOException(ioe);
        }
        new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
