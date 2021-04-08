package ATMSS.CashDispenserHandler.Emulator;

import AppKickstarter.AppKickstarter;
import AppKickstarter.misc.MBox;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.util.logging.Logger;

public class CashDispenserEmulatorController {
    private String id;
    private AppKickstarter appKickstarter;
    private Logger log;
    private CashDispenserEmulator cashDispenserEmulator;
    private MBox cashDispenserMBox;
    public TextField cashOut$100;
    public TextField cashOut$500;
    public TextField cashOut$1000;
    public TextField cashOutTotal;
    public Button collectCash;

    public void initialize(String id, AppKickstarter appKickstarter, Logger log, CashDispenserEmulator cashDispenserEmulator) {
        this.id = id;
        this.appKickstarter = appKickstarter;
        this.log = log;
        this.cashDispenserEmulator = cashDispenserEmulator;
        this.cashDispenserMBox = appKickstarter.getThread("CashDispenserHandler").getMBox();
    }

    public void collectCash(ActionEvent actionEvent) {
        cashOut$100.setText("");
        cashOut$500.setText("");
        cashOut$1000.setText("");
        cashOutTotal.setText("");
    }
}
