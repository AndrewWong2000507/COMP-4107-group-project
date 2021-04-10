package ATMSS.CashDepositCollectorHandler.Emulator;

import AppKickstarter.AppKickstarter;
import AppKickstarter.misc.MBox;
import AppKickstarter.misc.Msg;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.util.logging.Logger;

public class CashDepositCollectorEmulatorController {
    public TextField numOf100;
    public TextField numOf500;
    public TextField numOf1000;
    public TextArea infoPanel;
    public Button confirmDepositButton;
    public Button cancelDepositButton;

    private String id;
    private CashDepositCollectorEmulator cashDepositCollectorEmulator;
    private AppKickstarter appKickstarter;
    private Logger log;
    private MBox cashDepositCollectorMBox;

    public void initialize(String id, CashDepositCollectorEmulator cashDepositCollectorEmulator, AppKickstarter appKickstarter, Logger log) {
        this.appKickstarter = appKickstarter;
        this.cashDepositCollectorEmulator = cashDepositCollectorEmulator;
        this.id = id;
        this.log = log;
        this.cashDepositCollectorMBox = appKickstarter.getThread("CashDepositCollectorHandler").getMBox();
    }


    public void receiveDeposit(ActionEvent actionEvent) {
        try {
            System.out.println("$100: " + numOf100.getText() + " $500: " + numOf500.getText() + " $1000: " + numOf1000.getText());
            int hundredDollar;
            if (numOf100.getText().equals("")) {
                hundredDollar = 0;
            } else {
                hundredDollar = Integer.parseInt(numOf100.getText());
            }
            int fiveHundredDollar;
            if (numOf500.getText().equals("")) {
                fiveHundredDollar = 0;
            } else {
                fiveHundredDollar = Integer.parseInt(numOf500.getText());
            }
            int thousandDollar;
            if (numOf1000.getText().equals("")) {
                thousandDollar = 0;
            } else {
                thousandDollar = Integer.parseInt(numOf1000.getText());
            }
            if (hundredDollar >= 0 && fiveHundredDollar >= 0 && thousandDollar >= 0) {
                String total = String.valueOf(100 * hundredDollar + 500 * fiveHundredDollar + 1000 * thousandDollar);
                infoPanel.appendText("Cash deposited :" + total + "\n");
                cashDepositCollectorMBox.send(new Msg(id, cashDepositCollectorMBox, Msg.Type.CDC_CashDeposited, total));
            } else {
                log.warning(id + ": Invalid deposit");
            }
        } catch (Exception e) {
            log.info(id + " : input error");
        }
        cashDepositCollectorEmulator.handleCashDepositor();
        numOf1000.setText("0");
        numOf100.setText("0");
        numOf500.setText("0");
    }
}
