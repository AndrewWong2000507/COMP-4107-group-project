package ATMSS.AdvicePrinterHandler.Emulator;

import AppKickstarter.AppKickstarter;
import AppKickstarter.misc.MBox;

import java.awt.*;
import java.util.logging.Logger;


public class AdvicePrinterEmulatorController {
    private String id;
    private AppKickstarter appKickstarter;
    private Logger log;
    private AdvicePrinterEmulator advicePrinterEmulator;
    private MBox advicePrinterMbox;
    public TextField adviceShowingField;

    public void initialize(String id, AppKickstarter appKickstarter, Logger log, AdvicePrinterEmulator advicePrinterEmulator) {
        this.id = id;
        this.appKickstarter = appKickstarter;
        this.log = log;
        this.advicePrinterEmulator = advicePrinterEmulator;
        this.advicePrinterMbox = appKickstarter.getThread("AdvicePrinterHandler").getMBox();
    }

    public void handleClick(){

    }
}
