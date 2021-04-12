package ATMSS.ATMSS;

/**
 * This is the class of ATM is unavailable
 * It implements ATMState
 */
public class Unavailable implements ATMState{

    ATMSS atmMachine;
    public Unavailable(ATMSS atmss) {
        atmMachine = atmss;
    }

    @Override
    /**
     * Method that handle card insert when ATM unavailable
     */
    public void insertCard() {
        System.out.println("ATM unAvailable");
    }

    @Override
    /**
     * Method that handle card eject when ATM unavailable
     */
    public void ejectCard() {
        System.out.println("ATM unAvailable");
    }

    @Override
    /**
     * Method that handle Pin insert when ATM unavailable
     */
    public void insertPin(String CardNum, String Pin) {
        System.out.println("ATM unAvailable");
    }
}
