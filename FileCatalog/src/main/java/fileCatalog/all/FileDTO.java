/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fileCatalog.all;

import java.io.Serializable;
import java.rmi.RemoteException;

/**
 *
 * @author Emil
 */
public interface FileDTO extends Serializable {
    
   public String  getFilename() throws RemoteException;
   
   public String getUsername() throws RemoteException;
   
   public int getAccess() throws RemoteException;
   
   public int getSize() throws RemoteException;
    
}
