package medNin.settings;

import java.io.*;
import java.util.Properties;

/**
 * @author LSNinyo
 * @version 12/04/2020
 *
 * An auxiliary class that creates the database file and the
 * folders for the db and log files. It also reads the
 * settings and returns the paths of the log files
 * and database.
 */
public class Settings {

    private static final String SETTINGS_PATH_STR = "Settings\\Settings.properties";
    public static final String MAIN_APP_DIR = "MedNin data";
    public static final String DB_DIR_NAME = "Database";
    public static final String LOG_DIR_NAME = "LogFiles";

    /**
     * Checks if the settings are all filled in.
     */
    public static boolean checkIfSetUp() {
        return !getDBPath().isEmpty();
    }

    /**
     * Saves the provided path in Settings for both the
     * db and log files directories. Currently, the user
     * isn't able to set different paths for the different
     * directories.
     */
    public static void savePath(String path) {
        try (OutputStream output = new FileOutputStream(SETTINGS_PATH_STR)) {
            Properties prop = new Properties();

            prop.setProperty("dbDirPath",path+"\\"+DB_DIR_NAME);
            prop.setProperty("logFilesDirPath",path+"\\"+LOG_DIR_NAME);

            prop.store(output, null);
        } catch (IOException ioe) {
            System.out.println("Error writing to settings file: "+ioe.getMessage());
        }
    }

    /**
     * Returns the path to the directory where the db is stored.
     */
    public static String getDBPath() {
        File settingsFile = new File(SETTINGS_PATH_STR);
        String dbPathString = "";

        try (FileInputStream input = new FileInputStream(settingsFile)) {
            Properties prop = new Properties();
            prop.load(input);

            dbPathString = prop.getProperty("dbDirPath");
        } catch (FileNotFoundException fnfe) {
            System.out.println("settings file not found: "+fnfe.getMessage());
        } catch (IOException ioe) {
            System.out.println(ioe.getMessage());
        }
        return dbPathString;
    }

    /**
     * Returns the path of the folder where the log files are saved.
     */
    public static String getLogPath() {
        File settingsFile = new File(SETTINGS_PATH_STR);
        String dirPathString = "";

        try (FileInputStream input = new FileInputStream(settingsFile)) {
            Properties prop = new Properties();
            prop.load(input);

            dirPathString = prop.getProperty("logFilesDirPath");
        } catch (FileNotFoundException fnfe) {
            System.out.println("settings file not found: "+fnfe.getMessage());
        } catch (IOException ioe) {
            System.out.println(ioe.getMessage());
        }
        return dirPathString;
    }

    /**
     * Creates the two DB and log folders with the names specified
     * above. It also writes to Settings the paths of the folders.
     * @param path The path of the folder in which the DB and
     *             Log folders are to exist.
     */
    public static void initialSetUp(String path) {
        new File(path+"\\"+MAIN_APP_DIR).mkdir();
        new File(path+"\\"+MAIN_APP_DIR+"\\"+DB_DIR_NAME).mkdir();
        new File(path+"\\"+MAIN_APP_DIR+"\\"+LOG_DIR_NAME).mkdir();

        savePath(path+"\\"+MAIN_APP_DIR);
    }
}
