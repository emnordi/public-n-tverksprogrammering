/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fileCatalog.server.model;

import fileCatalog.all.Fclient;
import fileCatalog.all.UserCredentials;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 *
 * @author Emil
 */
public class DatabaseHandle {

    private final Random idGenerator = new Random();
    private final Map<Long, User> participants = Collections.synchronizedMap(new HashMap<>());

    public long createParticipant(Fclient remoteNode, UserCredentials credentials) {
        long userId = idGenerator.nextLong();
        User newUser = new User(userId, credentials.getUsername(),
                remoteNode, this);
        participants.put(userId, newUser);
        return userId;
    }

    // public User findUser(long id) {
    //    return participants.get(id);
    //}
    public void removeParticipant(long id) {
        participants.remove(id);
    }

    public void broadcast(String msg, long id) {
        synchronized (participants) {
            participants.get(id).send(msg);
        }
    }

}
