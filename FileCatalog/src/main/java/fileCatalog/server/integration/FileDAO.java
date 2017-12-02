/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fileCatalog.server.integration;

import fileCatalog.server.fileHandler.FileHandle;
import fileCatalog.server.model.User;
import java.io.IOException;
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
    private PreparedStatement addFileSm;
    private PreparedStatement getAccessSm;
    private PreparedStatement deleteFileSm;

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
            statement.executeUpdate("create table " + UTABLE + "(username varchar(50) primary key, password varchar(50))");
            statement.executeUpdate("create table " + FTABLE + "(filename varchar(50) primary key, username varchar(50), access int, size int, writable int)");
        }
        return connection;
    }

    public boolean registerUser(String username, String password) {
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

    public boolean authenticate(String username, String password) {
        boolean valid;
        try {
            findUserSm.setString(1, username);
            findUserSm.setString(2, password);
            ResultSet user = findUserSm.executeQuery();
            user.next();
            if (user.getString(1).equals(username)) {
                valid = true;
            } else {
                valid = false;
            }
        } catch (SQLException sqle) {
            valid = false;
        }
        return valid;
    }

    public void unregisterUser(String username) {
        try {
            deleteUserSm.setString(1, username);
            deleteUserSm.executeUpdate();

        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
    }

    public void uploadFile(String filename, int access, String username, int filesize, int writable) {
        try {
            addFileSm.setString(1, filename);
            addFileSm.setString(2, username);
            addFileSm.setInt(3, access);
            addFileSm.setInt(4, filesize);
            addFileSm.setInt(5, writable);
            int rows = addFileSm.executeUpdate();
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
    }

    public boolean downloadFile(String filename, int access, String username, int filesize) {
        try {
            ResultSet result = null;
            addFileSm.setString(1, filename);
            addFileSm.setString(2, username);
            addFileSm.setInt(3, access);
            addFileSm.setInt(4, filesize);
            int rows = addFileSm.executeUpdate();
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
        return true;
    }

    public boolean deleteFile(String filename, String usernam) {
        FileHandle fh = new FileHandle();
        try {
            int acc = -1;
            int writ = -1;
            ResultSet result = null;
            getAccessSm.setString(1, filename);
            result = getAccessSm.executeQuery();
            if (result.next()) {
                acc = result.getInt("ACCESS");
                writ = result.getInt("WRITABLE");
                String user = result.getString("USERNAME");
            
            switch (acc) {
                case 1:
                    if (writ == 1 || user.equals(usernam)) {
                        deleteFileSm.setString(1, filename);
                        deleteFileSm.executeUpdate();
                        return true;
                    }else{
                        return false;
                    }
                case 0:
                    if (user.equals(usernam)) {
                        deleteFileSm.setString(1, filename);
                        deleteFileSm.executeUpdate();
                        return true;
                    }else{
                        return false;
                    }
                default:
                    return false;
            }
            }

        } catch (SQLException sqle) {
            sqle.printStackTrace();
            return false;
        }
        return false;
    }

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
        addFileSm = connection.prepareStatement("INSERT INTO "
                + FTABLE + " VALUES (?, ?, ?, ?, ?)");
        deleteFileSm = connection.prepareStatement("DELETE FROM "
                + FTABLE
                + " WHERE filename = ?");
        changeAccessSm = connection.prepareStatement("UPDATE "
                + FTABLE + " SET access = ? WHERE filename = ? ");
        getAccessSm = connection.prepareStatement("SELECT * from "
                + FTABLE + " WHERE filename = ? ");
    }

    private void listPubFiles(Connection connection) throws SQLException {
        Statement stmt = connection.createStatement();
        ResultSet publicFiles = stmt.executeQuery("select * from " + FTABLE + " where access = 1");
        while (publicFiles.next()) {
            System.out.println(publicFiles.getInt(3));
        }
    }

    private Connection databaseCon() throws ClassNotFoundException, SQLException {
        //Loads to jvm
        Class.forName("org.apache.derby.jdbc.ClientXADataSource");
        return DriverManager.getConnection("jdbc:derby://localhost:1527/FileCatalog", "emil", "emil");
    }
}
