/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.kth.databaslabb;

/* This program is an example used to illustrate how JDBC works.
 ** It uses the JDBC driver for MySQL.
 **
 ** This program was originally written by nikos dimitrakas
 ** on 2007-08-31 for use in the basic database courses at DSV.
 **
 ** There is no error management in this program.
 ** Instead an exception is thrown. Ideally all exceptions
 ** should be caught and managed appropriately. But this 
 ** program's goal is only to illustrate the basic JDBC classes.
 **
 ** Last modified by nikos on 2015-10-07
 */
import java.sql.*;

public class DBJDBCM {

    private PreparedStatement getCarBrands;
    private PreparedStatement getAllCars;
    private PreparedStatement changeCarColour;

    // DB connection variable
    static protected Connection con;
    // DB access variables
    private String URL = "jdbc:mysql://localhost:3306/labb?zeroDateTimeBehaviour=convertToNull";
    private String driver = "com.mysql.jdbc.Driver";
    private String userID = "root";
    private String password = "";
    private String bil = "bil";
    private String pers = "person";
    private int counter;
    private boolean getInput;
    private static java.util.Scanner in = new java.util.Scanner(System.in);

    // method for establishing a DB connection
    public void connect() {
        try {
            // register the driver with DriverManager
            Class.forName(driver);
            //create a connection to the database
            con = DriverManager.getConnection(URL, userID, password);
            // Set the auto commit of the connection to false.
            // An explicit commit will be required in order to accept
            // any changes done to the DB through this connection.
            con.setAutoCommit(false);
            //Some logging
            System.out.println("Connected to " + URL + " using " + driver);
            prepareStatements(con);
            getInput = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void prepareStatements(Connection connection) throws SQLException {
        getCarBrands = connection.prepareStatement("SELECT distinct marke FROM "
                + bil);
        getAllCars = connection.prepareStatement("SELECT regnr, marke, farg from "
                + bil
                + " WHERE agare in (SELECT id FROM " + pers + " WHERE stad = ?)");
        changeCarColour = connection.prepareStatement("UPDATE "
                + bil
                + " SET farg = ? WHERE regnr = ?");

    }
    //Hämtar alla bilmärken
    private void getBrand() throws SQLException {
        ResultSet brands = getCarBrands.executeQuery();
        while (brands.next()) {
            System.out.println(brands.getString(1));
        }
    }
    //Hämtar alla bilar för en viss stad
    private void getCars() throws SQLException {
        counter = 0;
        getInput = true;
        System.out.println("Skriv in en stad för att få samtliga bilar i staden");
        while (getInput) {
            String stad = in.nextLine();
            getAllCars.setString(1, stad);
            ResultSet result = getAllCars.executeQuery();
            if (result.next()) {
                getInput = false;
                do{
                    System.out.println("BIL " + ++counter);
                    System.out.println(result.getString("REGNR") + " " + result.getString("MARKE") + " " + result.getString("FARG"));
                }while (result.next());
            } else {
                System.out.println("Det fanns ingen bil i den valda staden, försök igen");
            }
        }
    }
    //Uppdaterar en bils färg
    private void updateCarColour() throws SQLException {
        getInput = true;
        System.out.println("Skriv in ett registreringsnummer och en färg för att ändra färg på din bil");
        while (getInput) {
            String regfarg = in.nextLine();
            String[] regfargArray = regfarg.split(" ");
            changeCarColour.setString(1, regfargArray[1]);
            changeCarColour.setString(2, regfargArray[0]);
            int rows = changeCarColour.executeUpdate();
            if (rows > 0) {
            getInput = false;
            System.out.println("Din bils färg är nu " + regfargArray[1]);
            }else
            System.out.println("Din inmatning gav ingen matchning på en bil, försök igen");
        }
    }

    public static void main(String[] argv) throws Exception {
        // Create a new object of this class.
        DBJDBCM t = new DBJDBCM();
        boolean connected = true;
        // Call methods on the object t.
        System.out.println("-------- connect() ---------");
        t.connect();
        while(connected){
            System.out.println("Välkommen till huvudmenyn, du kan välja att:\n (a) Visa alla märken,\n"
                    + " (b) Hämta alla bilar för en viss stad\n"
                    + " (c) Uppdatera en bils färg.\n Skriv 'quit' för att avsluta");
            switch(in.nextLine().toLowerCase()){
                case("a"):
                t.getBrand();
                break;
                case("b"):
                t.getCars();
                break;
                case("c"):
                t.updateCarColour();
                con.commit();
                break;
                case("quit"):
                    connected = false;
                    break;
                default:
                    System.out.println("Fel input");
            }
        }
        
        con.close();
    }
}
