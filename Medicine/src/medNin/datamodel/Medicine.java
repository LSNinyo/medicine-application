package medNin.datamodel;

/**
 * @author Lyudmil Ninyo
 * @version 02-04-2020
 * A simple class that defines the Medicine data object. It has
 * 3 field variables: name (String), price (double) and quantity
 * (int). It also has constructor, getters and setters for each
 * of the fields. It additionally has an increaseQuantity(int amount)
 * and decreaseQuantity(int amount) methods.
 *
 * Note that there are no restrictions on the price and quantity,
 * meaning that they can be negative numbers.
 */
public class Medicine {

    private String name;
    private double price;
    private int quantity;

    public Medicine(String name, double price, int quantity) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public String getPriceString() {
        return price + "";
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getQuantityString() {
        return quantity + "";
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void increaseQuantity(int amount) {
        this.quantity += amount;
    }

    public void decreaseQuantity(int amount) {
        this.quantity -= amount;
    }

    @Override
    public String toString() {
        return name + " - $" + price + ", "+quantity+" pcs";
    }
}
