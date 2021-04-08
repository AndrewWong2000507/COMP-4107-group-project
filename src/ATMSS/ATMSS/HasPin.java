package ATMSS.ATMSS;

public class HasPin implements ATMState {
    ATMSS atmMachine;
    public HasPin(ATMSS atmss) {
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

    }
}
