/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fileCatalog.all;

import java.io.FileInputStream;
import java.io.FileReader;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.function.Consumer;

/**
 *
 * @author Emil
 */
public interface Fserver extends Remote{
    public static final String RegName = "File_Serv";
    //Login to application
    long login(Fclient remote, UserCredentials cred) throws RemoteException;
    //Log out from application
    void logout(long id) throws RemoteException;
    //Register user
    long register(Fclient remote, UserCredentials cred) throws RemoteException;
    //Unregister user
    long unregister(Fclient remote, UserCredentials cred) throws RemoteException;
    //Send a message
    void sendMsg(long id, String msg) throws RemoteException;
    //Update file
    void updatefile(String file, String text) throws RemoteException;
    //Upload a file
    void uploadFile(byte[] file, String text) throws RemoteException;
    //Read from file
    void read(String path, long id) throws RemoteException;
    //List files
    void list(String path, long id) throws RemoteException;
    //Creates a directory
    void createDir(String path, long id) throws RemoteException;
    //Deletes a directory
    void deleteDir(String path, long id) throws RemoteException;
    
    
}
