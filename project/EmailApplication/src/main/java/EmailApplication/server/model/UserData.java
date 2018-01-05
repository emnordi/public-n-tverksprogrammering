
package EmailApplication.server.model;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class UserData implements Serializable {

    @Id
    private String username;
    private String password;
    
    //Create instance of UserData
    public UserData() {
    }

    public UserData(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setSender(String username) {
        this.username = username;
    }

    public void setReciever(String password) {
        this.password = password;
    }
}
