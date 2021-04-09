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
    ATMState unAvailable;

    private String pin = "";
    public String cardNo = "";
    public int pinCounter = 0;
    boolean outOfCash = false;
    private String userInput;
    private String mode = "";
    private String[] accNo = {"0", "1"}; //temp


    //Create BAMSHandler
    protected BAMSHandler bamsHandler;
    private String urlPrefix = "http://cslinux0.comp.hkbu.edu.hk/comp4107_20-21_grp12/BAMS.php";

    //Create ATM state
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
        unAvailable = new Unavailable(this);
        atmState = noCard;

    } // ATMSS

    //Change ATM state
    void setATMState(ATMState newATMState) {
        atmState = newATMState;
    }

    public void resetCount() {
        pinCounter = 0;
    }

    public void resetPin() {
        pin = "";
    }

    public void resetAll() {
        resetCount();
        resetPin();
    }

    public void insertCard() {
        atmState.insertCard();
    }

    public void ejectCard() {
        atmState.ejectCard();
    }

    public void insertPin(String CardNum, String Pin) {
        atmState.insertPin(CardNum, Pin);
    }

    public ATMState getYesCardState() {
        return hasCard;
    }

    public ATMState getNoCardState() {
        return noCard;
    }

    public ATMState getHasPin() {
        return hasCorrectPin;
    }

    public ATMState getUnAvailable() {
        return unAvailable;
    }

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
        bamsHandler = new BAMSHandler(urlPrefix);
        bamsHandler.setLog(log);

        for (boolean quit = false; !quit; ) {
            if (outOfCash) {
                atmState = unAvailable;
            }
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
        //card inserted
        if (atmState == hasCard) {
            String key = msg.getDetails();
            switch (key) {
                case "0":
                case "1":
                case "2":
                case "3":
                case "4":
                case "5":
                case "6":
                case "7":
                case "8":
                case "9":
                    System.out.println("Key Pressed:" + key);
                    pin = pin + key;
                    System.out.println("Pin:" + pin);
                    break;
                case "Cancel":
                    pin = "";
                    cardReaderMBox.send(new Msg(id, mbox, Msg.Type.CR_EjectCard, "Card eject"));
                    break;
                case "Enter":
                    pinCounter++;
                    if (pinCounter <= 3) {
                        this.insertPin(cardNo, pin);
                    } else {
                        cardReaderMBox.send(new Msg(id, mbox, Msg.Type.CR_CardRemoved, "Card Locked"));
                    }
                    break;
                case "Erase":
                    if (pin.length() > 0) {
                        pin = pin.substring(0, pin.length() - 1);
                    } else {
                        log.info("ATMSS: PIN has nothing for erase");
                    }
                    break;
                default:
                    break;
            }
            //after login
        } else if (atmState == hasCorrectPin) {
            String key = msg.getDetails();
            switch (key) {
                case "0":
                case "1":
                case "2":
                case "3":
                case "4":
                case "5":
                case "6":
                case "7":
                case "8":
                case "9":
                    userInput = userInput + key;
                    log.info("User Input : " + userInput);
                    break;
                case "Cancel":
                    userInput = "";
                    log.info("Process canceled");
                    mode = "";
                    break;
                case "Enter":
                    switch (mode) {
                        case "cash withdrawal":
                            //just some template
                            try {
                                if (Double.parseDouble(userInput) % 100 == 0 && Double.parseDouble(userInput) > 0) {
                                    //only can get $100,$500 and $1000
                                    bamsHandler.withdraw(cardNo, accNo[0], "", userInput);
                                    mode = "";
                                }
                            } catch (BAMSInvalidReplyException | IOException e) {
                                log.info("ATMSS: Error");
                                mode = "";
                            }
                            break;
                        case "cash transaction":
                            //just some template
                            try {
                                if (Double.parseDouble(userInput) > 0) {
                                    bamsHandler.transfer(cardNo, "", accNo[0], accNo[1], userInput);
                                }
                                //send to td transaction success
                                log.info("ATMSS: transfer finished");
                                mode = "";
                            } catch (BAMSInvalidReplyException | IOException e) {
                                log.info("ATMSS: Error");
                                mode = "";
                            }
                            break;
                        case "cash deposit":
                            try {
                                if (Double.parseDouble(userInput) > 0) {
                                    bamsHandler.deposit(cardNo, accNo[0], "", userInput);
                                }
                                //send to td transaction success
                                log.info("ATMSS: deposit finished");
                                mode = "";
                            } catch (BAMSInvalidReplyException | IOException e) {
                                log.info("ATMSS: Error");
                                mode = "";
                            }
                            break;
                        default:
                            log.info("ATMSS: Please choose the service type");
                            //send the msg to display
                    }
                    break;
                case "Erase":
                    if (userInput.length() > 0) {
                        userInput = userInput.substring(0, userInput.length() - 1);
                    } else {
                        log.info("ATMSS: User Input has nothing for erase");
                    }
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

        String[] pos = msg.getDetails().trim().split("\\s+");
        int posX = Integer.parseInt(pos[0]);
        int posY = Integer.parseInt(pos[1]);
        if (posX <= 300 && posY >= 415) {
            //cash deposit
            log.info("pressed cash deposit");
            mode = "cash deposit";
        } else if (posX <= 300 && posY >= 345) {
            //cash withdrawal
            log.info("pressed cash withdrawal");
            mode = "cash withdrawal";
        } else if (posX <= 300 && posY >= 275) {
            //cet account
            log.info("pressed get account");
        } else if (posX >= 340 && posX <= 640 && posY >= 415) {
            //log out
            log.info("pressed logout");
        } else if (posX >= 340 && posX <= 640 && posY >= 345) {
            //balance enquiry
            log.info("pressed balance enquiry");
        } else if (posX >= 340 && posX <= 640 && posY >= 275) {
            //cash transaction
            log.info("pressed cash transaction");
            mode = "cash transaction";
        }

    } // processMouseClicked


} // CardReaderHandler