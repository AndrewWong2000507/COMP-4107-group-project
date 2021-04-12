package ATMSS.ATMSS;

import ATMSS.BAMSHandler.BAMSHandler;
import ATMSS.BAMSHandler.BAMSInvalidReplyException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This is the class of ATM having card without PIN
 * It implements ATMState
 */
public class HasCard implements ATMState {

    ATMSS atmMachine;
    String urlPrefix = "http://cslinux0.comp.hkbu.edu.hk/comp4107_20-21_grp12/BAMS.php";
    BAMSHandler bams = new BAMSHandler(urlPrefix);

    /**
     * Constructor of the state
     * @param atmss the ATM system
     */
    public HasCard(ATMSS atmss) {
        atmMachine = atmss;
    }


    @Override
    /**
     * Method that handle insert card when there is card
     */
    public void insertCard() {
        System.out.println("HasCard, Already has Card in the Card Reader!");
    }

    @Override
    /**
     * Method that handle card eject when there is card
     */
    public void ejectCard() {
        atmMachine.correctPinEntered = false;
        atmMachine.setATMState(atmMachine.getNoCardState());
        atmMachine.resetCount();
        System.out.println("Card ejected");
        atmMachine.resetPin();
    }

    @Override
    /**
     * Method that handle Pin insert when there is card
     * Login will be carried out with the PIN inputted
     * List of account will be fetch if login success
     */
    public void insertPin(String CardNum, String Pin) {
        boolean session = false;
        atmMachine.resetPin();
        try {
            session = login(bams, CardNum, Pin);
        } catch (Exception e) {
            System.out.println("TestBAMSHandler: Exception caught: " + e.getMessage());
            e.printStackTrace();
        }
        if (session) {
            try {
                atmMachine.acctList = getAcc(bams, CardNum);
            } catch (Exception e) {
                System.out.println("TestBAMSHandler: Exception caught: " + e.getMessage());
                e.printStackTrace();
            }
            atmMachine.correctPinEntered = true;
            atmMachine.setATMState(atmMachine.getHasPin());
            atmMachine.resetCount();
            System.out.println("Login Succ!");
        }
    }

    /**
     * Method that check whether the PIN input match the card number
     * @param bams handler link to BAMS services
     * @param cardNo card number of the card
     * @param Pin PIN of the user inputted
     * @return true if card number and PIN exist, false of card number and PIN do not match
     * @throws BAMSInvalidReplyException error in BAMS reply
     * @throws IOException error in input/output
     */
    boolean login(BAMSHandler bams, String cardNo, String Pin) throws BAMSInvalidReplyException, IOException {
        String cred = bams.login(cardNo, Pin);
        System.out.println("cred: " + cred);
        if (cred.equals("cred-1")) {
            return true;
        } else {
            return false;
        }
    } // testLogin

    /**
     * Method that fetch the accounts in the card and save temporarily in the ATMSS
     * @param bams handler link to BAMS services
     * @param cardNo card number of the card
     * @return array list of the account in the card
     * @throws BAMSInvalidReplyException in BAMS reply
     * @throws IOException in input/output
     */
    List<String> getAcc(BAMSHandler bams, String cardNo) throws BAMSInvalidReplyException, IOException {
        String bamsReply = "";
        bamsReply = bams.getAccounts(cardNo, "cred-1");
        String[] replys = bamsReply.split(" ");
        atmMachine.currAcc = replys[0];
        return new ArrayList<>(Arrays.asList(replys));
    }

}
