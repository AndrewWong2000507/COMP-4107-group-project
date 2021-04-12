package ATMSS.ATMSS;

/**
 * This is the interface for all ATM state
 */
public interface ATMState {

    /**
     * This is the insert card function for different ATM state
     */
    void insertCard();
    /**
     * This is the insert card function for different ATM state
     */
    void ejectCard();
    /**
     * This is the insert card function for different ATM state
     * @param CardNum the card number
     * @param Pin the input pin user press on Keyboard
     */
    void insertPin(String CardNum, String Pin);
}
