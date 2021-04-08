package ATMSS.ATMSS;

public class HasCard implements ATMState {

    ATMSS atmMachine;
    public HasCard(ATMSS atmss) {
        atmMachine = atmss;
    }

    @Override
    public void insertCard() {

    }

    @Override
    public void ejectCard() {
        atmMachine.correctPinEntered = false;
        atmMachine.setATMState(atmMachine.getNoCardState());
    }

    @Override
    public void insertPin() {
        atmMachine.correctPinEntered = true;
        atmMachine.setATMState(atmMachine.getHasPin());
    }
}
