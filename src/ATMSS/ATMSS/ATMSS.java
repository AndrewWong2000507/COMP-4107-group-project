package ATMSS.ATMSS;

import ATMSS.AdvicePrinterHandler.Emulator.AdvicePrinterEmulator;
import ATMSS.BAMSHandler.BAMSHandler;
import ATMSS.BAMSHandler.BAMSInvalidReplyException;
import AppKickstarter.AppKickstarter;
import AppKickstarter.misc.*;
import AppKickstarter.timer.Timer;

import java.io.IOException;

import java.time.*;
import java.time.format.DateTimeFormatter;
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

    /**
     * This is the different state of the ATM
     * Implemented the state design pattern
     * noCard => There are no Card in the ATMMachine
     * hasCard => There are Card in the ATMMachine
     * hasCorrentPin => User inserted the correct Pin, and successfully login
     * unAvailable => ATMMachine facing fetal error and cannot repair or ignore
     */
    ATMState hasCard;
    ATMState noCard;
    ATMState hasCorrectPin;
    ATMState unAvailable;

    /**
     * Global Variables that are widely used in the ATMMachine
     * Usually Temporary and will reset after some user actions
     * All variables need to be reset once user leave/logout/eject the ATM machine
     */
    private String pin = "";
    public String cardNo = "";
    public int pinCounter = 0;
    boolean outOfCash = false;
    private String userInput = "";
    private String mode = "";
    //acc List changed to array list as transfer press 6 -> i-1 = 5 >= 5 and have bug
    protected List<String> acctList;
    protected String currAcc;
    private int[] cashInventory = new int[3];
    private String destAcc = "";
    private String toPrint = "";
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

    //Create BAMSHandler
    protected BAMSHandler bamsHandler;
    boolean correctPinEntered = false;
    /**
     * Store the current ATM State
     */
    ATMState atmState;

    /**
     * Constructor for ATMSS
     * @param id
     * @param appKickstarter
     * @throws Exception
     */
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

    /**
     * Method to change the ATM State
     * @param newATMState
     */
    void setATMState(ATMState newATMState) {
        atmState = newATMState;
    }

    /**
     * Method to reset Mode variable
     */
    public void resetMode() {
        mode = "";
    }
    /**
     * Method to reset Account List variable
     */
    public void resetAccList() {
        acctList = new ArrayList<>();
    }
    /**
     * Method to reset counter variable
     */
    public void resetCount() {
        pinCounter = 0;
    }
    /**
     * Method to reset Pin variable, trigger ever login
     */
    public void resetPin() {
        pin = "";
    }
    /**
     * Method to reset Printer String variable, trigger ever print function
     */
    public void resetPrint() {
        toPrint = "";
    }
    /**
     * Method to reset all of above, trigger when eject/logout/error
     */
    public void resetAll() {
        resetCount();
        resetPin();
        resetAccList();
        resetMode();
        resetPrint();
    }

    /**
     * Method to handle Card insert, different when ATM are at different state
     */
    public void insertCard() {
        atmState.insertCard();
    }

    /**
     * Method to handle Card eject, different when ATM are at different state
     */
    public void ejectCard() {
        atmState.ejectCard();
    }
    /**
     * Method to handle Pin insert, different when ATM are at different state
     * User are allowed to try maximum three times
     */
    public void insertPin(String CardNum, String Pin) {
        atmState.insertPin(CardNum, Pin);
    }
    /**
     * Method to return a hasCard Sate
     */
    public ATMState getYesCardState() {
        return hasCard;
    }
    /**
     * Method to return a noCard Sate
     */
    public ATMState getNoCardState() {
        return noCard;
    }
    /**
     * Method to return a hasCorrectPin Sate
     */
    public ATMState getHasPin() {
        return hasCorrectPin;
    }

    /**
     * Method to return a Unavailable Sate
     */
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
        String urlPrefix = "http://cslinux0.comp.hkbu.edu.hk/comp4107_20-21_grp12/BAMS.php";
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

                case Unavailiable:
                    atmState = getUnAvailable();
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

                case CD_InventoryPressed:
                    log.info("InventoryPressed: " + msg.getDetails());
                    dispenseCash(msg);
                    break;

                case CDC_CashDeposited:
                    try {
                        bamsHandler.deposit(cardNo, currAcc, "", msg.getDetails());
                        //record Deposit
                        toPrint += dtf.format(LocalDateTime.now()) + " User deposit:" + msg.getDetails() + " From:" + currAcc + "\n";
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

    private void dispenseCash(Msg msg) {
        String[] cash = msg.getDetails().split("/");
        for (int i = 0; i < 3; i++) {
            cashInventory[i] = Integer.parseInt(cash[i]);
        }

    }

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
                        //Record the system time to printer
                        toPrint += "Login Time: " + dtf.format(LocalDateTime.now()) + "\n";
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
                case ".":
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
                            for (int i = 0; i < acctList.size(); i++) {
                                cred += i + 1 + ". " + acctList.get(i) + "\n";

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
                                if (userInput.equals("") || userInput.startsWith("0") || userInput.startsWith(".")) {
                                    cred = "Invalid withdrawal amount. Please input valid withdrawal value\nWithdrawal process cancelled.";
                                    touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_ShowScreen, cred));
                                } else {
                                    int outAmount = Integer.parseInt(userInput);
                                    if (outAmount % 100 == 0) {
                                        int cash1000 = Math.min(outAmount / 1000, cashInventory[0]);
                                        int cash500 = Math.min((outAmount - cash1000 * 1000) / 500, cashInventory[1]);

                                        int cash100 = (outAmount - cash1000 * 1000 - cash500 * 500) / 100;
                                        if (cash100 > cashInventory[2]) {
                                            cashDispenserMBox.send(new Msg(id, mbox, Msg.Type.CD_UpdateDispenser, "OutOfCash"));
                                        } else {
                                            String data = cash1000 + "/" + cash500 + "/" + cash100 + "/" + outAmount;

                                            cashDispenserMBox.send(new Msg(id, mbox, Msg.Type.CD_CashDispense, data));
                                            bamsHandler.withdraw(cardNo, currAcc, "", userInput);
                                            double enquiry = bamsHandler.enquiry(cardNo, currAcc, "");
                                            cred = "Withdrawal success. Please collect cash from the dispenser.\nCurrent account " + currAcc + " balance : " + enquiry;
                                            touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_ShowScreen, cred));
                                        }

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
                                        //Record the print statement to printer
                                        toPrint += dtf.format(LocalDateTime.now()) + " User withdrawal:" + outAmount + " From:" + currAcc + "\n";
                                        cred = "Withdrawal success. Please collect cash from the dispenser.\nCurrent account " + currAcc + " balance : " + enquiry;
                                        touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_ShowScreen, cred));
                                    } else {
                                        cred = "Invalid withdrawal amount. ATM only provide $100, $500 and $1000 cash\nWithdrawal process cancelled.";
                                        touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_ShowScreen, cred));
                                    }
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
                            if (userInput.equals("") || userInput.startsWith("0") || userInput.startsWith(".")) {
                                cred = "Invalid account number. Please input valid account number\ntransaction process cancelled.";
                                touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_ShowScreen, cred));
                                resetMode();
                            } else {
                                int i = Integer.parseInt(userInput);
                                System.out.println("list: " + acctList.size() + " i: " + (i - 1));
                                if (i - 1 >= acctList.size()) {
                                    cred = "Invalid option! This card only contain " + acctList.size() + " account(s).\nCurrent account: " + currAcc + "\nTransaction is cancelled. Please try again.";
                                    touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_ShowScreen, cred));
                                    resetMode();
                                    userInput = "";
                                } else {
                                    destAcc = acctList.get(i - 1);
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
                                }
                            }
                            userInput = "";
                            break;
                        case "cash transaction amount":
                            //just some template
                            boolean success = false;
                            log.info("ATMSS: user Input " + userInput);
                            try {
                                if (userInput.equals("") || userInput.startsWith("0") || userInput.startsWith(".")) {
                                    cred = "Invalid deposit amount. Please input valid deposit amount\ntransaction process cancelled.";
                                    touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_ShowScreen, cred));
                                } else {
                                    if (Double.parseDouble(userInput) >= 0) {
                                        double getTransAmount = bamsHandler.transfer(cardNo, "", currAcc, destAcc, userInput);

                                        if (getTransAmount > 0) {
                                            success = true;
                                        }
                                        if (success) {
                                            toPrint += dtf.format(LocalDateTime.now()) + " User transfer:" + userInput + " From:" + currAcc + " To:" + destAcc + "\n";
                                            log.info("Transfer Success");
                                        } else {
                                            log.info("Client do not have enough balance");
                                        }
                                    }
                                    //send to td transaction success
                                    log.info("ATMSS: transfer finished");
                                    double enquiry1 = bamsHandler.enquiry(cardNo, currAcc, "");
                                    double enquiry2 = bamsHandler.enquiry(cardNo, destAcc, "");
                                    if (success) {
                                        cred = "Transfer complete\n\n" + "Current account " + currAcc + " balance : " + enquiry1 + "\n\nDestination account " + destAcc + " balance : " + enquiry2;
                                    } else {
                                        cred = "Transfer Failed, amount no Change\n\n" + "Current account " + currAcc + " balance : " + enquiry1 + "\n\nDestination account " + destAcc + " balance : " + enquiry2;
                                    }
                                    touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_ShowScreen, cred));
                                }
                                userInput = "";
                                resetMode();
                            } catch (BAMSInvalidReplyException | IOException e) {
                                log.info("ATMSS: Error");
                                resetMode();
                                userInput = "";
                            }
                            break;
                        case "cash deposit":
                            cred = "Please put in cash into collector and press confirm to finish deposit process";
                            touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_ShowScreen, cred));

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
        } else if (atmState == noCard) {
            if (msg.getDetails().compareToIgnoreCase("Cancel") == 0) {
                cardReaderMBox.send(new Msg(id, mbox, Msg.Type.CR_EjectCard, ""));
            } else if (msg.getDetails().compareToIgnoreCase("1") == 0) {
                touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_UpdateDisplay, "BlankScreen"));
            }
        } else {
            cardReaderMBox.send(new Msg(id, mbox, Msg.Type.CR_EjectCard, ""));
            String cred = "ATM currently unavailable";
            touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_ShowScreen, cred));
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
            cashDispenserMBox.send(new Msg(id, mbox, Msg.Type.CD_GetInventoryForDispense, ""));

            touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_ShowScreen, cred));
            mode = "cash withdrawal";
            log.info("ATM mode : " + mode);
        } else if (posX <= 300 && posY >= 275 && atmState == hasCorrectPin) {
            //get account
            log.info("pressed get account");
            String cred = "Account(s) in current card:\n";
            for (String acc : acctList) {
                cred += acc + "\n";
            }
            touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_ShowScreen, cred));
        } else if (posX >= 340 && posX <= 640 && posY >= 415 && atmState == hasCorrectPin) {
            //log out
            log.info("pressed logout");
            //Send to Printer
            advicePrinterMBox.send(new Msg(id, mbox, Msg.Type.AP_print, toPrint));
            touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_UpdateDisplay, "BlankScreen"));
            resetAll();
            this.ejectCard();
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

            for (int i = 0; i < acctList.size(); i++) {
                cred += i + 1 + ". " + acctList.get(i) + "\n";

            }
            touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_ShowScreen, cred));
            log.info("ATM mode : " + mode);
        } else if (posX <= 120 && posY >= 160 && atmState == hasCorrectPin) {
            //change curr account button 5
            int accAmount = acctList.size();
            System.out.println("Total Acc Number: " + accAmount);
            //the number of account is more than 5
            if (accAmount >= 5) {
                //set curr acc as 5th acc in the list
                currAcc = acctList.get(4);
                String cred = "Customer is now accessing Account 5 :" + currAcc + "\n";
                touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_ShowScreen, cred));
            } else {
                String cred = "Invalid operation. Only " + accAmount + " account exist in this card.\n";
                touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_ShowScreen, cred));
            }
        } else if (posX <= 120 && posY >= 120 && atmState == hasCorrectPin) {
            //change curr account button 4
            int accAmount = acctList.size();
            System.out.println("Total Acc Number: " + accAmount);
            if (accAmount >= 4) {
                currAcc = acctList.get(3);
                String cred = "Customer is now accessing Account 4 : " + currAcc + "\n";
                touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_ShowScreen, cred));
            } else {
                String cred = "Invalid operation. Only " + accAmount + " account exist in this card.\n";
                touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_ShowScreen, cred));
            }
        } else if (posX <= 120 && posY >= 80 && atmState == hasCorrectPin) {
            //change curr account button 3
            int accAmount = acctList.size();
            System.out.println("Total Acc Number: " + accAmount);
            if (accAmount >= 3) {
                currAcc = acctList.get(2);
                String cred = "Customer is now accessing Account 3 : " + currAcc + "\n";
                touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_ShowScreen, cred));
            } else {
                String cred = "Invalid operation. Only " + accAmount + " account exist in this card.\n";
                touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_ShowScreen, cred));
            }
        } else if (posX <= 120 && posY >= 40 && atmState == hasCorrectPin) {
            //change curr account button 2
            int accAmount = acctList.size();
            System.out.println("Total Acc Number: " + accAmount);
            if (accAmount >= 2) {
                currAcc = acctList.get(1);
                String cred = "Customer is now accessing Account 2 : " + currAcc + "\n";
                touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_ShowScreen, cred));
            } else {
                String cred = "Invalid operation. Only " + accAmount + " account exist in this card.\n";
                touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_ShowScreen, cred));
            }
        } else if (posX <= 120 && posY >= 0 && atmState == hasCorrectPin) {
            //change curr account button 1
            int accAmount = acctList.size();
            System.out.println("Total Acc Number: " + accAmount);
            if (accAmount >= 1) {
                currAcc = acctList.get(0);
                String cred = "Customer is now accessing Account 1 : " + currAcc + "\n";
                touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_ShowScreen, cred));
            } else {
                String cred = "Invalid operation. Only " + accAmount + " account exist in this card.\n";
                touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_ShowScreen, cred));
            }
        }

    } // processMouseClicked


} // CardReaderHandler