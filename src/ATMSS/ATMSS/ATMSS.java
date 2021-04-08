package ATMSS.ATMSS;

import ATMSS.BAMSHandler.BAMSHandler;
import ATMSS.BAMSHandler.BAMSInvalidReplyException;
import AppKickstarter.AppKickstarter;
import AppKickstarter.misc.*;
import AppKickstarter.timer.Timer;

import java.io.IOException;


//======================================================================
// ATMSS
public class ATMSS extends AppThread {
    private int pollingTime;
    private MBox cardReaderMBox;
    private MBox keypadMBox;
    private MBox touchDisplayMBox;
    private MBox advicePrinterMBox;
    private MBox cashDispenserMBox;
    private MBox buzzerMBox;
    private MBox cashDepositCollectorMBox;

    ATMState hasCard;
    ATMState noCard;
    ATMState hasCorrectPin;

    private String pin = "";
    public String cardNo = "";
    //Create BAMSHandler

    ATMState atmState;
    boolean correctPinEntered = false;

    //------------------------------------------------------------
    // ATMSS
    public ATMSS(String id, AppKickstarter appKickstarter) throws Exception {
        super(id, appKickstarter);
        pollingTime = Integer.parseInt(appKickstarter.getProperty("ATMSS.PollingTime"));

        hasCard = new HasCard(this);
        noCard = new NoCard(this);
        hasCorrectPin = new HasPin(this);

        atmState = noCard;

    } // ATMSS

    //Change ATM state
    void setATMState(ATMState newATMState){
        atmState = newATMState;
    }

    public void insertCard() {
        atmState.insertCard();
    }

    public void ejectCard() {
        atmState.ejectCard();
    }

    public void insertPin(String CardNum, String Pin){
        atmState.insertPin(CardNum, Pin);
    }

    public ATMState getYesCardState(){return hasCard;}
    public ATMState getNoCardState(){return noCard;}
    public ATMState getHasPin(){return hasCorrectPin;}

    //end Change ATM state

    //------------------------------------------------------------
    // run
    public void run() {
        Timer.setTimer(id, mbox, pollingTime);
        log.info(id + ": starting...");

        cardReaderMBox = appKickstarter.getThread("CardReaderHandler").getMBox();
        keypadMBox = appKickstarter.getThread("KeypadHandler").getMBox();
        touchDisplayMBox = appKickstarter.getThread("TouchDisplayHandler").getMBox();
        advicePrinterMBox = appKickstarter.getThread("AdvicePrinterHandler").getMBox();
        cashDispenserMBox = appKickstarter.getThread("CashDispenserHandler").getMBox();
        buzzerMBox = appKickstarter.getThread("BuzzerHandler").getMBox();
        cashDepositCollectorMBox = appKickstarter.getThread("CashDepositCollectorHandler").getMBox();


        for (boolean quit = false; !quit; ) {
            Msg msg = mbox.receive();

            log.fine(id + ": message received: [" + msg + "].");

            switch (msg.getType()) {
                case TD_MouseClicked:
                    log.info("MouseCLicked: " + msg.getDetails());
                    processMouseClicked(msg);
                    break;

                case KP_KeyPressed:
                    log.info("KeyPressed: " + msg.getDetails());
                    processKeyPressed(msg);
                    break;

                case CR_CardInserted:
                    this.insertCard();
                    cardNo = msg.getDetails();
                    log.info("CardInserted: " + msg.getDetails());
                    break;

                case CR_EjectCard:
                    this.ejectCard();
                    cardNo = "";
                    log.info("CardEjected: " + msg.getDetails());
                    break;

                case CR_CardRemoved:
                    this.ejectCard();
                    log.info("CardRemoved: " + msg.getDetails());
                    break;
                case TimesUp:
                    Timer.setTimer(id, mbox, pollingTime);
                    log.info("Poll: " + msg.getDetails());
                    cardReaderMBox.send(new Msg(id, mbox, Msg.Type.Poll, ""));
                    keypadMBox.send(new Msg(id, mbox, Msg.Type.Poll, ""));
                    touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.Poll, ""));
                    advicePrinterMBox.send(new Msg(id, mbox, Msg.Type.Poll, ""));
                    break;

                case PollAck:
                    log.info("PollAck: " + msg.getDetails());
                    break;

                case Terminate:
                    quit = true;
                    break;

                default:
                    log.warning(id + ": unknown message type: [" + msg + "]");
            }
        }

        // declaring our departure
        appKickstarter.unregThread(this);
        log.info(id + ": terminating...");
    } // run


    //------------------------------------------------------------
    // processKeyPressed
    private void processKeyPressed(Msg msg) {
        if (atmState == hasCard) {
            String key = msg.getDetails();
            switch(key) {
                case "0": case "1": case "2": case "3": case "4": case "5": case "6": case "7": case "8": case "9":
                    System.out.println("Key Pressed:" + key);
                    pin = pin + key;
                    System.out.println("Pin:" + pin);
                    break;
                case "Cancel":
                    pin = "";
                    cardReaderMBox.send(new Msg(id, mbox, Msg.Type.CR_EjectCard, "Card eject"));
                    break;
                case "Enter":
                    this.insertPin(cardNo, pin);
                    break;
                case "Erase":
                    pin = pin.substring(0, pin.length()-1);
                    break;
                default:
                    break;
            }
        } else {
            if (msg.getDetails().compareToIgnoreCase("Cancel") == 0) {
                cardReaderMBox.send(new Msg(id, mbox, Msg.Type.CR_EjectCard, ""));
            } else if (msg.getDetails().compareToIgnoreCase("1") == 0) {
                touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_UpdateDisplay, "BlankScreen"));
            } else if (msg.getDetails().compareToIgnoreCase("2") == 0) {
                touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_UpdateDisplay, "MainMenu"));
            } else if (msg.getDetails().compareToIgnoreCase("3") == 0) {
                touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_UpdateDisplay, "Confirmation"));
            }
        }
        // *** The following is an example only!! ***
    }
     // processKeyPressed


    //------------------------------------------------------------
    // processMouseClicked
    private void processMouseClicked(Msg msg) {
        // *** process mouse click here!!! ***
    } // processMouseClicked

    //BAMS functions
    static boolean Login(BAMSHandler bams, String cardNo, String pin) throws BAMSInvalidReplyException, IOException {
        String cred = bams.login(cardNo, pin);
        if(cred == "cred-1"){
            return true;
        }else{
            return false;
        }

    }


} // CardReaderHandler
