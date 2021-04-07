import ATMSS.BAMSHandler.BAMSHandler;
import ATMSS.BAMSHandler.BAMSInvalidReplyException;

import java.io.IOException;
import java.util.Calendar;
import java.util.logging.*;

//======================================================================
// TestBASMHandler
public class TestBAMSHandler {
    //------------------------------------------------------------
    // main
    public static void main(String[] args) {
        String urlPrefix = "http://cslinux0.comp.hkbu.edu.hk/comp4107_20-21_grp12/BAMS.php";
        //String urlPrefix = "http://bams.kwunlam.com/BAMS.php";
        // try logging in
        // BAMSHandler bams = new BAMSHandler(urlPrefix);		// without logger
        BAMSHandler bams = new BAMSHandler(urlPrefix, initLogger());	// with logger

        try {
            testLogin(bams);
            testLogout(bams);
            testGetAcc(bams);
            testWithdraw(bams);
            testDeposit(bams);
            testEnquiry(bams);
            testTransfer(bams);
            //testAccStmtReq(bams);
            //testChqBookReq(bams);
            testChgPinReq(bams);
        } catch (Exception e) {
            System.out.println("TestBAMSHandler: Exception caught: " + e.getMessage());
            e.printStackTrace();
        }
        return;
    } // main


    //------------------------------------------------------------
    // testLogin
    static void testLogin(BAMSHandler bams) throws BAMSInvalidReplyException, IOException {
        System.out.println("Login:");
        String cred = bams.login("0000", "000077");
        System.out.println("cred: " + cred);
        System.out.println();
    } // testLogin


    //------------------------------------------------------------
    // testLogout
    static void testLogout(BAMSHandler bams) throws BAMSInvalidReplyException, IOException {
        System.out.println("Logout:");
        String result = bams.logout("12345678-0", "cred-0");
        System.out.println("result: " + result);
        System.out.println();
    } // testLogout


    //------------------------------------------------------------
    // testGetAcc
    static void testGetAcc(BAMSHandler bams) throws BAMSInvalidReplyException, IOException {
        System.out.println("GetAcc:");
        String accounts = bams.getAccounts("0000", "cred-1");
        System.out.println("accounts: " + accounts);
        System.out.println();
    } // testGetAcc


    //------------------------------------------------------------
    // testWithdraw
    static void testWithdraw(BAMSHandler bams) throws BAMSInvalidReplyException, IOException {
        System.out.println("Withdraw:");
        int outAmount = bams.withdraw("0000", "0000-01","cred-1", "100");
        System.out.println("outAmount: " + outAmount);
        System.out.println();
    } // testWithdraw


    //------------------------------------------------------------
    // testDeposit
    static void testDeposit(BAMSHandler bams) throws BAMSInvalidReplyException, IOException {
        System.out.println("Deposit:");
        double depAmount = bams.deposit("0000", "0000-01","cred-3", "5");
        System.out.println("depAmount: " + depAmount);
        System.out.println();
    } // testDeposit


    //------------------------------------------------------------
    // testEnquiry
    static void testEnquiry(BAMSHandler bams) throws BAMSInvalidReplyException, IOException {
        System.out.println("Enquiry:");
        double amount = bams.enquiry("12345678-4", "0000-01","cred-4");
        System.out.println("amount: " + amount);
        System.out.println();
    } // testEnquiry


    //------------------------------------------------------------
    // testTransfer
    static void testTransfer(BAMSHandler bams) throws BAMSInvalidReplyException, IOException {
        System.out.println("Transfer:");
        double transAmount = bams.transfer("12345678-5", "cred-5","0000-00", "0000-01", "200");
        System.out.println("transAmount: " + transAmount);
        System.out.println();
    } // testTransfer


    //------------------------------------------------------------
    // testAccStmtReq
    static void testAccStmtReq(BAMSHandler bams) throws BAMSInvalidReplyException, IOException {
        System.out.println("AccStmtReq:");
        String result = bams.accStmtReq("12345678-4", "111-222-334","cred-6");
        System.out.println("result: " + result);
        System.out.println();
    } // testAccStmtReq


    //------------------------------------------------------------
    // testChqBookReq
    static void testChqBookReq(BAMSHandler bams) throws BAMSInvalidReplyException, IOException {
        System.out.println("ChqBookReq:");
        String result = bams.chqBookReq("12345678-4", "0000-01","cred-7");
        System.out.println("result: " + result);
        System.out.println();
    } // testChqBookReq


    //------------------------------------------------------------
    // testChgPinReq
    static void testChgPinReq(BAMSHandler bams) throws BAMSInvalidReplyException, IOException {
        System.out.println("ChgPinReq:");
        String result = bams.chgPinReq("0000", "111111", "000000", "cred-8");
        System.out.println("result: " + result);
        System.out.println();
    } // testChgPinReq


    //------------------------------------------------------------
    // initLogger
    static Logger initLogger() {
        // init our logger
        ConsoleHandler logConHdr = new ConsoleHandler();
        logConHdr.setFormatter(new LogFormatter());
        Logger log = Logger.getLogger("TestBAMSHandler");
        log.setUseParentHandlers(false);
        log.setLevel(Level.ALL);
        log.addHandler(logConHdr);
        logConHdr.setLevel(Level.ALL);
        return log;
    } // initLogger


    static class LogFormatter extends Formatter {
        //------------------------------------------------------------
        // format
        public String format(LogRecord rec) {
            Calendar cal = Calendar.getInstance();
            String str = "";

            // get date
            cal.setTimeInMillis(rec.getMillis());
            str += String.format("%02d%02d%02d-%02d:%02d:%02d ",
                    cal.get(Calendar.YEAR) - 2000,
                    cal.get(Calendar.MONTH) + 1,
                    cal.get(Calendar.DAY_OF_MONTH),
                    cal.get(Calendar.HOUR_OF_DAY),
                    cal.get(Calendar.MINUTE),
                    cal.get(Calendar.SECOND));

            // level of the log
            str += "[" + rec.getLevel() + "] -- ";

            // message of the log
            str += rec.getMessage();
            return str + "\n";
        } // format
    } // LogFormatter
} // TestBAMSHandler