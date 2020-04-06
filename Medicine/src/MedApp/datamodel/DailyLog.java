package MedApp.datamodel;

import java.io.*;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Properties;
import java.util.stream.Stream;

/**
 * @author Lyudmil Ninyo
 * @version 06-04-2020
 * A singleton class that keeps track of the medicine being sold
 * during the day. It outputs everything to a file stored in
 * a folder in the project directory.
 *
 * Important! If the application keeps running through midnight - it
 * will continue saving in the file from the previous day. It will
 * have to be re-initialized in order to start a new file.
 */
public class DailyLog {

    private static DailyLog instance = new DailyLog();
    private String logFilePathStr;
    private Path logFilesDirPath;
    private ArrayList<Medicine> soldList = new ArrayList<>();

    private int additionCounter;

    private static final int ADDITIONS_PER_WRITE = 10;

    private static final Path SETTINGS_PATH = FileSystems.getDefault().getPath("Settings",
            "Settings.properties");

    private final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("dd-MM-yy");
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");
    private static final String FILE_NAME_SUFFIX = "-medicine-sold.txt";
    private static final String LAST_UPDATE_PREFIX = "Last updated at ";
    private static final String REGEX = "\t";
    private static final double DEFAULT_PRICE = -1.1;

    /**
     * The constructor first obtains the Log files directory from the
     * settings. It then searches through it to find if there is a file
     * for today. If there isn't it creates one.
     */
    private DailyLog() {
        String dirPathString = getDirPath();
        LocalDate today = LocalDate.now();
        logFilePathStr = dirPathString +"\\"+today.format(DTF) + FILE_NAME_SUFFIX;


//        Path filePath = FileSystems.getDefault().getPath(dirPathString, fileName);
        try (Stream<Path> walk = Files.walk(logFilesDirPath)) {
            walk.forEach(x-> {
                if (x.toString().equals(logFilePathStr)) {
                    loadLogFile();
                }
            });
        } catch (IOException ioe) {
            System.out.println("Exception reading log folder: "+ioe.getMessage());
        }

        additionCounter = 0;
    }

    public static DailyLog getInstance() {
        return instance;
    }

    /**
     * Looks if the medicine is already in the list.
     * If it is, it just increments the quantity and
     * if it isn't - it adds it.
     */
    public void addToSoldList(Medicine medicine) {
        int index = findAndGetIndex(medicine);
        if (index>=0) {
            medicine.increaseQuantity(soldList.get(index).getQuantity());
            soldList.set(index, medicine);
        } else {
            soldList.add(medicine);
        }
        if (additionCounter == ADDITIONS_PER_WRITE) {
            writeToFile();
            additionCounter=0;
        } else {
            additionCounter++;
        }
    }

    private int findAndGetIndex(Medicine medicine) {
        if (soldList!=null) {
            for (int i=0; i<soldList.size(); i++) {
                if (soldList.get(i).getName().equals(medicine.getName())) {
                    return i;
                }
            }
        }
        return -1;
    }

    private String getDirPath() {
        File settingsFile = new File("Settings\\Settings.properties");
        String dirPathString = "";

        try (FileInputStream input = new FileInputStream(settingsFile)) {
            Properties prop = new Properties();
            prop.load(input);

            // !!! PRODUCTION - IN THE LAST ITERATION THE SETTINGS MAY BE SAVED SOMEWHERE ELSE
            logFilesDirPath = FileSystems.getDefault().getPath(prop.getProperty("logFilesDirectoryPath"));
            dirPathString = prop.getProperty("logFilesDirectoryPath");

        } catch (FileNotFoundException fnfe) {
            System.out.println("settings file not found: "+fnfe.getMessage());
        } catch (IOException ioe) {
            System.out.println(ioe.getMessage());
        }
        return dirPathString;
    }

    /**
     * Loads file using the logFilePath field variable. It populates the
     * list of sold medicine using the file entries. The file doesn't store
     * price so a default price is used.
     */
    private void loadLogFile() {
        try (BufferedReader br = new BufferedReader(new FileReader(logFilePathStr))) {
            String currentLine;
            while ((currentLine = br.readLine()) != null) {
                if (!currentLine.startsWith(LAST_UPDATE_PREFIX) && !currentLine.isEmpty()) {
                    String[] splitLine = currentLine.split(REGEX);
                    int quantity = Integer.parseInt(splitLine[2]);
                    Medicine med = new Medicine(splitLine[0], DEFAULT_PRICE, quantity);
                    addToSoldList(med);
                }
            }
        } catch (FileNotFoundException fnfe) {
            System.out.println("Log file not found: "+fnfe.getMessage());
        } catch (IOException ioe) {
            System.out.println("Error creating buffered reader for log file: "+ioe.getMessage());
        }
    }

    /**
     * Called automatically by the addToList method every
     * ADDITIONS_PER_WRITE additions. It writes to file in
     * the specified way.
     *
     * It first starts with the current date and time to outline
     * when it was last updated. It leaves a line and then
     * starts enumerating the medicine in the list and the
     * quantities sold.
     */
    public void writeToFile() {
        LocalTime time = LocalTime.now();
        LocalDate today = LocalDate.now();

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(logFilePathStr))) {
            bw.write(LAST_UPDATE_PREFIX + time.format(TIME_FORMAT) +
                    " on " + today.format(DTF) + "\n");
            bw.write("\n");

            for (Medicine medicine : soldList) {
                bw.write(medicine.getName() + REGEX + " - " + REGEX + medicine.getQuantity()+"\n");
            }
        } catch (IOException ioe) {
            System.out.println("Error writing to log file: "+ioe.getMessage());
        }
    }

    /**
     * Development mode!
     */
    public void printList() {
        System.out.println("Sold medicine:");
        soldList.forEach(System.out::println);
    }
}
