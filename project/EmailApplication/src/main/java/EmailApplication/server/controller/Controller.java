/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package EmailApplication.server.controller;

import EmailApplication.both.Email;
import EmailApplication.both.User;
import EmailApplication.server.integration.EmailDAO;
import EmailApplication.server.model.EmailData;
import EmailApplication.server.model.UserData;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Set;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

/**
 *
 * @author Emil
 */
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
@Stateless
public class Controller {
    //private final EmailDAO edb;
    @EJB
    EmailDAO edao;

    public EmailData addMail(Email email) {
        return edao.storeMail(email);
    }
    public void removeMail(int id) {
        edao.removeMail(id);
    }
    public boolean authenticateUser(User user){
    UserData usd = new UserData(user.getUsername(), user.getPassword());
    return edao.authenticateUser(usd);
    }
    
    public boolean registerUser(User newUser) {
       return edao.storeUser(newUser);
    }
    
    public Set<EmailData> getRecieved(String user){
        return edao.getMailsForUser(user);
    }
    
    public Set<EmailData> getSent(String user){
        return edao.getMailsForSender(user);
    }
    
}
