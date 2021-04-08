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
        System.out.println("NoCard,no card in the Card Reader!");
    }

    @Override
    public void insertPin(String CardNum, String Pin) {
        System.out.println("NoCard,no card in the Card Reader!");
    }
}
