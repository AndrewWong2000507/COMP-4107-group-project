package ATMSS.AdvicePrinterHandler.Emulator;

import AppKickstarter.AppKickstarter;
import AppKickstarter.misc.MBox;

import java.util.logging.Logger;

import javafx.scene.control.Button;
import javafx.scene.control.TextArea;


public class AdvicePrinterEmulatorController {
    public TextArea advicePrintArea;
    public Button confirmReceiveAdviceButton;
    private String id;
    private AppKickstarter appKickstarter;
    private Logger log;
    private AdvicePrinterEmulator advicePrinterEmulator;
    private MBox advicePrinterMbox;

    public void initialize(String id, AppKickstarter appKickstarter, Logger log, AdvicePrinterEmulator advicePrinterEmulator) {
        this.id = id;
        this.appKickstarter = appKickstarter;
        this.log = log;
        this.advicePrinterEmulator = advicePrinterEmulator;
        this.advicePrinterMbox = appKickstarter.getThread("AdvicePrinterHandler").getMBox();
    }

    public void handleClick() {
        advicePrintArea.setText("");
    }
}
