package ATMSS.AdvicePrinterHandler.Emulator;

import ATMSS.ATMSSStarter;
import AppKickstarter.misc.Msg;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ATMSS.AdvicePrinterHandler.AdvicePrinterHandler;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
/**
 * This is the class for building Advice Printer Emulator
 * @author Group 12
 *
 * */


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

    /**
     * This method will link the stage with FXML file and controller and start showing the stage
     * @throws Exception exist on loading the FXML file
     */
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

    @Override
    /**
     * This method receive the message and set the text in message details to the advice printer
     */
    protected void handlePrint(Msg msg) {
        advicePrinterEmulatorController.advicePrintArea.setText(msg.getDetails());
    }
}
