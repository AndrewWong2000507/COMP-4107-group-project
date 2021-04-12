package ATMSS.BuzzerHandler.Emulator;

import ATMSS.ATMSSStarter;
import ATMSS.BuzzerHandler.BuzzerHandler;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.media.Media;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

public class BuzzerEmulator extends BuzzerHandler {
    private String id;
    private ATMSSStarter atmssStarter;
    private Stage myStage;
    private BuzzerEmulatorController buzzerEmulatorController;

    public BuzzerEmulator(String id, ATMSSStarter atmssStarter) {
        super(id, atmssStarter);
        this.id = id;
        this.atmssStarter = atmssStarter;
    }

    public void start() throws Exception {
        Parent root;
        String fxmlName = "BuzzerEmulator.fxml";
        FXMLLoader loader = new FXMLLoader();
        myStage = new Stage();
        loader.setLocation(BuzzerEmulator.class.getResource(fxmlName));
        root = loader.load();
        buzzerEmulatorController = (BuzzerEmulatorController) loader.getController();
        buzzerEmulatorController.initialize(id, atmssStarter, log, this);
        myStage.setScene(new Scene(root, 264, 214));
        myStage.setResizable(false);
        myStage.setTitle("Buzzer");
        myStage.initStyle(StageStyle.DECORATED);
        myStage.setOnCloseRequest((WindowEvent event) -> {
            atmssStarter.stopApp();
            Platform.exit();
        });
        myStage.show();
    }

    @Override
    protected void buzzerOn() {
        super.buzzerOn();
        buzzerEmulatorController.updateBuzzerStatus("ON");
    }

    @Override
    protected void buzzerOff() {
        super.buzzerOff();
        buzzerEmulatorController.updateBuzzerStatus("OFF");
    }
}
