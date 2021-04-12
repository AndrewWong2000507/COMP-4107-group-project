package ATMSS.AdvicePrinterHandler.Emulator;

import AppKickstarter.AppKickstarter;
import AppKickstarter.misc.MBox;

import java.util.logging.Logger;

import javafx.scene.control.Button;
import javafx.scene.control.TextArea;

/**
 * This is the class for building Advice Printer Emulator Controller to interact the GUI with the handler
 * */
public class AdvicePrinterEmulatorController {
    public TextArea advicePrintArea;
    public Button confirmReceiveAdviceButton;
    private String id;
    private AppKickstarter appKickstarter;
    private Logger log;
    private AdvicePrinterEmulator advicePrinterEmulator;
    private MBox advicePrinterMbox;

    /**
     * This method initialize the controller
     * @param id name of the appThread
     * @param appKickstarter a reference to our AppKickstarter
     * @param log the logger will be used to log
     * @param advicePrinterEmulator defining the emulator this controller is linked with
     */
    public void initialize(String id, AppKickstarter appKickstarter, Logger log, AdvicePrinterEmulator advicePrinterEmulator) {
        this.id = id;
        this.appKickstarter = appKickstarter;
        this.log = log;
        this.advicePrinterEmulator = advicePrinterEmulator;
        this.advicePrinterMbox = appKickstarter.getThread("AdvicePrinterHandler").getMBox();
    }

    /**
     * This method will clear the advice printed on the advice printer
     * Simulating the ATM user taking away the advice slip
     */
    public void handleClick() {
        advicePrintArea.setText("");
    }
}
