/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fileCatalog.all;

import fileCatalog.server.model.UserFile;
import java.io.FileInputStream;
import java.io.FileReader;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.function.Consumer;

/**
 *
 * @author Emil
 */
public interface Fserver extends Remote{
    public static final String RegName = "File_Serv";
    //Login to application
    boolean login(Fclient remote, UserCredentials cred) throws RemoteException;
    //Log out from application
    void logout(String username) throws RemoteException;
    //Register user
    boolean register(Fclient remote, UserCredentials cred) throws RemoteException;
    //Unregister user
    void unregister(String username) throws RemoteException;
    //Send a message
    void sendMsg(String id, String msg) throws RemoteException;
    //Update file
    boolean updateFileAccess(String filename, int access, String username, int writable) throws RemoteException;
    //Write to file
    boolean updateFileContent(String file, String text, String username) throws RemoteException;
    //Upload a file
    boolean uploadFile(byte[] file, String name, int access, String username, int filesize, int writable) throws RemoteException;
    //Deletes a file
    boolean deleteFile(String filename, String username) throws RemoteException;
    //Download a file
    boolean downloadFile(String filename, String username) throws RemoteException;
    //Read from file
    void read(String path, String username) throws RemoteException;
    //List files
    List<? extends FileDTO> listFiles(String username) throws RemoteException;
    //Creates a directory
    void createDir(String path, String username) throws RemoteException;
    //Deletes a directory
    void deleteDir(String path, String username) throws RemoteException;
    
    
}
