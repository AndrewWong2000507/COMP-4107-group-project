package ATMSS.ATMSS;

import ATMSS.BAMSHandler.BAMSHandler;

public class HasCard implements ATMState {

    ATMSS atmMachine;
    //String urlPrefix = "http://cslinux0.comp.hkbu.edu.hk/comp4107_20-21_grp12/BAMS.php";
    //BAMSHandler bams = new BAMSHandler(urlPrefix);
    public HasCard(ATMSS atmss) {
        atmMachine = atmss;
    }

    @Override
    public void insertCard() {
        System.out.println("HasCard, Already has Card in the Card Reader!");
    }

    @Override
    public void ejectCard() {
        //atmMachine.correctPinEntered = false;
        atmMachine.setATMState(atmMachine.getNoCardState());
        System.out.println("Card ejected");
    }

    @Override
    public void insertPin() {
        atmMachine.correctPinEntered = true;
        atmMachine.setATMState(atmMachine.getHasPin());
    }
}
