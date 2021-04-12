package ATMSS.AdvicePrinterHandler;

import ATMSS.HWHandler.HWHandler;
import AppKickstarter.AppKickstarter;
import AppKickstarter.misc.Msg;

public class AdvicePrinterHandler extends HWHandler {
    public AdvicePrinterHandler(String id, AppKickstarter appKickstarter) {
        super(id, appKickstarter);
    }

    @Override
    protected void processMsg(Msg msg) {
        switch (msg.getType()) {
            case AP_print:
                handlePrint(msg);
                break;
            default:
                log.warning(id + ": unknown message type: [" + msg + "]");
        }
    }

    protected void handlePrint(Msg msg) {
        log.info(id + ": confirm Log out , printing advice slip");
    }
}
