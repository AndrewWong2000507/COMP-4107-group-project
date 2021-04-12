package ATMSS.CardReaderHandler;

import ATMSS.HWHandler.HWHandler;
import AppKickstarter.AppKickstarter;
import AppKickstarter.misc.*;


/**
 * This is the class for handling the Card Reader
 *
 * @author Group 12
 */
public class CardReaderHandler extends HWHandler {
    /**
     * Constructor for an Card Reader handler
     *
     * @param id name of the handler thread
     * @param appKickstarter a reference to AppKickstarter
     */
    public CardReaderHandler(String id, AppKickstarter appKickstarter) {
        super(id, appKickstarter);
    }

    /**
     * This method is for receiving message sent to the mailbox
     * and decide corresponding action of Card Reader according to the message type
     * @param msg The whole message object from the mailbox
     */
    protected void processMsg(Msg msg) {
        switch (msg.getType()) {
            case CR_CardInserted:
                atmss.send(new Msg(id, mbox, Msg.Type.CR_CardInserted, msg.getDetails()));
                //handleCardInsert();
                break;

            case CR_EjectCard:
                atmss.send(new Msg(id, mbox, Msg.Type.CR_EjectCard, msg.getDetails()));
                handleCardEject();
                break;

            case CR_CardRemoved:
                atmss.send(new Msg(id, mbox, Msg.Type.CR_CardRemoved, msg.getDetails()));
                //handleCardRemove();
                break;

            default:
                log.warning(id + ": unknown message type: [" + msg + "]");
        }
    } // processMsg


    /**
     * This is method to log Card Insert
     */
    protected void handleCardInsert() {
        log.info(id + ": card inserted");
    } // handleCardInsert

    /**
     * This is method to log Card Eject
     */
    protected void handleCardEject() {
        log.info(id + ": card ejected");
    } // handleCardEject


    /**
     * This is method to log Card Remove
     */
    protected void handleCardRemove() {
        log.info(id + ": card removed");
    } // handleCardRemove
} // CardReaderHandler
