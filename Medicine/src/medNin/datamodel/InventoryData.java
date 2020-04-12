package medNin.datamodel;

import medNin.settings.Settings;

import java.util.ArrayList;
import java.util.Scanner;


/**
 * @author Lyudmil Ninyo
 * @version 12-04-20
 *
 * A singleton class that stores the medicine inventory.
 */
public class InventoryData {

    private static InventoryData instance = new InventoryData();
    private ArrayList<Medicine> inventory = new ArrayList<>();
    // The quantities of the medicines stored in toBeUpdated are the
    // amounts which should be incremented!
    private ArrayList<Medicine> toBeUpdated = new ArrayList<>();

    private int qtyChangeCount = 0;

    public static InventoryData getInstance() {
        return instance;
    }

    public ArrayList<Medicine> getInventory() {
        return inventory;
    }

    /**
     * Constructor. This method checks if the Settings are all
     * set up. If not it asks the user, through the terminal to
     * input the path where the app's data folder is going to
     * reside. It then calls for the settings to set up and
     * for the DB to set up.
     *
     * The choice for a terminal input was there since there is
     * not usual installer and this way it is much easier. It will
     * require for a more savvy person to set up the app for a
     * non-savvy user, but it won't be too hard.
     */
    private InventoryData() {
        if (!Settings.checkIfSetUp()) {
            Scanner sc = new Scanner(System.in);
            System.out.println("Please provide the path for the app's data folder. "+
                    "It should look something like C:\\Program Files. ");
            String path = sc.nextLine();

            Settings.initialSetUp(path);
            Database.initialSetUp();
        }
    }


    /**
     * If the medicine provided is completely new - it adds it
     * to the inventory. If it's already existing - it just
     * increases its quantity.
     */
    public void addMedicine(Medicine newMedicine) {
        if (!increaseQuantity(newMedicine, newMedicine.getQuantity())) {
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

    /**
     * Decreases the quantity of this medicine's version in the
     * inventory by the amount provided. It also passes it to the
     * toBeUpdated list.
     */
    public boolean decreaseQuantity(Medicine medicine, int amount) {
        Medicine invVersion = inListOfMedicines(medicine, inventory);
        if (invVersion != null && medicine.getQuantity()>=amount) {
            invVersion.decreaseQuantity(amount);

            addToUpdate(new Medicine(medicine.getName(), medicine.getPrice(), -amount));
            updateQtyCount();
            return true;
        } else {
            return false;
        }
    }

    /**
     * Increases the quantity of this medicine's version in the
     * inventory by the amount provided. It also passes it to the
     * toBeUpdated list.
     */
    public boolean increaseQuantity(Medicine medicine, int amount) {
        Medicine invVersion = inListOfMedicines(medicine, inventory);
        if (invVersion != null) {
            invVersion.increaseQuantity(amount);

            addToUpdate(new Medicine(medicine.getName(), medicine.getPrice(), amount));
            updateQtyCount();
            return true;
        } else {
            return false;
        }
    }

    /**
     * Removes the old medicine from the inventory and adds
     * the new one.
     */
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
        Medicine listVersion = inListOfMedicines(med, toBeUpdated);
        if (listVersion!=null) {
            listVersion.increaseQuantity(med.getQuantity());
        } else {
            toBeUpdated.add(med);
        }
    }

    /**
     * Checks if the medicine is in the inventory and returns the
     * version of the medicine stored in the inventory. This
     * allows to increment the same medicine from the inventory.
     */
    private Medicine inListOfMedicines(Medicine medicine, ArrayList<Medicine> list) {
        Medicine correspondingMedicine = null;
        if (list!=null){
            for (Medicine med: list) {
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
