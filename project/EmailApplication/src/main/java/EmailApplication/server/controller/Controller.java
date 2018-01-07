package EmailApplication.server.controller;

import EmailApplication.constructors.Email;
import EmailApplication.constructors.User;
import EmailApplication.server.integration.EmailDAO;
import EmailApplication.server.model.EmailData;
import EmailApplication.server.model.UserData;
import java.util.Set;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
@Stateless
public class Controller {

    @EJB
    EmailDAO edao;

    public EmailData addMail(Email email) {
        return edao.storeMail(email);
    }

    public boolean removeMail(int id) {
        return edao.removeMail(id);
    }

    public boolean authenticateUser(User user) {
        UserData usd = new UserData(user.getUsername(), user.getPassword());
        return edao.authenticateUser(usd);
    }

    public boolean registerUser(User newUser) {
        return edao.storeUser(newUser);
    }

    public Set<EmailData> getRecieved(String user) {
        return edao.getMailsForUser(user);
    }

    public Set<EmailData> getSent(String user) {
        return edao.getMailsForSender(user);
    }

    public EmailData getMailById(int id) {
        return edao.getMailById(id);
    }

    public boolean toggleMail(int id) {
        return edao.toggleMail(id);
    }

}
