package ATMSS.CardReaderHandler.Emulator;

import ATMSS.ATMSSStarter;
import ATMSS.CardReaderHandler.CardReaderHandler;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;


/**
 * This a Card Reader Emulator that emulate the Card Reader on a physical ATM
 */
public class CardReaderEmulator extends CardReaderHandler {
    private ATMSSStarter atmssStarter;
    private String id;
    private Stage myStage;
    private CardReaderEmulatorController cardReaderEmulatorController;

    //------------------------------------------------------------
    // CardReaderEmulator
    public CardReaderEmulator(String id, ATMSSStarter atmssStarter) {
	super(id, atmssStarter);
	this.atmssStarter = atmssStarter;
	this.id = id;
    } // CardReaderEmulator


    //------------------------------------------------------------
    // start

	/**
	 * This method will link the stage with FXML file and controller and start showing the stage
	 * @throws Exception exist on loading the FXML file
	 */
    public void start() throws Exception {
	Parent root;
	myStage = new Stage();
	FXMLLoader loader = new FXMLLoader();
	String fxmlName = "CardReaderEmulator.fxml";
	loader.setLocation(CardReaderEmulator.class.getResource(fxmlName));
	root = loader.load();
	cardReaderEmulatorController = (CardReaderEmulatorController) loader.getController();
	cardReaderEmulatorController.initialize(id, atmssStarter, log, this);
	myStage.initStyle(StageStyle.DECORATED);
	myStage.setScene(new Scene(root, 350, 470));
	myStage.setTitle("Card Reader");
	myStage.setResizable(false);
	myStage.setOnCloseRequest((WindowEvent event) -> {
	    atmssStarter.stopApp();
	    Platform.exit();
	});
	myStage.show();
    } // CardReaderEmulator


	/**
	 * This is method handle the Card Insertion
	 */
    protected void handleCardInsert() {
        // fixme
	super.handleCardInsert();
	cardReaderEmulatorController.appendTextArea("Card Inserted");
	cardReaderEmulatorController.updateCardStatus("Card Inserted");
    } // handleCardInsert


	/**
	 * This is method handle Card ejection
	 */
    protected void handleCardEject() {
        // fixme
	super.handleCardEject();
	cardReaderEmulatorController.appendTextArea("Card Ejected");
	cardReaderEmulatorController.updateCardStatus("Card Ejected");
    } // handleCardEject


	/**
	 * This is method handle Card Removal
	 */
    protected void handleCardRemove() {
	// fixme
	super.handleCardRemove();
	cardReaderEmulatorController.appendTextArea("Card Removed");
	cardReaderEmulatorController.updateCardStatus("Card Reader Empty");
    } // handleCardRemove
} // CardReaderEmulator
