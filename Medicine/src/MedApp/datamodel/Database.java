package MedApp.datamodel;

import java.sql.*;
import java.util.ArrayList;

/**
 * @author LSNinyo
 * @version 10/04/2020
 *
 * A class that connects to the database where the medicines and the
 * inventory are stored. It has 1 method, load(), that returns an ArrayList of
 * type Medicine with all the data from the DB.
 *
 * It also has 4 methods that alter the content of the database.
 * Insert(Medicine) just inserts a record of the specified medicine.
 * Remove(Medicine) removes all the records of the medicine from the DB.
 * Edit(Medicine) updates the record of medicine as specified.
 * And updateQuantities(ArrayList type Medicine) iterates over the
 * passed list and increments the quantities in the DB by as much as provided
 * by the quantity field variable of each specific medicine.
 * For example if the DB contains records that Ibuprofen's quantity is 3 and
 * an instance of medicine with the fields: Ibuprofen, 0.75 and 4 is passed - the
 * method will increment the quantity by 4 resulting in 7.
 */
public class Database {

    public static final String CONNECTION_STRING =
            "jdbc:sqlite:D:med.db";
    public static final String MED_TABLE = "Medicines";
    public static final String INV_TABLE = "Inventory";

    public static final String QUERY_BOTH_TABLES =
            "SELECT "+MED_TABLE+".Name AS name, "+MED_TABLE+".Price AS price, "+
                    INV_TABLE +".Quantity AS quantity FROM "+MED_TABLE+ " INNER JOIN "+ INV_TABLE +
                    " ON Name = MedName;";

    public static final String INCREMENT_QUANTITY_PREP =
            "UPDATE "+ INV_TABLE +" SET Quantity = Quantity + ? WHERE MedName = ?";
    public static final String INSERT_INTO_MED_PREP =
            "INSERT INTO " + MED_TABLE + " VALUES(?,?);";
    public static final String INSERT_INTO_INV_PREP =
            "INSERT INTO " + INV_TABLE + " VALUES(?,?);";
    public static final String DELETE_MED_PREP =
            "DELETE FROM " + MED_TABLE + " WHERE Name=?;";
    public static final String DELETE_INV_PREP =
            "DELETE FROM " + INV_TABLE + " WHERE MedName=?;";
    public static final String UPDATE_MED_PREP =
            "UPDATE " + MED_TABLE + " SET Name=?, Price=? WHERE Name=?;";
    public static final String UPDATE_INV_PREP =
            "UPDATE " + INV_TABLE + " SET MedName=?, Quantity=? WHERE MedName=?;";


    /**
     * Connect to db and load all the medicine and the
     * corresponding quantities from there onto an
     * ArrayList of type Medicine and pass it to
     * the calling method.
     */
    public static ArrayList<Medicine> load() {
        ArrayList<Medicine> inventory = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(CONNECTION_STRING);
             Statement statement = conn.createStatement()) {

            statement.execute(QUERY_BOTH_TABLES);
            ResultSet results = statement.getResultSet();

            while(results.next()) {
//                System.out.println(results.getString("name") + " "+
//                        results.getDouble("price") + " " +
//                        results.getInt("quantity"));

                inventory.add(new Medicine(results.getString("name"), results.getDouble("price"),
                        results.getInt("quantity")));
            }
            // good practice is for any resource not used anymore to be closed:
            results.close();

        } catch(SQLException e) {
            System.out.println("Something went wrong: " + e.getMessage());
        }
        return inventory;
    }

    /**
     * Iterates over the passed ArrayList and performs an
     * SQL update query to update the quantities in the DB.
     */
    public static void updateQuantities(ArrayList<Medicine> toBeUpdated) {
        PreparedStatement incrementQuantity;
        try (Connection conn = DriverManager.getConnection(CONNECTION_STRING)) {
            incrementQuantity = conn.prepareStatement(INCREMENT_QUANTITY_PREP);
            for (Medicine med: toBeUpdated) {
                incrementQuantity.setInt(1, med.getQuantity());
                incrementQuantity.setString(2, med.getName());
                incrementQuantity.executeUpdate();
            }
            incrementQuantity.close();

        } catch (SQLException sqle) {
            System.out.println("Error updating quantities: "+sqle.getMessage());
        }
    }

    public static void insertMed(Medicine med) {
        try (Connection conn = DriverManager.getConnection(CONNECTION_STRING);
             PreparedStatement insertMedPrepStm = conn.prepareStatement(INSERT_INTO_MED_PREP);
             PreparedStatement insertInvPrepStm = conn.prepareStatement(INSERT_INTO_INV_PREP)) {
            insertMedPrepStm.setString(1, med.getName());
            insertMedPrepStm.setDouble(2, med.getPrice());
            insertMedPrepStm.executeUpdate();

            insertInvPrepStm.setString(1, med.getName());
            insertInvPrepStm.setInt(2, med.getQuantity());
            insertInvPrepStm.executeUpdate();
        } catch (SQLException sqle) {
            System.out.println("Error inserting medicine in DB: "+sqle.getMessage());
        }
    }

    public static void deleteMed(Medicine med) {
        try (Connection conn = DriverManager.getConnection(CONNECTION_STRING);
             PreparedStatement deleteMedPrepStm = conn.prepareStatement(DELETE_MED_PREP);
             PreparedStatement deleteInvPrepStm = conn.prepareStatement(DELETE_INV_PREP)) {
            deleteMedPrepStm.setString(1, med.getName());
            deleteMedPrepStm.executeUpdate();

            deleteInvPrepStm.setString(1, med.getName());
            deleteInvPrepStm.executeUpdate();
        } catch (SQLException sqle) {
            System.out.println("Error deleting medicine from DB: "+sqle.getMessage());
        }
    }

    /**
     * This method updates the records for the medicine in the
     * DB. Since the med name is a foreign key in the Inventory
     * table - we need to update the name first in the Medicines
     * table and then update it in the Inventory table.
     */
    public static void editMed(Medicine oldMed, Medicine newMed) {
        try (Connection conn = DriverManager.getConnection(CONNECTION_STRING);
             PreparedStatement updateMedPrepStm = conn.prepareStatement(UPDATE_MED_PREP);
             PreparedStatement updateInvPrepStm = conn.prepareStatement(UPDATE_INV_PREP)) {
            updateMedPrepStm.setString(1, newMed.getName());
            updateMedPrepStm.setDouble(2, newMed.getPrice());
            updateMedPrepStm.setString(3, oldMed.getName());
            updateMedPrepStm.executeUpdate();

            updateInvPrepStm.setString(1, newMed.getName());
            updateInvPrepStm.setInt(2, newMed.getQuantity());
            updateInvPrepStm.setString(3, oldMed.getName());
            updateInvPrepStm.executeUpdate();
        } catch (SQLException sqle) {
            System.out.println("Error editing medicine in DB: "+sqle.getMessage());
        }
    }
}
