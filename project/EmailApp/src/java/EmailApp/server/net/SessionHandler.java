package EmailApp.server.net;

import EmailApp.both.Email;
import EmailApp.both.User;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.enterprise.context.ApplicationScoped;
import javax.json.JsonObject;
import javax.json.spi.JsonProvider;
import javax.websocket.Session;

@ApplicationScoped
public class SessionHandler {
    private int emailId = 0;
    //Stores active session
    private final Set<User> users = new HashSet<>();
    private final Set<Session> sessions = new HashSet<>();
    private final Set<String> msgs = new HashSet<>();
    //Stores emails until db is setup
    private final Set<Email> emails = new HashSet<>();
    private Session reciever;
    //Add user
    
    public void addUser(User newUser) {
        users.add(newUser);
        JsonProvider provider = JsonProvider.provider();
        JsonObject msg = provider.createObjectBuilder()
                .add("kind", "msg")
                .add("message", "Logged on").build();
        sendToOneSession(newUser.getSession(), msg);
    }
    public void removeUser(Session session) {
        for(User user : users){
            if(user.getSession().equals(session)){
                users.remove(user);
                sessions.remove(user.getSession());
                System.out.println("User removed");
            }
        }
    }
    
    /*
    public void addSession(Session session) {
        sessions.add(session);
        msgs.add("number 1");
        msgs.add("Connected");
        JsonProvider provider = JsonProvider.provider();
        for (String email : msgs) {
           // JsonObject addMessage = createMessage(email);
           JsonObject msg = provider.createObjectBuilder()
                    .add("kind", "msg")
                   .add("message", email).build();
            sendToOneSession(session, msg);
        }
    }*/
    
    //Remove user
    public void removeSession(Session session) {
        sessions.remove(session);
    }
    
    private void sendToAllSessions(JsonObject message) {
        for (Session session : sessions) {
            sendToOneSession(session, message);
        }
    }

    private void sendToOneSession(Session session, JsonObject message) {
        try {
            session.getBasicRemote().sendText(message.toString());
        } catch (IOException ioe) {
            sessions.remove(session);
            ioe.printStackTrace();
        }
    }
    
    public void addMail(Email email) {
        emails.add(email);
        emailId++;
        for(User user : users){
            if(user.getUsername().equals(email.getReciever())){
                reciever = user.getSession();
                JsonObject addMessage = createMessage(email);
                sendToOneSession(reciever, addMessage);
            }
            //TODO put into database
        }
        
        //sendToAllSessions(addMessage);
    }
    
    private JsonObject createMessage(Email mail) {
        JsonProvider provider = JsonProvider.provider();
        JsonObject addMessage = provider.createObjectBuilder()
                .add("kind", "mail")
                .add("to", mail.getReciever())
                .add("from", mail.getSender())
                .add("type", mail.getType())
                .add("status", mail.getStatus())
                .add("subject", mail.getSubject())
                .add("message", mail.getMessage())
                .build();
        return addMessage;
    }
    
    /*
    public List<Email> getMails() {
        return new ArrayList<>(emails);
    }

    public void addMail(Email email) {
        email.setId(emailId);
        emails.add(email);
        emailId++;
        JsonObject addMessage = createMessage(email);
        sendToAllSessions(addMessage);
    }

    public void removeMail(int id) {
        Email email = getMailById(id);
        if (email != null) {
            emails.remove(email);
            JsonProvider provider = JsonProvider.provider();
            JsonObject removeMessage = provider.createObjectBuilder()
                    .add("action", "remove")
                    .add("id", id)
                    .build();
            sendToAllSessions(removeMessage);
        }
    }

    public void markRead(int id) {
        JsonProvider provider = JsonProvider.provider();
        Email email = getMailById(id);
        if (email != null) {
            if ("Unread".equals(email.getStatus())) {
                email.setStatus("Read");
            } else {
                email.setStatus("Unread");
            }
            JsonObject updateDevMessage = provider.createObjectBuilder()
                    .add("action", "toggle")
                    .add("id", email.getId())
                    .add("status", email.getStatus())
                    .build();
            sendToAllSessions(updateDevMessage);
        }
    }

    private Email getMailById(int id) {
         for (Email email : emails) {
            if (email.getId() == id) {
                return email;
            }
        }
        return null;
    }

    private JsonObject createMessage(Email mail) {
        JsonProvider provider = JsonProvider.provider();
        JsonObject addMessage = provider.createObjectBuilder()
                .add("action", "add")
                .add("id", mail.getId())
                .add("to", mail.getReciever())
                .add("from", mail.getSender())
                .add("type", mail.getType())
                .add("status", mail.getStatus())
                .add("subject", mail.getSubject())
                .add("message", mail.getMessage())
                .build();
        return addMessage;
    }
*/
    

    

}
