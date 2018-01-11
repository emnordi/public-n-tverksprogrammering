
package databasproject.model;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

public class UserDropdown {
    private final String[] result;

    public UserDropdown(Window owner) {
      final Stage dialog = new Stage();
      result = new String[2];
      dialog.setTitle("Enter Information: ");
      dialog.initOwner(owner);
      dialog.initStyle(StageStyle.UTILITY);
      dialog.initModality(Modality.WINDOW_MODAL);
      dialog.setX(owner.getX() + owner.getWidth());
      dialog.setY(owner.getY());

      final ComboBox comboBox = new ComboBox();
      final ComboBox comboBox2 = new ComboBox();
      final Button submitButton = new Button("Submit");
      submitButton.setDefaultButton(true);
      submitButton.setOnAction(new EventHandler<ActionEvent>() {
        @Override public void handle(ActionEvent t) {
          dialog.close();
        }
      });
      comboBox.setMinWidth(150.0);
      comboBox2.setMinWidth(150.0);
      final VBox layout = new VBox(10);
      layout.setAlignment(Pos.CENTER_RIGHT);
      layout.setStyle("-fx-background-color: azure; -fx-padding: 10;");
      layout.getChildren().setAll(
        comboBox, 
        comboBox2, 
        submitButton
      );

      dialog.setScene(new Scene(layout));
      dialog.showAndWait();

      result[0] = comboBox.getPromptText();
      result[1] = comboBox2.getPromptText();
    }

    public String[] getResult() {
      return result;
    }
  }
