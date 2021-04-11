package ATMSS.ATMSS;

import ATMSS.BAMSHandler.BAMSHandler;
import ATMSS.BAMSHandler.BAMSInvalidReplyException;
import AppKickstarter.AppKickstarter;
import AppKickstarter.misc.*;
import AppKickstarter.timer.Timer;

import java.io.IOException;

import java.util.*;

//======================================================================
// ATMSS
public class ATMSS extends AppThread {
    private final int pollingTime;
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
    private String userInput = "";
    private String mode = "";
    protected String[] acctList;
    protected String currAcc;
    public int count = 0;
    private String destAcc = "";

    //changed
    private final int p = 0;
    private final String amount = "";


    //Create BAMSHandler
    protected BAMSHandler bamsHandler;
    private final String urlPrefix = "http://cslinux0.comp.hkbu.edu.hk/comp4107_20-21_grp12/BAMS.php";

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

    public void resetMode() {
        mode = "";
    }

    public void resetAccList() {
        acctList = new String[5];
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
        resetAccList();
        resetMode();
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
                    resetAll();
                    cardReaderMBox.send(new Msg(id, mbox, Msg.Type.CR_EjectCard, "Card eject"));
                    touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_UpdateDisplay, "BlankScreen"));
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

                case CDC_CashDeposited:
                    try {
                        bamsHandler.deposit(cardNo, currAcc, "", msg.getDetails());
                        resetMode();
                        double enquiry = bamsHandler.enquiry(cardNo, currAcc, "");
                        String cred = "Deposit Success!\nAmount:" + msg.getDetails() + "\nCard No. : " + cardNo + "\nAccount : " + currAcc + "\nBalance : $" + enquiry + "\n";
                        touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_ShowScreen, cred));
                    } catch (BAMSInvalidReplyException | IOException e) {
                        e.printStackTrace();
                    }

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
                case "00":
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
                    if (atmState == hasCorrectPin) {
                        touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_UpdateDisplay, "MainMenu"));
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
            log.info("ATMSS state: has correct PIN");
            String key = msg.getDetails();
            String cred = "";
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
                case "00":
                    userInput = userInput + key;
                    log.info("User Input : " + userInput);
                    break;
                case "Cancel":
                    userInput = "";
                    switch (mode) {
                        case "cash deposit":
                            cashDepositCollectorMBox.send(new Msg(id, mbox, Msg.Type.CDC_CashDepositorOpen, ""));
                            resetMode();
                            userInput = "";
                            cred = "Cash deposit process is cancelled. \nDeposit collector is closing";
                            touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_ShowScreen, cred));
                            break;
                        case "cash transaction":
                            resetMode();
                            userInput = "";
                            cred = "Cash transaction process is cancelled.";
                            touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_ShowScreen, cred));
                            break;
                        case "cash transaction amount":
                            //step back to ask dest acc step
                            mode = "cash transaction";
                            destAcc = "";
                            //Ask user click the account
                            cred = "Please select your destination account\n";
                            for (int i = 0; i < acctList.length; i++) {
                                cred += i + 1 + ". " + acctList[i] + "\n";

                            }
                            touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_ShowScreen, cred));
                            log.info("ATM mode : " + mode);
                            break;
                        case "cash withdrawal":
                            userInput = "";
                            cred = "Withdrawal Process cancelled";
                            touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_ShowScreen, cred));
                            resetMode();
                            break;
                        default:
                            userInput = "";
                            log.info("Process canceled");
                            resetMode();
                    }
                    break;
                case "Enter":
                    switch (mode) {
                        case "cash withdrawal":

                            log.info("ATMSS: user Input " + userInput);
                            try {
                                int outAmount = Integer.parseInt(userInput);  //test
                                if (outAmount % 100 == 0) {
                                    String data = "";
                                    //this will be the $1000
                                    data += outAmount / 1000 + "/";
                                    //this will be the $500
                                    data += (outAmount % 1000) / 500 + "/";
                                    //this will be the $100
                                    data += ((outAmount % 1000) % 500) / 100 + "/";
                                    //this will be the total
                                    data += userInput;
                                    System.out.println(data);
                                    cashDispenserMBox.send(new Msg(id, mbox, Msg.Type.CD_CashDispense, data));
                                    bamsHandler.withdraw(cardNo, currAcc, "", userInput);
                                    double enquiry = bamsHandler.enquiry(cardNo, currAcc, "");
                                    cred = "Withdrawal success. Please collect cash from the dispenser.\nCurrent account " + currAcc + " balance : " + enquiry;
                                    touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_ShowScreen, cred));
                                } else {
                                    cred = "Invalid withdrawal amount. ATM only provide $100, $500 and $1000 cash\nWithdrawal process cancelled.";
                                    touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_ShowScreen, cred));
                                }
                                userInput = "";
                                resetMode();
                                break;
                            } catch (BAMSInvalidReplyException | IOException e) {
                                log.info("ATMSS: Error");
                                resetMode();
                            }
                            break;
                        case "cash transaction":
                            //Ask user choice the target account
                            int i = Integer.parseInt(userInput);
                            if (i - 1 > acctList.length) {
                                cred = "Invalid option! This card only contain " + acctList.length + " account(s).\nCurrent account: " + currAcc + "\nTransaction is cancelled. Please try again.";
                                touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_ShowScreen, cred));
                                resetMode();
                                userInput = "";
                            } else {
                                destAcc = acctList[i - 1];
                            }
                            if (currAcc.equals(destAcc)) {
                                //when choosing same ac as currACC
                                log.info(id + ": Cannot transfer from same acc to same acc");
                                cred = "Invalid option! Please select other account that you are not currently accessing.\nCurrent account: " + currAcc + "\nTransaction is cancelled. Please try again.";
                                touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_ShowScreen, cred));
                                resetMode();
                            } else {
                                //chose other ACC
                                log.info(id + ": account " + destAcc + " is chosen");
                                mode = "cash transaction amount";
                                cred = "Please input the amount you want to transfer: \n";
                                touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_ShowScreen, cred));
                            }
                            userInput = "";
                            break;
                        case "cash transaction amount":
                            //just some template
                            log.info("ATMSS: user Input " + userInput);
                            try {
                                if (Double.parseDouble(userInput) >= 0) {
                                    bamsHandler.transfer(cardNo, "", currAcc, destAcc, userInput);
                                    System.out.println("Transfer Success!");
                                }
                                //send to td transaction success
                                log.info("ATMSS: transfer finished");
                                double enquiry1 = bamsHandler.enquiry(cardNo, currAcc, "");
                                double enquiry2 = bamsHandler.enquiry(cardNo, destAcc, "");
                                cred = "Transfer complete\n\nCurrent account " + currAcc + " balance : " + enquiry1 + "\n\nDestination account " + destAcc + " balance : " + enquiry2;
                                touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_ShowScreen, cred));
                                resetMode();
                                userInput = "";
                            } catch (BAMSInvalidReplyException | IOException e) {
                                log.info("ATMSS: Error");
                                resetMode();
                                userInput = "";
                            }
                            break;
                        default:
                            userInput = "";
                            log.info("ATMSS: Please choose the service type");
                            cred = "Please select the required service";
                            touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_ShowScreen, cred));
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

        if (posX <= 300 && posY >= 415 && atmState == hasCorrectPin) {
            //cash deposit
            log.info("pressed cash deposit");
            cashDepositCollectorMBox.send(new Msg(id, mbox, Msg.Type.CDC_CashDepositorOpen, ""));
            String cred = "Please deposit cash into the collector";
            touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_ShowScreen, cred));
            mode = "cash deposit";
            log.info("ATM mode : " + mode);
        } else if (posX <= 300 && posY >= 345 && atmState == hasCorrectPin) {
            //cash withdrawal
            log.info("pressed cash withdrawal");
            String cred = "Please enter the amount to be withdrawn";
            touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_ShowScreen, cred));
            mode = "cash withdrawal";
            log.info("ATM mode : " + mode);
        } else if (posX <= 300 && posY >= 275 && atmState == hasCorrectPin) {
            //cet account
            log.info("pressed get account");
            String cred = "Account(s) in current card:\n";
            for (String acc : acctList) {
                cred += acc + "\n";
            }
            touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_ShowScreen, cred));
        } else if (posX >= 340 && posX <= 640 && posY >= 415 && atmState == hasCorrectPin) {
            //log out
            log.info("pressed logout");
            touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_UpdateDisplay, "Confirmation"));
        } else if (posX >= 340 && posX <= 640 && posY >= 345 && atmState == hasCorrectPin) {
            //balance enquiry
            log.info("pressed balance enquiry");
            try {
                double enquiry = bamsHandler.enquiry(cardNo, currAcc, "");
                String cred = "Card No. : " + cardNo + "\nAccount : " + currAcc + "\nBalance : $" + enquiry + "\n";
                touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_ShowScreen, cred));
            } catch (BAMSInvalidReplyException | IOException e) {
                e.printStackTrace();
            }
        } else if (posX >= 340 && posX <= 640 && posY >= 275 && atmState == hasCorrectPin) {
            //cash transaction
            log.info("pressed cash transaction");
            mode = "cash transaction";
            //Ask user click the account
            String cred = "Which is your destination Account?\n";

            for (int i = 0; i < acctList.length; i++) {
                cred += i + 1 + ". " + acctList[i] + "\n";

            }
            touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_ShowScreen, cred));
            log.info("ATM mode : " + mode);
        } else if (posX <= 120 && posY >= 160 && atmState == hasCorrectPin) {
            //change curr account button 5
            int accAmount = acctList.length;
            System.out.println("Total Acc Number: " + accAmount);
            if (accAmount >= 5) {
                currAcc = acctList[4];
                String cred = "Customer is now accessing Account 5 :" + currAcc + "\n";
                touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_ShowScreen, cred));
            } else {
                String cred = "Invalid operation. Only " + accAmount + " account exist in this card.\n";
                touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_ShowScreen, cred));
            }
        } else if (posX <= 120 && posY >= 120 && atmState == hasCorrectPin) {
            //change curr account button 4
            int accAmount = acctList.length;
            System.out.println("Total Acc Number: " + accAmount);
            if (accAmount >= 4) {
                currAcc = acctList[3];
                String cred = "Customer is now accessing Account 4 : " + currAcc + "\n";
                touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_ShowScreen, cred));
            } else {
                String cred = "Invalid operation. Only " + accAmount + " account exist in this card.\n";
                touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_ShowScreen, cred));
            }
        } else if (posX <= 120 && posY >= 80 && atmState == hasCorrectPin) {
            //change curr account button 3
            int accAmount = acctList.length;
            System.out.println("Total Acc Number: " + accAmount);
            if (accAmount >= 3) {
                currAcc = acctList[2];
                String cred = "Customer is now accessing Account 3 : " + currAcc + "\n";
                touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_ShowScreen, cred));
            } else {
                String cred = "Invalid operation. Only " + accAmount + " account exist in this card.\n";
                touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_ShowScreen, cred));
            }
        } else if (posX <= 120 && posY >= 40 && atmState == hasCorrectPin) {
            //change curr account button 2
            int accAmount = acctList.length;
            System.out.println("Total Acc Number: " + accAmount);
            if (accAmount >= 2) {
                currAcc = acctList[1];
                String cred = "Customer is now accessing Account 2 : " + currAcc + "\n";
                touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_ShowScreen, cred));
            } else {
                String cred = "Invalid operation. Only " + accAmount + " account exist in this card.\n";
                touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_ShowScreen, cred));
            }
        } else if (posX <= 120 && posY >= 0 && atmState == hasCorrectPin) {
            //change curr account button 1
            int accAmount = acctList.length;
            System.out.println("Total Acc Number: " + accAmount);
            if (accAmount >= 1) {
                currAcc = acctList[0];
                String cred = "Customer is now accessing Account 1 : " + currAcc + "\n";
                touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_ShowScreen, cred));
            } else {
                String cred = "Invalid operation. Only " + accAmount + " account exist in this card.\n";
                touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_ShowScreen, cred));
            }
        }

    } // processMouseClicked


} // CardReaderHandler