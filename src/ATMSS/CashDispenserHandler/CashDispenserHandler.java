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
        super.processMsg(msg);
    }
}
