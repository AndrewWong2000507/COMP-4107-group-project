package ATMSS.ATMSS;

/**
 * This is the class of ATM do not have card in card reader
 * It implements ATMState
 */
public class NoCard implements ATMState {
    ATMSS atmMachine;
    public NoCard(ATMSS atmss) {
        atmMachine = atmss;
    }

    @Override
    /**
     * Method that handle card insert when there is no card in card reader
     */
    public void insertCard() {
        atmMachine.resetCount();
        atmMachine.setATMState(atmMachine.getYesCardState());
    }

    @Override
    /**
     * Method that handle card eject when there is no card in card reader
     */
    public void ejectCard() {
        System.out.println("NoCard,no card in the Card Reader!");
    }

    @Override
    /**
     * Method that handle Pin insert when there is no card in card reader
     */
    public void insertPin(String CardNum, String Pin) {
        System.out.println("NoCard,no card in the Card Reader!");
    }
}
