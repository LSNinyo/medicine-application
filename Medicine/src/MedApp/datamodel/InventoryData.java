package MedApp.datamodel;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;

public class InventoryData {

    private static InventoryData instance = new InventoryData();
    private ObservableList<Medicine> inventory = FXCollections.observableArrayList();
    // The quantities of the medicines stored in toBeUpdated are the
    // amounts which should be incremented!
    private ArrayList<Medicine> toBeUpdated = new ArrayList<>();

    private int qtyChangeCount = 0;

    public static InventoryData getInstance() {
        return instance;
    }

    public ObservableList<Medicine> getInventory() {
        return inventory;
    }


    /**
     * If the medicine provided is completely new - it adds it
     * to the inventory. If it's already existing - it just
     * increases its quantity.
     */
    public void addMedicine(Medicine newMedicine) {
        Medicine correspondingMedicine = inInventory(newMedicine);
        if (correspondingMedicine != null) {
            correspondingMedicine.increaseQuantity(newMedicine.getQuantity());
        } else {
            inventory.add(newMedicine);
            Database.insertMed(newMedicine);
        }
    }

    /**
     * Removes medicine from inventory by looking at the name.
     */
    public void removeMedicine(Medicine medicine) {
        inventory.removeIf(med -> med.getName().equals(medicine.getName()));
        Database.deleteMed(medicine);
    }

    public boolean decreaseQuantity(Medicine medicine, int amount) {
        if (inInventory(medicine) != null && medicine.getQuantity()>=amount) {
            medicine.decreaseQuantity(amount);

            addToUpdate(new Medicine(medicine.getName(), medicine.getPrice(), -amount));
            updateQtyCount();
            return true;
        } else {
            return false;
        }
    }

    public boolean increaseQuantity(Medicine medicine, int amount) {
        if (inInventory(medicine) != null) {
            medicine.increaseQuantity(amount);

            addToUpdate(new Medicine(medicine.getName(), medicine.getPrice(), amount));
            updateQtyCount();
            return true;
        } else {
            return false;
        }
    }

    public void editMedicine(Medicine oldMed, Medicine editedMed) {
        removeMedicine(oldMed);
        addMedicine(editedMed);
        Database.editMed(oldMed, editedMed);
    }

    /**
     * Loads the saved medicines into an observable
     * arrayList.
     */
    public void loadMedicine() {
        ArrayList<Medicine> invFromDB = Database.load();
        inventory.addAll(invFromDB);

    }

    /**
     * Passes the toBeUpdated list to the DB and also empties it.
     */
    public void updateInventory() {
        Database.updateQuantities(toBeUpdated);
        toBeUpdated.clear();
    }

    /**
     * Adds the passed medicine to the toBeUpdated ArrayList.
     * The quantity of the passed medicine is how much the
     * medicine quantity needs to change. If the number is
     * negative - then the quantity reduces.
     */
    private void addToUpdate(Medicine med) {
        int upMedIndex = -1;
        int oldQty=0;
        for (int i=0; i<toBeUpdated.size(); i++) {
            Medicine upMed = toBeUpdated.get(i);
            if (upMed.getName().equals(med.getName())) {
                upMedIndex = i;
                oldQty = upMed.getQuantity();
            }
        }
        if (upMedIndex>=0) {
            toBeUpdated.add(upMedIndex, new Medicine(med.getName(), med.getPrice(), med.getQuantity()+oldQty));
        } else {
            toBeUpdated.add(med);
        }
    }

    /**
     * Checks if the medicine is in the inventory and returns the
     * version of the medicine stored in the inventory. This
     * allows to increment the same medicine from the inventory.
     */
    private Medicine inInventory(Medicine medicine) {
        Medicine correspondingMedicine = null;
        if (inventory!=null){
            for (Medicine med: inventory) {
                if (med.getName().equals(medicine.getName())) {
                    correspondingMedicine=med;
                }
            }
        }
        return correspondingMedicine;
    }

    /**
     * Increments the qtyChangeCount by one. If it
     * reaches 6, it calls the updateInventory() and
     * resets the counter.
     */
    private void updateQtyCount() {
        qtyChangeCount++;

        if (qtyChangeCount>5) {
            updateInventory();
            qtyChangeCount=0;
        }
    }
}
