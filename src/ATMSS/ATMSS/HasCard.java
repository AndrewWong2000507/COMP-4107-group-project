package ATMSS.ATMSS;

import ATMSS.BAMSHandler.BAMSHandler;
import ATMSS.BAMSHandler.BAMSInvalidReplyException;

import java.io.IOException;

public class HasCard implements ATMState {

    ATMSS atmMachine;
    String urlPrefix = "http://cslinux0.comp.hkbu.edu.hk/comp4107_20-21_grp12/BAMS.php";
    BAMSHandler bams = new BAMSHandler(urlPrefix);

    public HasCard(ATMSS atmss) {
        atmMachine = atmss;
    }

    @Override
    public void insertCard() {
        System.out.println("HasCard, Already has Card in the Card Reader!");
    }

    @Override
    public void ejectCard() {
        atmMachine.correctPinEntered = false;
        atmMachine.setATMState(atmMachine.getNoCardState());
        atmMachine.resetCount();
        System.out.println("Card ejected");
        atmMachine.resetPin();
    }

    @Override
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

    boolean login(BAMSHandler bams, String cardNo, String Pin) throws BAMSInvalidReplyException, IOException {
        String cred = bams.login(cardNo, Pin);
        System.out.println("cred: " + cred);
        if (cred.equals("cred-1")) {
            return true;
        } else {
            return false;
        }
    } // testLogin

    String[] getAcc(BAMSHandler bams, String cardNo) throws BAMSInvalidReplyException, IOException {
        String bamsReply = "";
        bamsReply = bams.getAccounts(cardNo, "cred-1");
        String[] accts = bamsReply.split(" ");
        atmMachine.currAcc = accts[0];
        System.out.println(accts[0]);
        return accts;
    }

}
