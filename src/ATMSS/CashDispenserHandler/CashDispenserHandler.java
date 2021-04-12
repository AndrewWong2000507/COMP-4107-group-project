package ATMSS.CashDispenserHandler;

import ATMSS.HWHandler.HWHandler;
import AppKickstarter.AppKickstarter;
import AppKickstarter.misc.Msg;

public class CashDispenserHandler extends HWHandler {

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

            case CD_UpdateDispenser:
                handleUpdateDispenser(msg);
                break;

            case CD_GetInventoryForDispense:
                getInventory();
                break;
            default:
                log.warning(id + ": unknown message type: [" + msg + "]");
        }
    }

    protected void dispenseCash(Msg msg) {
        log.info(id + ": dispense Cash -- " + msg.getDetails());
    }

    protected void getInventory(){
        log.info(id + ": get invertory");
    }

    protected void handleUpdateDispenser(Msg msg) {
        log.info(id + ": update dispenser -- " + msg.getDetails());
    }

}
