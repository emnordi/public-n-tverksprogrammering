
package EmailApp.server.model;
import EmailApp.both.User;



public class DatabaseHandler {
  //  private EmailDAO edao = new EmailDAO();
    
    public boolean registerUser(User newUser) {
        boolean ruser;
        ruser = edao.registerUser(newUser.getUsername(), newUser.getPassword());
        if (ruser) {
            return true;
        } else {
            return false;
        }
    }
}
