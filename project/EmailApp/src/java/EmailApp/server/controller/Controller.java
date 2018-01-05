/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package EmailApp.server.controller;

import EmailApp.both.User;

import EmailApp.server.model.DatabaseHandler;
import java.rmi.RemoteException;

/**
 *
 * @author Emil
 */
public class Controller {
  //private final EmailDAO edb;
    public Controller() throws RemoteException {
      //  edb = new EmailDAO();
    }
     private final DatabaseHandler dbhandle = new DatabaseHandler();
     public void registerUser(User newUser){
        // boolean registered = dbhandle.registerUser(newUser);
     }
}
