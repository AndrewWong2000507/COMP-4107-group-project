package ATMSS.CashDepositCollectorHandler.Emulator;

import AppKickstarter.AppKickstarter;
import AppKickstarter.misc.MBox;

import java.util.logging.Logger;

public class CashDepositCollectorEmulatorController {
    private String id;
    private CashDepositCollectorEmulator cashDepositCollectorEmulator;
    private AppKickstarter appKickstarter;
    private Logger log;
    private MBox cashDepositCollectorMBox;

    public void initialize(String id, CashDepositCollectorEmulator cashDepositCollectorEmulator, AppKickstarter appKickstarter, Logger log) {
        this.appKickstarter = appKickstarter;
        this.cashDepositCollectorEmulator = cashDepositCollectorEmulator;
        this.id = id;
        this.log = log;
        this.cashDepositCollectorMBox = appKickstarter.getThread("CashDepositCollectorHandler").getMBox();
    }


}
