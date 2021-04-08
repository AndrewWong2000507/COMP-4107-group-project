package ATMSS.ATMSS;

public class NoCard implements ATMState {
    ATMSS atmMachine;
    public NoCard(ATMSS atmss) {
        atmMachine = atmss;
    }

    @Override
    public void insertCard() {
        atmMachine.setATMState(atmMachine.getYesCardState());
    }

    @Override
    public void ejectCard() {

    }

    @Override
    public void insertPin(int pinEntered) {

    }
}
