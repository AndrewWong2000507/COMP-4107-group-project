package ATMSS.ATMSS;

public interface ATMState {

    void insertCard();
    void ejectCard();
    void insertPin(String CardNum, String Pin);
}
