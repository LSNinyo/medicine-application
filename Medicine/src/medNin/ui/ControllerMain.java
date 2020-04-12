package medNin.ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import medNin.datamodel.InventoryData;
import medNin.datamodel.Medicine;
import medNin.main.Application;
import medNin.settings.Settings;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.function.Predicate;

public class ControllerMain {

    @FXML
    private BorderPane borderPane;
    @FXML
    private ListView<Medicine> medicineListView;
    @FXML
    private Label nameLabel;
    @FXML
    private Label quantityLabel;
    @FXML
    private Label priceLabel;
    @FXML
    private Label totalAmountLabel;
    @FXML
    private Spinner<Integer> spinner;
    @FXML
    private TextField searchField;
    @FXML
    private Label newLabel;
    @FXML
    private ListView<String> basketListView;
    @FXML
    private Button removeOrAddToInventoryButton;
    @FXML
    private Label inventoryModeLabel;
    @FXML
    private ToggleButton inventoryToggleButton;
    @FXML
    private Label spinnerLabel;
    @FXML
    private Label basketLabel;
    @FXML
    private Label totalLabel;

    private Application app = new Application();

    private FilteredList<Medicine> searchList;
    private Predicate<Medicine> wantAllMedicine;
    private Predicate<Medicine> wantSpecificLetter;
    private ContextMenu basketContextMenu;

    private double totalAmountValue = 0;

    private boolean inInventoryMode = false;

    public static final DecimalFormat priceFormat = new DecimalFormat("0.00");



    public void initialize() {
        // Adds a listener that listens to the what's selected and updates the
        // info field with the latest select.
        medicineListView.getSelectionModel().selectedItemProperty().addListener((observableValue, o, t1) -> {
            if (t1!=null) {
                Medicine selectedMedicine = medicineListView.getSelectionModel().getSelectedItem();
                nameLabel.setText(selectedMedicine.getName());
                priceLabel.setText(priceFormat.format(selectedMedicine.getPrice()));
                quantityLabel.setText(selectedMedicine.getQuantityString());
                spinner.getValueFactory().setValue(0);
            }
        });

        // A predicate that is used to filter a list. In this case it will
        // allow for the list to include any medicine.
        wantAllMedicine = medicine -> true;

        setUpMedicineList();
    }

    /**
     * Filters the list so that it shows all the medicine. It then populates
     * the medicineListView with the filtered list. It then also creates a
     * cell factory that listens for changes in the medicine and paints the
     * medicine red if the quantity is 0.
     */
    private void setUpMedicineList() {
        ObservableList<Medicine> obsInventory = FXCollections.observableArrayList();
        obsInventory.addAll(InventoryData.getInstance().getInventory());
        searchList = new FilteredList<Medicine>(obsInventory, wantAllMedicine);

        medicineListView.setItems(searchList);
        medicineListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        medicineListView.getSelectionModel().selectFirst();

        // cell factory
        medicineListView.setCellFactory(new Callback<ListView<Medicine>, ListCell<Medicine>>() {
            @Override
            public ListCell call(ListView listView) {
                ListCell<Medicine> cell = new ListCell<>() {

                    @Override
                    protected void updateItem(Medicine medicine, boolean b) {
                        super.updateItem(medicine, b);
                        // b stand for empty
                        if (b) {
                            setText(null);
                        } else {
                            setText(medicine.getName());
                            if (medicine.getQuantity() == 0) {
                                setTextFill(Color.RED);
                            } else {
                                // this fixes a bug
                                setTextFill(Color.BLACK);
                            }
                        }
                    }
                };
                return cell;
            }
        });
    }

    @FXML
    public void handleSpinnerPress() {
        int spinnerVal = spinner.getValue();

        if (app.addMedicineToBasketList(medicineListView.getSelectionModel().getSelectedItem(),
                spinnerVal, inInventoryMode)) {
            reloadBasketListView();
        } else {
            raiseInsufficientQuantityAlert();
        }
    }

    /**
     * Gets the current list from the Application instance and repopulates
     * the list of the basket.
     */
    private void reloadBasketListView() {
        ArrayList<Medicine> medicineList = app.getBasketMedicine();
        ArrayList<Integer> quantityList = app.getBasketPieces();
        totalAmountValue=0;
        ObservableList<String> basketList = FXCollections.observableArrayList();
        for (int i=0; i<medicineList.size(); i++) {
            double totalPrice = quantityList.get(i)*medicineList.get(i).getPrice();

            basketList.add(quantityList.get(i) + " x "+ medicineList.get(i).getName() +" - "
                    + priceFormat.format(totalPrice));
            totalAmountValue += totalPrice;
        }
        totalAmountLabel.setText(priceFormat.format(totalAmountValue));
        basketListView.setItems(basketList);
        setUpBasketList();
    }

    /**
     * Adds context menu to the basket list.
     */
    private void setUpBasketList() {
        basketContextMenu = new ContextMenu();
        MenuItem removeMenuItem = new MenuItem("Remove");
        removeMenuItem.setOnAction(actionEvent -> {
            int medIndex = basketListView.getSelectionModel().getSelectedIndex();
            app.removeMedicineFromBasket(medIndex);
            reloadBasketListView();
        });
        basketContextMenu.getItems().addAll(removeMenuItem);
        basketContextMenu.getStyleClass().add("med-search");

        basketListView.setContextMenu(basketContextMenu);
    }


    private void raiseInsufficientQuantityAlert() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Invalid sell attempt");
        alert.setHeaderText("You don't have enough in your inventory to sell this many!");
        alert.showAndWait();
    }

    /**
     * This method now uses the .startsWith() method of the name
     * of the medicine to check if the search starts with this.
     */
    @FXML
    public void handleSearchMain() {
        String searchInput = searchField.getText().toLowerCase().trim();

        // A predicate that is used to filter a list. In this case it will
        // allow for the list to include only medicine whose name starts
        // with the string searchInput.
        wantSpecificLetter = medicine -> {
            String name = medicine.getName().toLowerCase();
            if (name.startsWith(searchInput)) {
                return true;
            }
            return false;
        };

        searchList.setPredicate(wantSpecificLetter);
        medicineListView.getSelectionModel().selectFirst();
    }

    @FXML
    public void handleRemoveOrAddToInventory() {
        if (inInventoryMode) {
            app.addBasketToInventory();
        } else {
            app.removeBasketFromInventory();
        }
        quantityLabel.setText(medicineListView.getSelectionModel().getSelectedItem().getQuantity()+"");
        reloadBasketListView();
    }

    @FXML
    public void handleInventoryPressed() {
        if (!app.getBasketMedicine().isEmpty()) {
            // This makes sure that the toggle button gets selected if it is not
            // or de-selected if it is.
            inventoryToggleButton.setSelected(!inventoryToggleButton.isSelected());
            raiseBasketNotEmptyAlert();
        } else {
            if (inventoryToggleButton.isSelected()) {
                inInventoryMode = true;
                inventoryModeLabel.setVisible(true);
                spinnerLabel.setText("You will add:");
                basketLabel.setText("To be added to inventory:");
                removeOrAddToInventoryButton.setText("Add to inventory");
                totalLabel.setVisible(false);
                totalAmountLabel.setVisible(false);
            } else {
                inInventoryMode = false;
                inventoryModeLabel.setVisible(false);
                spinnerLabel.setText("You will sell:");
                basketLabel.setText("Basket:");
                removeOrAddToInventoryButton.setText("Sell");
                totalLabel.setVisible(true);
                totalAmountLabel.setVisible(true);
            }
        }
    }

    private void raiseBasketNotEmptyAlert() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Basket not empty");
        alert.setHeaderText("Your basket is not empty! "+"In order to change the mode you need to remove " +
                "or sell all of the items "+ "from the basket.");
        alert.showAndWait();
    }

    @FXML
    public void handleClearBasket() {
        app.clearBasket();
        reloadBasketListView();
    }

    @FXML
    public void showNewDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        // specifies the owner of the dialog - the top-level window
        dialog.initOwner(borderPane.getScene().getWindow());

        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/MedDialog.fxml"));
        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());
        } catch(IOException e) {
            System.out.println("Couldn't load dialog: " + e.getMessage());
        }

        DialogController dialogController = fxmlLoader.getController();
        dialogController.displayNewMedDialog(this, dialog);
    }

    public void sendNewMed(Medicine newMed) {
        app.addNewMedicine(newMed);
        setUpMedicineList();
        medicineListView.getSelectionModel().select(newMed);
    }

    @FXML
    public void showEditDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        // specifies the owner of the dialog - the top-level window
        dialog.initOwner(borderPane.getScene().getWindow());

        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/MedDialog.fxml"));
        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());
        } catch(IOException e) {
            System.out.println("Couldn't load dialog: " + e.getMessage());
        }

        DialogController dialogController = fxmlLoader.getController();
        dialogController.displayEditDialog(this, dialog, medicineListView.getSelectionModel().getSelectedItem());
    }


    public void editMedicine(Medicine editedMed) {
        Medicine oldMed = medicineListView.getSelectionModel().getSelectedItem();
        app.editMedicine(oldMed, editedMed);
    }

    @FXML
    public void showNewPathDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        // specifies the owner of the dialog - the top-level window
        dialog.initOwner(borderPane.getScene().getWindow());

        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/PathDialog.fxml"));
        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());
        } catch(IOException e) {
            System.out.println("Couldn't load dialog: " + e.getMessage());
        }

        DialogController dialogController = fxmlLoader.getController();
        dialogController.displayPathDialog(this, dialog);
    }

    public void editLogFolderPath(String path) {
        Settings.savePath(path);
    }

    @FXML
    public void deleteMed() {
        raiseDeleteConfirmationAlert();
        setUpMedicineList();
    }

    /**
     * Raises alert. If it's confirmed - calls a method in the Application class
     * that deletes the medicine.
     */
    private void raiseDeleteConfirmationAlert() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete medicine confirmation");
        alert.setHeaderText("Delete "+
                        medicineListView.getSelectionModel().getSelectedItem().getName()+
                        " confirmation!");
        alert.setContentText("Are you sure you want delete "+
                medicineListView.getSelectionModel().getSelectedItem().getName()+"?");

        alert.showAndWait()
                .filter(response -> response == ButtonType.OK)
                .ifPresent(response -> {
                    app.removeMedicine(medicineListView.getSelectionModel().getSelectedItem());
                });
    }
}
