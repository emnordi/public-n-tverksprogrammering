package databasproject;

import databasproject.integration.StoreDAO;
import databasproject.model.UserDropdown;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import databasproject.model.UserInput;
import javafx.scene.layout.GridPane;

/**
 * Contains eventhandlers
 */
public class DatalagringFXMLController implements Initializable {

    StoreDAO sdb = new StoreDAO();
    @FXML
    private TextField selectionField;

    @FXML
    private TextArea textArea;

    @FXML
    private GridPane gridPane;

    @FXML
    private void selectHandler(ActionEvent ae) throws SQLException {
        UserInput prompt;
        UserDropdown drop;
        String select;
        String[] selections = new String[2];
        String selection = selectionField.getText();
        switch (selection.toLowerCase()) {
            case ("a"):
                drop = new UserDropdown(
                        gridPane.getScene().getWindow()
                );
                selections = drop.getResult();
                showProduct();
                mainMenu();
                break;
            case ("b"):
                prompt = new UserInput(
                        gridPane.getScene().getWindow()
                );
                select = prompt.getResult();
                showStores(select);
                mainMenu();
                break;
            case ("c"):
                addMenu("Please enter {email} {ean} {shopid} \n");
                prompt = new UserInput(
                        gridPane.getScene().getWindow()
                );
                select = prompt.getResult();
                addBis(select);
                break;
        }
        //textArea.appendText(selection);
    }

    @FXML
    private void startHandler(ActionEvent ae) {
        mainMenu();
    }

    @FXML
    public void mainMenu() {
        addMenu("Welcome to the Store menu! \n");
        addMenu("You now have three options. They are the following: \n"
                + "Show all products (A) \n"
                + "Show all stores that do not have a product (B) \n"
                + "Add a 'back in stock' entry (C) \n");
    }

    @FXML
    public void addMenu(String item) {
        textArea.appendText(item);
    }

    @FXML
    void addRS(ResultSet res) throws SQLException {
        ResultSetMetaData rsmd = res.getMetaData();
        int colnum = rsmd.getColumnCount();
        while (res.next()) {

            for (int i = 1; i <= colnum; i++) {
                if (i > 1) {
                    addMenu("  ");
                }
                String columnValue = res.getString(i);
                addMenu(rsmd.getColumnName(i) + ": " + columnValue + "\n");
                if (i > 8) {
                    addMenu("");
                }
            }
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    public void showProduct() throws SQLException {
        ResultSet rs = sdb.getProducts();
        addRS(rs);
    }

    public void showStores(String ean) throws SQLException {
        ResultSet rs = sdb.notInStock(ean);
        addRS(rs);
    }

    public void addBis(String input) throws SQLException {
        boolean added = sdb.addBiS(input);
        if (added) {
            addMenu("Successfully added! \n");
        } else {
            addMenu("Input paramaters wrong, please try again \n");
        }
    }
}
