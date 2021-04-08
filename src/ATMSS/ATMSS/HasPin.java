package ATMSS.ATMSS;

import ATMSS.BAMSHandler.BAMSHandler;

public class HasPin implements ATMState {
    ATMSS atmMachine;

    //String urlPrefix = "http://cslinux0.comp.hkbu.edu.hk/comp4107_20-21_grp12/BAMS.php";
    //BAMSHandler bams = new BAMSHandler(urlPrefix);

    public HasPin(ATMSS atmss) {
        atmMachine = atmss;
    }

    @Override
    public void insertCard() {
        System.out.println("HasPin, Already a Card in the Card Reader!");
    }

    @Override
    public void ejectCard() {
        atmMachine.correctPinEntered = false;
        atmMachine.setATMState(atmMachine.getNoCardState());
    }

    @Override
    public void insertPin() {
        System.out.println("Already Logined!");
    }
}
