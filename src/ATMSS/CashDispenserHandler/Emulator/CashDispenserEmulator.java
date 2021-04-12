package ATMSS.CashDispenserHandler.Emulator;

import ATMSS.ATMSSStarter;
import ATMSS.CashDispenserHandler.CashDispenserHandler;
import AppKickstarter.misc.Msg;
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

    @Override
    protected void dispenseCash(Msg msg) {
        log.info(msg.getDetails());
        String[] cash = msg.getDetails().split("/");
        cashDispenserEmulatorController.cashOut$100.setText(cash[2]);
        cashDispenserEmulatorController.cashOut$500.setText(cash[1]);
        cashDispenserEmulatorController.cashOut$1000.setText(cash[0]);
        cashDispenserEmulatorController.cashOutTotal.setText(cash[3]);
        cashDispenserEmulatorController.dispenseCash(cash);
    }

    protected void getInventory() {
        atmss.send(new Msg(id, mbox, Msg.Type.CD_InventoryPressed, cashDispenserEmulatorController.getInventory()));
    }

    //------------------------------------------------------------
    // handleUpdateDispenser
    protected void handleUpdateDispenser(Msg msg) {
        log.info(id + ": update display -- " + msg.getDetails());

        switch (msg.getDetails()) {
            case "OutOfCash":
                reloadStage("CashDispenserEmulatorOutOfCash.fxml");
                break;

            case "Main":
                reloadStage("CashDispenserEmulator.fxml");
                break;

            default:
                log.severe(id + ": update display with unknown display type -- " + msg.getDetails());
                break;
        }
    } // handleUpdateDispenser

    //------------------------------------------------------------
    // reloadStage
    private void reloadStage(String fxmlFName) {
        CashDispenserEmulator touchDisplayEmulator = this;

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                try {
                    log.info(id + ": loading fxml: " + fxmlFName);

                    Parent root;
                    FXMLLoader loader = new FXMLLoader();
                    loader.setLocation(CashDispenserEmulator.class.getResource(fxmlFName));
                    root = loader.load();
                    cashDispenserEmulatorController = (CashDispenserEmulatorController) loader.getController();
                    cashDispenserEmulatorController.initialize(id, atmssStarter, log, touchDisplayEmulator);
                    myStage.setScene(new Scene(root));
                } catch (Exception e) {
                    log.severe(id + ": failed to load " + fxmlFName);
                    e.printStackTrace();
                }
            }
        });
    } // reloadStage
}
