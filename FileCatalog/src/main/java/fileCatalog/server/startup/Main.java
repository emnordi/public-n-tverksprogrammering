/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fileCatalog.server.startup;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import fileCatalog.server.controller.Controller;
import fileCatalog.server.integration.FileDAO;
import java.net.MalformedURLException;
/**
 *
 * @author Emil
 */
public class Main {
    public static void main(String[] args){
        try {
            new Main().startReg();
            Naming.rebind(Controller.RegName, new Controller());
           // new Main().startdb();
        } catch (RemoteException | MalformedURLException ex) {
            System.out.println("Server not started");
        }
    }

    
    private void startReg() throws RemoteException{
        try{
            //Checks if registry is started
            LocateRegistry.getRegistry().list();
            //Create registry if there is not one
        }catch(RemoteException re){
            LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
        }
    }
    
}
