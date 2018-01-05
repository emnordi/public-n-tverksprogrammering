package EmailApplication.server.integration;

import EmailApplication.both.Email;
import EmailApplication.both.User;
import EmailApplication.server.model.EmailData;
import EmailApplication.server.model.UserData;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@TransactionAttribute(TransactionAttributeType.MANDATORY)
@Stateless
public class EmailDAO {

    @PersistenceContext(unitName = "MailPU")
    private EntityManager em;
    

    //Used to insert values into the database
    public EmailData storeMail(Email newMail) {
        EmailData email = new EmailData(newMail.getSender(), newMail.getReciever(), newMail.getStatus(), newMail.getType(), newMail.getSubject(), newMail.getMessage());
        em.persist(email);
        return email;
    }

    public void removeMail(int id) {
        Query query = em.createQuery("Delete  from EmailData e where e.eid = :id");
        query.setParameter("id", id);
        int rows = query.executeUpdate();
        //todo check it was deleted
    }
    
    public Set<EmailData> getMailsForUser(String reciever){
        Set<EmailData> eds = new HashSet<>();
        EmailData ed;
        Query q = em.createNativeQuery(
                "SELECT * FROM EMAILDATA a WHERE"
                + " (a.reciever = ? AND a.type = ?)");
        q.setParameter(1, reciever);
        q.setParameter(2, "Recieved");
        List<Object[]> ob = q.getResultList();
        int i;
        for(Object[] o : ob){
            i = Integer.parseInt(o[0].toString());
            ed = new EmailData(i, o[3].toString(), o[2].toString(), o[4].toString(), o[6].toString(), o[5].toString(), o[1].toString());
            eds.add(ed);
        }
        return eds;
    }

    public Set<EmailData> getMailsForSender(String sender){
        Set<EmailData> eds = new HashSet<>();
        EmailData ed;
        Query q = em.createNativeQuery(
                "SELECT * FROM EMAILDATA a WHERE"
                + " (a.sender = ? AND a.type = ?)");
        q.setParameter(1, sender);
        q.setParameter(2, "Sent");
        List<Object[]> ob = q.getResultList();
        
        int i;
        for(Object[] o : ob){
            i = Integer.parseInt(o[0].toString());
            ed = new EmailData(i, o[3].toString(), o[2].toString(), o[4].toString(), o[6].toString(), o[5].toString(), o[1].toString());
            eds.add(ed);
        }
        return eds;
    }
    

    public boolean storeUser(User newUser) {
        UserData user = new UserData(newUser.getUsername(), newUser.getPassword());
        if (!existsUser(user)) {
            em.persist(user);
            return true;
        } else {
            return false;
        }
    }

    public boolean existsUser(UserData user) {
        UserData u = em.find(UserData.class, user.getUsername());
        if (u != null) {
            return true;
        } else {
            return false;
        }
    }

    public boolean authenticateUser(UserData user) {
        Query q = em.createNativeQuery(
                "SELECT a.username FROM USERDATA a WHERE"
                + " (a.username = ? AND a.password = ?)");
        q.setParameter(1, user.getUsername());
        q.setParameter(2, user.getPassword());
        List<UserData> ud = q.getResultList();
        if (ud.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

}
