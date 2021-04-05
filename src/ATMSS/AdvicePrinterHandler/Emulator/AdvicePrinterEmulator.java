package ATMSS.AdvicePrinterHandler.Emulator;

import ATMSS.ATMSSStarter;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ATMSS.AdvicePrinterHandler.AdvicePrinterHandler;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;


public class AdvicePrinterEmulator extends AdvicePrinterHandler {
    private Stage myStage;
    private ATMSSStarter atmssStarter;
    private String id;
    private AdvicePrinterEmulatorController advicePrinterEmulatorController;

    public AdvicePrinterEmulator(String id, ATMSSStarter atmssStarter) {
        super(id, atmssStarter);
        this.id = id;
        this.atmssStarter = atmssStarter;
    }

    public void start() throws Exception {
        Parent root;
        myStage = new Stage();
        FXMLLoader loader = new FXMLLoader();
        String fxmlName = "AdvicePrinterEmulator.fxml";
        loader.setLocation(AdvicePrinterEmulator.class.getResource(fxmlName));
        root = loader.load();
        advicePrinterEmulatorController = (AdvicePrinterEmulatorController) loader.getController();
        advicePrinterEmulatorController.initialize(id, atmssStarter, log, this);
        myStage.initStyle(StageStyle.DECORATED);
        myStage.setScene(new Scene(root, 305, 301));
        myStage.setTitle("Advice Printer");
        myStage.setResizable(false);
        myStage.setOnCloseRequest((WindowEvent event) -> {
            atmssStarter.stopApp();
            Platform.exit();
        });
        myStage.show();
    }

}
