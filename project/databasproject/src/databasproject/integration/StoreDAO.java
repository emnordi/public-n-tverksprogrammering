package databasproject.integration;

import java.sql.*;

public class StoreDAO {

    private PreparedStatement getAllProducts;
    private PreparedStatement notInStock;
    private PreparedStatement addBiS;

    // DB connection variable
    protected Connection connection;
    // DB access variables
    private String URL = "jdbc:mysql://localhost:3306/spelrvi";
    private String driver = "com.mysql.jdbc.Driver";
    private String userID = "root";
    private String password = "";
    private String prod = "produkt";

    public StoreDAO() {
        try {
            connection = connect();
            prepareStatements(connection);
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
    }

    /*
    * Establishes connection with database, set autocommit to false.
    */
    public Connection connect() {
        Connection con;
        try {
            // register the driver with DriverManager
            Class.forName(driver);
            con = DriverManager.getConnection(URL, userID, password);
            con.setAutoCommit(false);
            return con;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public void closeCon() throws SQLException{
        connection.close();
    }

    private void prepareStatements(Connection connection) throws SQLException {
        getAllProducts = connection.prepareStatement("SELECT distinct * FROM "
                + prod);
        notInStock = connection.prepareStatement("SELECT * from Butik where butiksID in (SELECT butiksID from "
                + "lagerrad"
                + " WHERE (Saldo = 0 AND EAN = ?))");
        addBiS = connection.prepareStatement("INSERT INTO "
                + "bevakning (epost, ean, butiksid)"
                + " VALUES (?, ?, ?)");

    }
    //Hämtar alla produkter från databasen
    public ResultSet getProducts() throws SQLException {
        ResultSet products = getAllProducts.executeQuery();
        if(products.isBeforeFirst()){
                return products;
            } else {
                return null;
            }
    }
    
    public boolean addBiS(String input) {
        try{
        String[] inputs = input.split(" ");
        addBiS.setString(1, inputs[0]);
        addBiS.setString(2, inputs[1]);
        addBiS.setString(3, inputs[2]);
        int rows = addBiS.executeUpdate();
        if(rows > 0){
            connection.commit();
                return true;
                
            } else {
                return false;
            }
        
        }catch(SQLException e){
            return false;
        }
        
    }

    //Hämtar alla butiker som inte har en produkt i lager
    public ResultSet notInStock(String ean) throws SQLException {
            notInStock.setString(1, ean);
            ResultSet res = notInStock.executeQuery();
            if(res.isBeforeFirst()){
                return res;
            } else {
                return null;
            }
    }

}
