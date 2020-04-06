package MedApp;

import MedApp.datamodel.Medicine;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;

import java.util.Optional;


public class DialogController {

    private ControllerMain controllerMain;

    @FXML
    private Text headerText;
    @FXML
    private TextField medPriceField;
    @FXML
    private TextField medNameField;
    @FXML
    private Spinner<Integer> quantitySpinner;
    @FXML
    private ButtonType buttonCancel;

    private static final String NOT_ENOUGH_INFO_ALERT_MESSAGE =
            "You have not provided information for all of the fields.";

    private static final String INVALID_PRICE_ALERT_MESSAGE =
            "Please provide numerical value for the price!";


    /**
     * Displays the new medicine dialog.
     */
    public void displayNewMedDialog(ControllerMain controller, Dialog<ButtonType> dialog) {
        dialog.setTitle("Add new medicine");
        headerText.setText("Please specify the medicine you want to add");

        controllerMain=controller;

        // not just add buttons and listen but actually display the dialog with .showAndWait()
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        dialog.getDialogPane().getStylesheets().add(
                "MedApp/styles.css");
        dialog.getDialogPane().lookupButton(ButtonType.OK).getStyleClass().add("button1");
        dialog.getDialogPane().lookupButton(ButtonType.CANCEL).getStyleClass().add("button1");

        final Button btOK = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        btOK.addEventFilter(ActionEvent.ACTION, actionEvent -> {
            Medicine newMed = processInput();
            if (newMed!=null) {
                controller.sendNewMed(newMed);
            } else {
                actionEvent.consume();
            }
        });
        Optional<ButtonType> result = dialog.showAndWait();
    }

    /**
     * Displays the edit medicine dialog with the existing values of the
     * medicine filled in.
     */
    public void displayEditDialog(ControllerMain controller, Dialog<ButtonType> dialog, Medicine medicine) {
        dialog.setTitle("Edit " + medicine.getName());
        headerText.setText("Please edit the medicine values");

        controllerMain=controller;

        medNameField.setText(medicine.getName());
        medPriceField.setText(medicine.getPriceString());
        quantitySpinner.getValueFactory().setValue(medicine.getQuantity());

        // not just add buttons and listen but actually display the dialog with .showAndWait()
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        dialog.getDialogPane().getStylesheets().add(
                "MedApp/styles.css");
        dialog.getDialogPane().lookupButton(ButtonType.OK).getStyleClass().add("button1");
        dialog.getDialogPane().lookupButton(ButtonType.CANCEL).getStyleClass().add("button1");

        final Button btOK = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        btOK.addEventFilter(ActionEvent.ACTION, actionEvent -> {
            raiseEditConfirmationAlert();
        });
        Optional<ButtonType> result = dialog.showAndWait();
    }

    public Medicine processInput() {
        String medName = medNameField.getText();
        Integer quantity = quantitySpinner.getValue();
        double price;
        try {
            price = Double.parseDouble(medPriceField.getText());
        } catch (NumberFormatException nfe) {
            raiseInvalidSaveAttemptAlert(INVALID_PRICE_ALERT_MESSAGE);
            return null;
        }

        if (!medName.isEmpty() && quantity!=null) {
            // process data and create a medicine instance
            return new Medicine(medName,price, quantity);
        } else {
            raiseInvalidSaveAttemptAlert(NOT_ENOUGH_INFO_ALERT_MESSAGE);
        }
        return null;
    }

    private void raiseInvalidSaveAttemptAlert(String reason) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Invalid save attempt");
        alert.setHeaderText("Invalid save attempt");
        alert.setContentText(reason);

        alert.showAndWait();
    }

    private void raiseEditConfirmationAlert() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Save changes confirmation");
        alert.setHeaderText("Save changes confirmation");
        alert.setContentText("Are you sure you want to save the changes?");

        alert.showAndWait()
            .filter(response -> response == ButtonType.OK)
            .ifPresent(response -> {
                Medicine editedMed = processInput();
                if (editedMed!=null) {
                    controllerMain.editMedicine(editedMed);
                }
            });
    }

}
