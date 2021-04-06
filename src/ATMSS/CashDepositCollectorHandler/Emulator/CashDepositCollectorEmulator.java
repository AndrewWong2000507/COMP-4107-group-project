package ATMSS.CashDepositCollectorHandler.Emulator;

import ATMSS.ATMSSStarter;
import ATMSS.CashDepositCollectorHandler.CashDepositCollectorHandler;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

public class CashDepositCollectorEmulator extends CashDepositCollectorHandler {
    private String id;
    private CashDepositCollectorEmulatorController cashDepositCollectorEmulatorController;
    private Stage myStage;
    private ATMSSStarter atmssStarter;

    public CashDepositCollectorEmulator(String id, ATMSSStarter atmssStarter) {
        super(id, atmssStarter);
        this.id = id;
        this.atmssStarter = atmssStarter;
    }

    public void start() throws Exception {
        myStage = new Stage();
        String fxmlName = "CashDepositCollectorEmulator.fxml";
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(CashDepositCollectorEmulator.class.getResource(fxmlName));
        Parent root = loader.load();
        cashDepositCollectorEmulatorController = (CashDepositCollectorEmulatorController) loader.getController();
        cashDepositCollectorEmulatorController.initialize(id, this, atmssStarter, log);
        myStage.setScene(new Scene(root, 456, 360));
        myStage.initStyle(StageStyle.DECORATED);
        myStage.setResizable(false);
        myStage.setTitle("Cash Deposit Collector");
        myStage.setOnCloseRequest((WindowEvent event) -> {
            atmssStarter.stopApp();
            Platform.exit();
        });
        myStage.show();
    }

}
