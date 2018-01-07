package EmailApplication.server.net;

import EmailApplication.constructors.Email;
import EmailApplication.constructors.User;
import EmailApplication.server.controller.Controller;
import EmailApplication.server.model.EmailData;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.JsonObject;
import javax.json.spi.JsonProvider;
import javax.websocket.Session;

@ApplicationScoped
public class SessionHandler {

    @Inject
    private Controller cont;

    private int emailId = 0;
    //Stores active session
    private final Set<User> users = new HashSet<>();
    private Session reciever;
    private Session sender;
    private EmailData ed;
    private EmailData eds;
    private Set<EmailData> rmails;
    private Set<EmailData> smails;
//Add user
    public void addUser(User newUser) {
        JsonProvider provider = JsonProvider.provider();
        JsonObject msg;
        if (cont.authenticateUser(newUser)) {
            users.add(newUser);
            msg = provider.createObjectBuilder()
                    .add("kind", "login")
                    .add("user", newUser.getUsername()).build();
            
            startupMails(newUser);
        } else {
            msg = provider.createObjectBuilder()
                    .add("kind", "msg")
                    .add("message", "Invalid credentials, try again!").build();
        }
        sendToOneSession(newUser.getSession(), msg);
    }
    
    public User getUser(String username, Session session){
        User user = null;
        for(User u : users){
            if(u.getUsername().equals(username)){
                u.setSession(session);
                user = u;
            }
        }
        if(user == null){
            user = new User(username, session, " ");
            users.add(user);
        }
        return user;
    }

    public void registerUser(User newUser) {
        JsonProvider provider = JsonProvider.provider();
        JsonObject msg;
        if (cont.registerUser(newUser)) {
            users.add(newUser);
            msg = provider.createObjectBuilder()
                    .add("kind", "register")
                    .add("user", newUser.getUsername()).build();
        } else {
            msg = provider.createObjectBuilder()
                    .add("kind", "msg")
                    .add("message", "That username is taken, please pick another!").build();
        }
        sendToOneSession(newUser.getSession(), msg);
    }
 
    public void startupMails(User username) {
        rmails = cont.getRecieved(username.getUsername());
        smails = cont.getSent(username.getUsername());
        if (!rmails.isEmpty()) {
            for (EmailData mail : rmails) {
                JsonObject addMessage = createMessage(mail);
                sendToOneSession(username.getSession(), addMessage);
            }
        }
        if (!smails.isEmpty()) {
            for (EmailData mail : smails) {
                JsonObject addMessage = createMessage(mail);
                sendToOneSession(username.getSession(), addMessage);
            }
        }
    }

    public void removeUser(Session session) {
        for (User user : users) {
            if (user.getSession().equals(session)) {
                users.remove(user);
                System.out.println("User removed");
            }
        }
    }

    public void toggleEmail(int id, Session session) {
        JsonProvider provider = JsonProvider.provider();
        JsonObject addMessage;
                if(cont.toggleMail(id)){
                    EmailData mail = cont.getMailById(id);
                    addMessage = createMessage(mail);
                }else{
                    addMessage = provider.createObjectBuilder()
                    .add("kind", "error")
                    .add("message", "An error occured, mail status could not be updated!").build();
                }
        sendToOneSession(session, addMessage);
        }
    
    private void sendToOneSession(Session session, JsonObject message) {
        try {
            session.getBasicRemote().sendText(message.toString());
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public void removeMail(int id, Session session) {
        JsonProvider provider = JsonProvider.provider();
        JsonObject addMessage;
                if(cont.removeMail(id)){
                    addMessage = provider.createObjectBuilder()
                    .add("kind", "success")
                    .add("message", "Email deleted!").build();
                }else{
                    addMessage = provider.createObjectBuilder()
                    .add("kind", "error")
                    .add("message", "An error occured, email could not be deleted!").build();
                }
        sendToOneSession(session, addMessage);
    }

    public void addMail(Email email, Email sMail) {
        ed = cont.addMail(email);
        eds = cont.addMail(sMail);
        emailId++;
        for (User user : users) {
            if (user.getUsername().equals(email.getReciever())) {
                reciever = user.getSession();
                JsonObject addMessage = createMessage(ed);
                sendToOneSession(reciever, addMessage);
            }
            if (user.getUsername().equals(email.getSender())) {
                sender = user.getSession();
                JsonObject addMessage = createMessage(eds);
                sendToOneSession(sender, addMessage);
            }
        }
    }

    private JsonObject createMessage(EmailData mail) {
        JsonProvider provider = JsonProvider.provider();
        JsonObject addMessage = provider.createObjectBuilder()
                .add("kind", "mail")
                .add("id", mail.getEid())
                .add("to", mail.getReciever())
                .add("from", mail.getSender())
                .add("type", mail.getType())
                .add("status", mail.getStatus())
                .add("subject", mail.getSubject())
                .add("message", mail.getMessage())
                .build();
        return addMessage;
    }

}
