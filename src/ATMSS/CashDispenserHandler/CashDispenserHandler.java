package ATMSS.CashDispenserHandler;

import ATMSS.HWHandler.HWHandler;
import AppKickstarter.AppKickstarter;
import AppKickstarter.misc.Msg;

public class CashDispenserHandler extends HWHandler {
    private double inventory = 10000000;

    public CashDispenserHandler(String id, AppKickstarter appKickstarter) {
        super(id, appKickstarter);
        this.id = id;
        this.appKickstarter = appKickstarter;
    }

    @Override
    protected void processMsg(Msg msg) {
        switch (msg.getType()) {
            case CD_CashDispense:
                dispenseCash(msg);
                break;
            default:
                log.warning(id + ": unknown message type: [" + msg + "]");
        }
    }

    protected void checkInventory(Msg msg) {

    }

    protected void dispenseCash(Msg msg) {
        log.info(id + ": dispense Cash -- " + msg.getDetails());
    }

}
