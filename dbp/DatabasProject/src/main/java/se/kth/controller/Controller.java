/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.kth.controller;

import java.sql.ResultSet;
import java.sql.SQLException;
import se.kth.databaslabb.StoreDAO;

/**
 *
 * @author Emil
 */
public class Controller {
    StoreDAO sdb = new StoreDAO();
    
    public ResultSet showProduct() throws SQLException{
        return sdb.getProducts();
    }
    public ResultSet showStores(String ean) throws SQLException{
        return sdb.notInStock(ean);
    }
    public boolean addBis(String input) throws SQLException{
       return sdb.addBiS(input);
    }
}
