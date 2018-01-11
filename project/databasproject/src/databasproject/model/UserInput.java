
package databasproject.model;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

public class UserInput {
    private final String result;

    public UserInput(Window owner) {
      final Stage dialog = new Stage();

      dialog.setTitle("Enter Information: ");
      dialog.initOwner(owner);
      dialog.initStyle(StageStyle.UTILITY);
      dialog.initModality(Modality.WINDOW_MODAL);
      dialog.setX(owner.getX() + owner.getWidth());
      dialog.setY(owner.getY());

      final TextField textField = new TextField();
      final Button submitButton = new Button("Submit");
      submitButton.setDefaultButton(true);
      submitButton.setOnAction(new EventHandler<ActionEvent>() {
        @Override public void handle(ActionEvent t) {
          dialog.close();
        }
      });
      textField.setMinHeight(TextField.USE_PREF_SIZE);

      final VBox layout = new VBox(10);
      layout.setAlignment(Pos.CENTER_RIGHT);
      layout.setStyle("-fx-background-color: azure; -fx-padding: 10;");
      layout.getChildren().setAll(
        textField, 
        submitButton
      );

      dialog.setScene(new Scene(layout));
      dialog.showAndWait();

      result = textField.getText();
    }

    public String getResult() {
      return result;
    }
  }
