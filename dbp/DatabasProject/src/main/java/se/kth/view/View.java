/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.kth.view;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import se.kth.controller.Controller;

/**
 *
 * @author Emil
 */
public class View {

    private java.util.Scanner in = new java.util.Scanner(System.in);
    private boolean connected;
    private boolean work;
    private ResultSet res;

    public void menu() {
        Controller cont = new Controller();
        System.out.println("Welcome to the Store menu!");
        connected = true;
        while (connected) {
            work = false;
            System.out.println("You now have three options. They are the following: \n"
                    + "Show all products (A) \n"
                    + "Show all stores that do not have a product (B) \n"
                    + "Add a 'back in stock' entry (C)");
            try {
                String read = in.nextLine();
                switch (read.toLowerCase()) {
                    case ("a"):
                        res = cont.showProduct();
                        if(res != null){
                            printer(res);
                        }else{
                            System.out.println("There are no products");
                        }
                        break;
                    case ("b"):
                        res = null;
                        while (res == null) {
                            System.out.println("Skriv in ean för en produkt: ");
                            String ean = in.nextLine();
                            res = cont.showStores(ean);
                            if (res != null) {
                               printer(res);
                            } else {
                                System.out.println("Det fanns ingen butik som inte har produkten, försök igen");
                            }
                        }
                        break;
                    case ("c"):
                        work = false;
                        while(!work){
                        System.out.println("Please enter {email} {ean} {shopid}");
                        String input = in.nextLine();
                        work = cont.addBis(input);
                        if(work){
                            System.out.println("Successfully added!");
                            work = true;
                        }else{
                            System.out.println("Input paramaters wrong, please try again:");
                        }
                        }
                        
                        break;
                    case ("quit"):
                        connected = false;
                        break;
                }

            } catch (SQLException sqle) {
                sqle.printStackTrace();
            }

        }

    }

    private void printer(ResultSet res) throws SQLException{
        ResultSetMetaData rsmd = res.getMetaData();
        int colnum = rsmd.getColumnCount();
        while (res.next()) {

            for (int i = 1; i <= colnum; i++) {
                if (i > 1) {
                    System.out.print("  ");
                }
                String columnValue = res.getString(i);
                System.out.print(rsmd.getColumnName(i) + ": " + columnValue + "\n");
                if (i > 8) {
                    System.out.println("");
                }
            }
        }
    }

}
