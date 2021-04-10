package ATMSS.CashDepositCollectorHandler;

import ATMSS.HWHandler.HWHandler;
import AppKickstarter.AppKickstarter;
import AppKickstarter.misc.Msg;

public class CashDepositCollectorHandler extends HWHandler {

    public CashDepositCollectorHandler(String id, AppKickstarter appKickstarter) {
        super(id, appKickstarter);
    }

    @Override
    protected void processMsg(Msg msg) {
        switch (msg.getType()) {
            case CDC_CashDepositorOpen:
                handleCashDepositor();
                break;
            case CDC_CashDeposited:
                atmss.send(new Msg(id, mbox, Msg.Type.CDC_CashDeposited, msg.getDetails()));
                break;
            default:
                log.warning(id + ": unknown message type: [" + msg + "]");
        }
    }

    protected void handleCashDepositor() {
        log.info(id + ": Opening Cash Deposit Collector");
    }
}
