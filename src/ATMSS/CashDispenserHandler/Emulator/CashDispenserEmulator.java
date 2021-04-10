package ATMSS.CashDispenserHandler.Emulator;

import ATMSS.ATMSSStarter;
import ATMSS.CashDispenserHandler.CashDispenserHandler;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;


public class CashDispenserEmulator extends CashDispenserHandler {
    private String id;
    private ATMSSStarter atmssStarter;
    private CashDispenserEmulatorController cashDispenserEmulatorController;
    private Stage myStage;

    public CashDispenserEmulator(String id, ATMSSStarter atmssStarter) {
        super(id, atmssStarter);
        this.id = id;
        this.atmssStarter = atmssStarter;
    }

    public void start() throws Exception {
        Parent root;
        String fxmlName = "CashDispenserEmulator.fxml";
        FXMLLoader loader = new FXMLLoader();
        myStage = new Stage();
        loader.setLocation(CashDispenserEmulator.class.getResource(fxmlName));
        root = loader.load();
        cashDispenserEmulatorController = (CashDispenserEmulatorController) loader.getController();
        cashDispenserEmulatorController.initialize(id, atmssStarter, log, this);
        myStage.setTitle("Cash Dispenser");
        myStage.initStyle(StageStyle.DECORATED);
        myStage.setResizable(false);
        myStage.setScene(new Scene(root, 444, 238));
        myStage.setOnCloseRequest((WindowEvent event) -> {
            atmssStarter.stopApp();
            Platform.exit();
        });
        myStage.show();
    }

}
