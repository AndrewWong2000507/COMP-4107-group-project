package ATMSS.CardReaderHandler.Emulator;

import AppKickstarter.AppKickstarter;
import AppKickstarter.misc.MBox;
import AppKickstarter.misc.Msg;

import java.util.logging.Logger;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;


/**
 * This is the class for handling the Advice Printer
 * @author Group 12
 * */
public class CardReaderEmulatorController {
    private String id;
    private AppKickstarter appKickstarter;
    private Logger log;
    private CardReaderEmulator cardReaderEmulator;
    private MBox cardReaderMBox;
    public TextField cardNumField;
    public TextField cardStatusField;
    public TextArea cardReaderTextArea;


    /**
     * This method initialize the controller
     * @param id name of the appThread
     * @param appKickstarter a reference to our AppKickstarter
     * @param log the logger will be used to log
     * @param cardReaderEmulator defining the emulator this controller is linked with
     */
    public void initialize(String id, AppKickstarter appKickstarter, Logger log, CardReaderEmulator cardReaderEmulator) {
        this.id = id;
        this.appKickstarter = appKickstarter;
        this.log = log;
        this.cardReaderEmulator = cardReaderEmulator;
        this.cardReaderMBox = appKickstarter.getThread("CardReaderHandler").getMBox();
    } // initialize


    /**
     * This method is to handle if the button on the emulator is clicked
     * @param actionEvent
     */
    public void buttonPressed(ActionEvent actionEvent) {
        Button btn = (Button) actionEvent.getSource();

        switch (btn.getText()) {
            case "Card 1":
                cardNumField.setText(appKickstarter.getProperty("CardReader.Card1"));
                break;

            case "Card 2":
                cardNumField.setText(appKickstarter.getProperty("CardReader.Card2"));
                break;

            case "Card 3":
                cardNumField.setText(appKickstarter.getProperty("CardReader.Card3"));
                break;

            case "Reset":
                cardNumField.setText("");
                break;

            case "Insert Card":
                if (cardNumField.getText().length() != 0) {
                    cardReaderMBox.send(new Msg(id, cardReaderMBox, Msg.Type.CR_CardInserted, cardNumField.getText()));
                    cardReaderTextArea.appendText("Sending " + cardNumField.getText() + "\n");
                    cardStatusField.setText("Card Inserted, Please Input your Pin");
                }
                break;
            case "Eject Card":
                if (cardStatusField.getText().compareTo("Card Inserted, Please Input your Pin") == 0) {
                    cardReaderTextArea.appendText("Removing card\n");
                    cardReaderMBox.send(new Msg(id, cardReaderMBox, Msg.Type.CR_EjectCard, cardNumField.getText()));
                    cardStatusField.setText("No Card");
                }
                break;
            case "Remove Card":
                if (cardStatusField.getText().compareTo("Card Inserted, Please Input your Pin") == 0) {
                    cardReaderTextArea.appendText("Removing card\n");
                    cardReaderMBox.send(new Msg(id, cardReaderMBox, Msg.Type.CR_CardRemoved, cardNumField.getText()));
                    cardStatusField.setText("No Card");
                }
                break;

            default:
                log.warning(id + ": unknown button: [" + btn.getText() + "]");
                break;
        }
    } // buttonPressed

    /**
     * This method is to change the displayed status on Card Reader Emulator
     * @param status
     */
    public void updateCardStatus(String status) {
        cardStatusField.setText(status);
    } // updateCardStatus


    /**
     * This method is to change the displayed appendTextArea
     * @param status
     */
    public void appendTextArea(String status) {
        cardReaderTextArea.appendText(status + "\n");
    } // appendTextArea

} // CardReaderEmulatorController
