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
        System.out.println("Card ejected");
    }

    @Override
    public void insertPin(String CardNum, String Pin) {
        boolean session = false;
        try {
            session = login(bams,CardNum, Pin);

        } catch (Exception e) {
            System.out.println("TestBAMSHandler: Exception caught: " + e.getMessage());
            e.printStackTrace();
        }
        if(session){
            atmMachine.correctPinEntered = true;
            atmMachine.setATMState(atmMachine.getHasPin());
        }
    }

    static boolean login(BAMSHandler bams, String cardNo, String Pin) throws BAMSInvalidReplyException, IOException {
        String cred = bams.login(cardNo, Pin);
        System.out.println("cred: " + cred);
        if(cred == "cred-1"){
            return true;
        }else{
            return false;
        }
    } // testLogin
}
