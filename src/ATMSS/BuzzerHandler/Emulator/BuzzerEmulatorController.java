package ATMSS.BuzzerHandler.Emulator;

import AppKickstarter.AppKickstarter;
import AppKickstarter.misc.MBox;
import javafx.scene.control.TextField;

import java.util.logging.Logger;

public class BuzzerEmulatorController {
    public TextField buzzerStatus;
    private String id;
    private AppKickstarter appKickstarter;
    private Logger log;
    private BuzzerEmulator buzzerEmulator;
    private MBox buzzerMBox;

    public void initialize(String id, AppKickstarter appKickstarter, Logger log, BuzzerEmulator buzzerEmulator) {
        this.id = id;
        this.appKickstarter = appKickstarter;
        this.log = log;
        this.buzzerEmulator = buzzerEmulator;
        this.buzzerMBox = appKickstarter.getThread("BuzzerHandler").getMBox();
    }

    //updateBuzzerStatus
    public void updateBuzzerStatus(String status) {
        buzzerStatus.setText(status);
    } // updateBuzzerStatus
}
