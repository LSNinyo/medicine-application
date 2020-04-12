package medNin.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import medNin.datamodel.DailyLog;
import medNin.datamodel.InventoryData;


public class RunApp extends Application {



    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader fxmlloader = new FXMLLoader();
        fxmlloader.setLocation(getClass().getResource(
                "/mainWindow.fxml"));
        Parent root = fxmlloader.load();
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
        DailyLog.getInstance().writeToFile();
    }
}
