package MedApp;

import MedApp.datamodel.DailyLog;
import MedApp.datamodel.InventoryData;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {



    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("mainWindow.fxml"));
        primaryStage.setTitle("Medicine App");
        primaryStage.setScene(new Scene(root, 1400, 700));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void init() {
        InventoryData.getInstance().loadMedicine();
    }

    @Override
    public void stop() {
        InventoryData.getInstance().updateInventory();
    }
}
