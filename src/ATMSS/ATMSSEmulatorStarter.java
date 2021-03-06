package ATMSS;

import ATMSS.BuzzerHandler.BuzzerHandler;
import ATMSS.BuzzerHandler.Emulator.BuzzerEmulator;
import ATMSS.CashDepositCollectorHandler.Emulator.CashDepositCollectorEmulator;
import ATMSS.CashDepositCollectorHandler.CashDepositCollectorHandler;
import AppKickstarter.timer.Timer;

import ATMSS.ATMSS.ATMSS;
import ATMSS.CardReaderHandler.Emulator.CardReaderEmulator;
import ATMSS.KeypadHandler.KeypadHandler;
import ATMSS.TouchDisplayHandler.Emulator.TouchDisplayEmulator;
import ATMSS.CardReaderHandler.CardReaderHandler;
import ATMSS.KeypadHandler.Emulator.KeypadEmulator;
import ATMSS.TouchDisplayHandler.TouchDisplayHandler;
import ATMSS.AdvicePrinterHandler.AdvicePrinterHandler;
import ATMSS.AdvicePrinterHandler.Emulator.AdvicePrinterEmulator;
import ATMSS.CashDispenserHandler.CashDispenserHandler;
import ATMSS.CashDispenserHandler.Emulator.CashDispenserEmulator;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

//======================================================================
// ATMSSEmulatorStarter
public class ATMSSEmulatorStarter extends ATMSSStarter {
    //------------------------------------------------------------
    // main
    public static void main(String[] args) {
        new ATMSSEmulatorStarter().startApp();
    } // main


    //------------------------------------------------------------
    // startHandlers
    @Override
    protected void startHandlers() {
        Emulators.atmssEmulatorStarter = this;
        new Emulators().start();
    } // startHandlers


    //------------------------------------------------------------
    // Emulators
    public static class Emulators extends Application {
        private static ATMSSEmulatorStarter atmssEmulatorStarter;

        //----------------------------------------
        // start
        public void start() {
            launch();
        } // start

        //----------------------------------------
        // start
        public void start(Stage primaryStage) {
            Timer timer = null;
            ATMSS atmss = null;
            CardReaderEmulator cardReaderEmulator = null;
            KeypadEmulator keypadEmulator = null;
            TouchDisplayEmulator touchDisplayEmulator = null;
            AdvicePrinterEmulator advicePrinterEmulator = null;
            CashDispenserEmulator cashDispenserEmulator = null;
            BuzzerEmulator buzzerEmulator = null;
            CashDepositCollectorEmulator cashDepositCollectorEmulator = null;
            // create emulators
            try {
                timer = new Timer("timer", atmssEmulatorStarter);
                atmss = new ATMSS("ATMSS", atmssEmulatorStarter);
                cardReaderEmulator = new CardReaderEmulator("CardReaderHandler", atmssEmulatorStarter);
                keypadEmulator = new KeypadEmulator("KeypadHandler", atmssEmulatorStarter);
                touchDisplayEmulator = new TouchDisplayEmulator("TouchDisplayHandler", atmssEmulatorStarter);
                advicePrinterEmulator = new AdvicePrinterEmulator("AdvicePrinterHandler", atmssEmulatorStarter);
                cashDispenserEmulator = new CashDispenserEmulator("CashDispenserHandler", atmssEmulatorStarter);
                buzzerEmulator = new BuzzerEmulator("BuzzerHandler", atmssEmulatorStarter);
                cashDepositCollectorEmulator = new CashDepositCollectorEmulator("CashDepositCollectorHandler", atmssEmulatorStarter);
                // start emulator GUIs
                keypadEmulator.start();
                cardReaderEmulator.start();
                touchDisplayEmulator.start();
                advicePrinterEmulator.start();
                cashDispenserEmulator.start();
                buzzerEmulator.start();
                cashDepositCollectorEmulator.start();
            } catch (Exception e) {
                System.out.println("Emulators: start failed");
                e.printStackTrace();
                Platform.exit();
            }
            atmssEmulatorStarter.setTimer(timer);
            atmssEmulatorStarter.setATMSS(atmss);
            atmssEmulatorStarter.setCardReaderHandler(cardReaderEmulator);
            atmssEmulatorStarter.setKeypadHandler(keypadEmulator);
            atmssEmulatorStarter.setTouchDisplayHandler(touchDisplayEmulator);
            atmssEmulatorStarter.setAdvicePrinterHandler(advicePrinterEmulator);
            atmssEmulatorStarter.setCashDispenserHandler(cashDispenserEmulator);
            atmssEmulatorStarter.setBuzzerHandler(buzzerEmulator);
            atmssEmulatorStarter.setCashDepositCollectorHandler(cashDepositCollectorEmulator);
            // start threads
            new Thread(timer).start();
            new Thread(atmss).start();
            new Thread(cardReaderEmulator).start();
            new Thread(keypadEmulator).start();
            new Thread(touchDisplayEmulator).start();
            new Thread(advicePrinterEmulator).start();
            new Thread(cashDispenserEmulator).start();
            new Thread(buzzerEmulator).start();
            new Thread(cashDepositCollectorEmulator).start();
        } // start
    } // Emulators


    //------------------------------------------------------------
    //  setters
    private void setTimer(Timer timer) {
        this.timer = timer;
    }

    private void setATMSS(ATMSS atmss) {
        this.atmss = atmss;
    }

    private void setCardReaderHandler(CardReaderHandler cardReaderHandler) {
        this.cardReaderHandler = cardReaderHandler;
    }

    private void setKeypadHandler(KeypadHandler keypadHandler) {
        this.keypadHandler = keypadHandler;
    }

    private void setTouchDisplayHandler(TouchDisplayHandler touchDisplayHandler) {
        this.touchDisplayHandler = touchDisplayHandler;
    }

    private void setAdvicePrinterHandler(AdvicePrinterHandler advicePrinterHandler) {
        this.advicePrinterHandler = advicePrinterHandler;
    }

    private void setCashDispenserHandler(CashDispenserHandler cashDispenserHandler) {
        this.cashDispenserHandler = cashDispenserHandler;
    }

    private void setBuzzerHandler(BuzzerHandler buzzerHandler) {
        this.buzzerHandler = buzzerHandler;
    }

    private void setCashDepositCollectorHandler(CashDepositCollectorHandler cashDepositCollectorHandler) {
        this.cashDepositCollectorHandler = cashDepositCollectorHandler;
    }
}// ATMSSEmulatorStarter
