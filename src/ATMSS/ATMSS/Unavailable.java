package ATMSS.ATMSS;

public class Unavailable implements ATMState{

    ATMSS atmMachine;
    public Unavailable(ATMSS atmss) {
        atmMachine = atmss;
    }

    @Override
    public void insertCard() {
        System.out.println("ATM unAvailable");
    }

    @Override
    public void ejectCard() {
        System.out.println("ATM unAvailable");
    }

    @Override
    public void insertPin(String CardNum, String Pin) {
        System.out.println("ATM unAvailable");
    }
}
