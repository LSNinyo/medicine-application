package medNin.main;

import medNin.datamodel.DailyLog;
import medNin.datamodel.InventoryData;
import medNin.datamodel.Medicine;

import java.util.ArrayList;

public class Application {

    private ArrayList<Medicine> basketMedicine = new ArrayList<>();
    private ArrayList<Integer> basketPieces = new ArrayList<>();

    public Application () {

    }

    public ArrayList<Medicine> getBasketMedicine() {
        return basketMedicine;
    }

    public ArrayList<Integer> getBasketPieces() {
        return basketPieces;
    }


    /**
     * Updates the basket list by adding the passed medicine to it. If the medicine
     * is already in the list - it increments the quantity. If the quantity is larger
     * than the available and is not in inventory mode - it doesn't do anything and returns
     * false. If everything went according to plan - it returns true;
     */
    public boolean addMedicineToBasketList(Medicine medicine, int amount, boolean inInventoryMode) {
        if (amount>0) {
            if (basketMedicine.contains(medicine)) {
                int index = basketMedicine.indexOf(medicine);

                if (basketPieces.get(index)+amount > medicine.getQuantity() && !inInventoryMode) {
                    return false;
                } else {
                    int newQuantity = basketPieces.get(index) + amount;
                    basketPieces.add(index, newQuantity);
                }
            } else {
                if (amount > medicine.getQuantity() && !inInventoryMode) {
                    return false;
                } else {
                    basketMedicine.add(medicine);
                    basketPieces.add(amount);
                }
            }
        }
        return true;
    }

    /**
     * Removes the medicine with the provided index from the basket.
     */
    public void removeMedicineFromBasket(int medicineIndex) {
        basketMedicine.remove(medicineIndex);
        int pieces = basketPieces.remove(medicineIndex);
    }


    public void clearBasket() {
        basketMedicine.clear();
        basketPieces.clear();
    }

    /**
     * It decrements all the medicine in the inventory with the
     * specified values. It also adds the medicine from the basket
     * to the daily log.
     */
    public void removeBasketFromInventory() {
        for (int i=0; i<basketMedicine.size(); i++) {
            InventoryData.getInstance().decreaseQuantity(basketMedicine.get(i), basketPieces.get(i));
            Medicine tempMed = new Medicine(basketMedicine.get(i).getName(), 0.0, basketPieces.get(i));
            DailyLog.getInstance().addToSoldList(tempMed);
        }
        clearBasket();
    }

    public void addBasketToInventory() {
        for (int i=0; i<basketMedicine.size(); i++) {
            InventoryData.getInstance().increaseQuantity(basketMedicine.get(i), basketPieces.get(i));
        }
        clearBasket();
    }

    public void addNewMedicine(Medicine newMedicine) {
        InventoryData.getInstance().addMedicine(newMedicine);
    }

    public void editMedicine(Medicine oldMed, Medicine newMed) {
        InventoryData.getInstance().editMedicine(oldMed, newMed);
    }

    public void removeMedicine(Medicine medicine) {
        InventoryData.getInstance().removeMedicine(medicine);
    }
}
