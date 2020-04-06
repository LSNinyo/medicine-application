package MedApp.datamodel;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;

public class InventoryData {

    private static InventoryData instance = new InventoryData();
    private static String dataFile = "Inventory.txt";
    private ObservableList<Medicine> inventory = FXCollections.observableArrayList();

    public static InventoryData getInstance() {
        return instance;
    }

    public ObservableList<Medicine> getInventory() {
        return inventory;
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
        }
    }

    /**
     * Removes medicine from inventory by looking at the name.
     */
    public void removeMedicine(Medicine medicine) {
        inventory.remove(medicine);
    }

    public boolean decreaseQuantity(Medicine medicine, int amount) {
        if (inInventory(medicine) != null && medicine.getQuantity()>=amount) {
            medicine.decreaseQuantity(amount);
            return true;
        } else {
            return false;
        }
    }

    public boolean increaseQuantity(Medicine medicine, int amount) {
        if (inInventory(medicine) != null) {
            medicine.increaseQuantity(amount);
            return true;
        } else {
            return false;
        }
    }

    public void editMedicine(Medicine oldMed, Medicine editedMed) {
        removeMedicine(oldMed);
        addMedicine(editedMed);
    }

    public void loadMedicine() throws IOException {
        inventory = FXCollections.observableArrayList();
        Path path = Paths.get(dataFile);
        BufferedReader br = Files.newBufferedReader(path);

        String text;

        try {
            while ( (text=br.readLine()) != null) {
                String[] medicinePieces = text.split(";\t");
                String name = medicinePieces[0];
                double price = Double.parseDouble(medicinePieces[1]);
                int quantity = Integer.parseInt(medicinePieces[2]);

                Medicine medicine = new Medicine(name, price, quantity);
                inventory.add(medicine);
            }
        } finally {
            if (br!=null) {
                br.close();
            }
        }

    }

    public void storeMedicine() throws IOException {
        Path path = Paths.get(dataFile);
        BufferedWriter bw = Files.newBufferedWriter(path);

        try {
            Iterator<Medicine> iterator = inventory.iterator();
            while (iterator.hasNext()) {
                Medicine medicine = iterator.next();
                String price = medicine.getPrice() +"";
                String quantity = medicine.getQuantity() + "";
                bw.write(String.format("%s;\t%s;\t%s",
                        medicine.getName(), price, quantity));
                bw.newLine();
            }
        } finally {
            if (bw!=null) {
                bw.close();
            }
        }
    }
}
