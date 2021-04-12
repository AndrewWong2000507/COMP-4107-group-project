package ATMSS.AdvicePrinterHandler;

import ATMSS.HWHandler.HWHandler;
import AppKickstarter.AppKickstarter;
import AppKickstarter.misc.Msg;
/**
 * This is the class for handling the Advice Printer
 * @author Group 12
 * */
public class AdvicePrinterHandler extends HWHandler {
    /**
     * Constructor for an advice printer handler
     * @param id name of the handler thread
     * @param appKickstarter a reference to AppKickstarter
     */
    public AdvicePrinterHandler(String id, AppKickstarter appKickstarter) {
        super(id, appKickstarter);
    }

    @Override
    /**
     * This method is for receiving message sent to the mailbox
     * and decide corresponding action of advice printer according to the message type
     * @param msg The whole message object from the mailbox
     */
    protected void processMsg(Msg msg) {
        switch (msg.getType()) {
            case AP_print:
                handlePrint(msg);
                break;
            default:
                log.warning(id + ": unknown message type: [" + msg + "]");
        }
    }

    /**
     * This method is for handling print message
     * @param msg The whole message object from the mailbox
     */
    protected void handlePrint(Msg msg) {
        log.info(id + ": confirm Log out , printing advice slip");
    }
}
