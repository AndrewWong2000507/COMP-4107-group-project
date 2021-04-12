package ATMSS.ATMSS;

import ATMSS.BAMSHandler.BAMSHandler;

/**
 * This is the class of ATM having card with PIN
 * It implements ATMState
 */
public class HasPin implements ATMState {
    ATMSS atmMachine;
    //String urlPrefix = "http://cslinux0.comp.hkbu.edu.hk/comp4107_20-21_grp12/BAMS.php";
    //BAMSHandler bams = new BAMSHandler(urlPrefix);

    /**
     * Constructor of the state
     * @param atmss the ATM system
     */
    public HasPin(ATMSS atmss) {
        atmMachine = atmss;
    }

    @Override
    /**
     * Method that handle insert card when there is card and login finished
     */
    public void insertCard() {
        System.out.println("HasPin, Already a Card in the Card Reader!");
    }

    @Override
    /**
     * Method that handle card eject when user has log inned
     */
    public void ejectCard() {
        atmMachine.resetAll();
        System.out.println("Log out");
        atmMachine.correctPinEntered = false;
        atmMachine.setATMState(atmMachine.getNoCardState());
    }

    @Override
    /**
     * Method that handle Pin insert when user has log inned
     */
    public void insertPin(String CardNum, String Pin) {
        System.out.println("Already Logined!");
    }


}
