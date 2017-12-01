/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fileCatalog.client.startup;
import fileCatalog.client.view.View;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author Emil
 */
public class Main {

    public static void main(String[] args){
        try {
            new View().start();
        } catch (RemoteException ex) {
            System.out.println("Client not started");
        }
    }
}
