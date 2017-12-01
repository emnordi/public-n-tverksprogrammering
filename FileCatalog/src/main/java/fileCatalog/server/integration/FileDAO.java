/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fileCatalog.server.integration;

import fileCatalog.server.model.User;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author Emil
 */
public class FileDAO {

    private static final String UTABLE = "USERS";
    private static final String FTABLE = "FILES";
    private PreparedStatement createUserSm;
    private PreparedStatement findUserSm;
    private PreparedStatement findAllUsersSm;
    private PreparedStatement deleteUserSm;
    private PreparedStatement changeAccessSm;

    public FileDAO() {
        try {
            Connection connection = createDatasource();
            prepareStatements(connection);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    private Connection createDatasource() throws ClassNotFoundException, SQLException {
        Connection connection = databaseCon();
        if (!tableExists(connection, UTABLE) && !tableExists(connection, FTABLE)) {
            Statement statement = connection.createStatement();
            statement.executeUpdate("create table " + UTABLE +  "(username varchar(50) primary key, password varchar(50))");
            statement.executeUpdate("create table " + FTABLE +  "(username varchar(50) primary key, filename varchar(50), access int)");
        }
        return connection;
    }
    
    public boolean registerUser(String username, String password){
        try {
            createUserSm.setString(1, username);
            createUserSm.setString(2, password);
            int rows = createUserSm.executeUpdate();
            if (rows != 1) {
                return false;
            }
            return true;
        } catch (SQLException sqle) {
            return false;
        }
    }
    public boolean authenticate(String username, String password){
        boolean valid;
        try {
            findUserSm.setString(1, username);
            findUserSm.setString(2, password);
            ResultSet user = findUserSm.executeQuery();
            user.next();
            if(user.getString(1).equals(username)){
                valid = true;
            }else{
                valid = false;
            }
        } catch (SQLException sqle) {
            valid = false;
        }
        return valid;
    }
    
/*
    public void unregisterUser(User account) throws BankDBException {
        try {
            deleteAccountStmt.setString(1, account.getHolderName());
            deleteAccountStmt.executeUpdate();
        } catch (SQLException sqle) {
            throw new BankDBException("Could not delete the account: " + account, sqle);
        }
    }
    */

    private boolean tableExists(Connection connection, String table) throws SQLException {
        //Name is in third column
        int col = 3;
        DatabaseMetaData dbm = connection.getMetaData();
        try (ResultSet rs = dbm.getTables(null, null, null, null)) {
            for (; rs.next();) {
                if (rs.getString(col).equals(table)) {
                    return true;
                }
            }
            return false;
        }
    }

    private void prepareStatements(Connection connection) throws SQLException {
        createUserSm = connection.prepareStatement("INSERT INTO "
                + UTABLE + " VALUES (?, ?)");
        findUserSm = connection.prepareStatement("SELECT * from "
                + UTABLE + " WHERE (username = ? AND password = ?)");
        findAllUsersSm = connection.prepareStatement("SELECT * from "
                + UTABLE);
        deleteUserSm = connection.prepareStatement("DELETE FROM "
                + UTABLE
                + " WHERE username = ?");
        changeAccessSm = connection.prepareStatement("UPDATE "
                + FTABLE
                + " SET access = ? WHERE filename= ? ");
    }

    private void listPubFiles(Connection connection) throws SQLException{
        Statement stmt = connection.createStatement();
        ResultSet publicFiles = stmt.executeQuery("select * from " + FTABLE + " where access = 1");
        while(publicFiles.next()){
            System.out.println(publicFiles.getInt(3));
        }
    }
    
    private Connection databaseCon() throws ClassNotFoundException, SQLException {
        //Loads to jvm
        Class.forName("org.apache.derby.jdbc.ClientXADataSource");
        return DriverManager.getConnection("jdbc:derby://localhost:1527/FileCatalog", "emil", "emil");
    }
}
