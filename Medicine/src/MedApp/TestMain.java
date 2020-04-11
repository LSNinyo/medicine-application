package MedApp;

import MedApp.datamodel.DailyLog;
import MedApp.datamodel.Medicine;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

public class TestMain {

    public static void main(String[] args) {
//        ArrayList<Integer> testArr = new ArrayList<>();
//
//        testArr.add(0); testArr.add(1); testArr.add(2); testArr.add(3);
//        System.out.println("Display first");
//        for (Integer el: testArr) {
//            System.out.println(el);
//        }
//
//        testArr.remove(2);
//        System.out.println("Display after");
//        for (int i=0; i<testArr.size(); i++) {
//            System.out.println(i+": "+testArr.get(i));
//        }

//        double randomPrice = 100002.51;
//        DecimalFormat format = new DecimalFormat("0.00");
//        System.out.println(format.format(randomPrice));

        DailyLog.getInstance().addToSoldList(new Medicine("B", 2.3, 4));
        DailyLog.getInstance().printList();
        DailyLog.getInstance().writeToFile();


//        dlTest.addToSoldList(new Medicine("A", 2.2, 3));
//        dlTest.addToSoldList(new Medicine("asdasdsad", 2.2, 4));
//        dlTest.addToSoldList(new Medicine("C", 2.2, 7));
//        dlTest.addToSoldList(new Medicine("A quite long name", 2.2, 1));
//        dlTest.writeToFile();



    }

}
