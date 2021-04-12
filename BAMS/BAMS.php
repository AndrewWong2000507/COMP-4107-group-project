<?php

require_once('./mysql_conn.php');

$req = json_decode($_POST["BAMSReq"], false);

if (strcmp($req->msgType, "LoginReq") === 0) {
  $reqCardNo = $req->cardNo;
  $reqPin = $req->pin;

  //Login
  $sql = "SELECT * FROM Client where CardNo = '$reqCardNo'";
  $result = $conn->query($sql);
  

  if($result->num_rows > 0){
    $row = mysqli_fetch_array($result);
    $printer->Pin = $row['Pin'];
    if($reqPin == $printer->Pin){
        $reply->msgType = "LoginReply";
        $reply->cardNo = $req->cardNo;
        $reply->pin = $req->pin;
        $reply->cred = "cred-1";
    }else{
        $reply->msgType = "LoginReply";
        $reply->cardNo = $req->cardNo;
        $reply->pin = $req->pin;
        $reply->cred = "Wrong Pin";
    }
  }else{
  $reply->msgType = "LoginReply";
  $reply->cardNo = $req->cardNo;
  $reply->pin = $req->pin;
  $reply->cred = "No Such User!!!";
  }
  //End Login

} else if (strcmp($req->msgType, "LogoutReq") === 0) {
  $reply->msgType = "LogoutReply";
  $reply->cardNo = $req->cardNo;
  $reply->cred = $req->cred;
  $reply->result = "succ";
} else if (strcmp($req->msgType, "GetAccReq") === 0) {

    //var from req
    $reqCardNo = $req->cardNo;
    $reqCred = $req->cerd;
    //end req var
    
    //fetch MYSQL
    $sql = "SELECT * FROM `Account` WHERE `CardNo` = '$reqCardNo'";
    $result = $conn->query($sql);
    $row = mysqli_fetch_all($result,MYSQLI_BOTH);

    //build the acctNum String
    $accounts="";
    foreach($row as $data){
        $accounts = $accounts . $data['acctNum'] . " ";
    }
    //reply
    $reply->msgType = "GetAccReply";
    $reply->cardNo = $req->cardNo;
    $reply->cred = $req->cred;
    $reply->accounts = $accounts;

    //End get account req

} else if (strcmp($req->msgType, "WithdrawReq") === 0) {

  //get var from req
  $reqCardNo = $req->cardNo;
  $reqCred = $req->cerd;
  $reqAmount = $req->amount;
  $reqAccNo = $req->accNo;
  //End get var from req
  $sql = "SELECT * FROM `Account` WHERE `acctNum` = '$reqAccNo'";
  $result = $conn->query($sql);
  $row = mysqli_fetch_array($result,MYSQLI_BOTH);
  $balance = $row['credit'];
  
  //conform balance and reqAmount is float
    $balance = floatval($balance);
    $reqAmount = floatval($reqAmount);
  //

  if($balance>$reqAmount){
    $balance = $balance - $reqAmount;
    $sql = "UPDATE `Account` SET `credit`='$balance' WHERE acctNum = '$reqAccNo'";
    if($conn->query($sql)){
        $reply->msgType = "WithdrawReply";
        $reply->cardNo = $req->cardNo;
        $reply->accNo = $req->accNo;
        $reply->cred = $req->cred;
        $reply->amount = $reqAmount;
        $reply->outAmount = $reqAmount;
    }else{
        //Unknown fail query
        $reply->msgType = "WithdrawReply";
        $reply->cardNo = $req->cardNo;
        $reply->accNo = $req->accNo;
        $reply->cred = $req->cred;
        $reply->amount = $reqAmount;
        $reply->outAmount = 0;
    }
  }else{
    //No enough money in account
    $reply->msgType = "WithdrawReply";
    $reply->cardNo = $req->cardNo;
    $reply->accNo = $req->accNo;
    $reply->cred = $req->cred;
    $reply->amount = $reqAmount;
    $reply->outAmount = 0;
  }

  /*
  $reply->msgType = "WithdrawReply";
  $reply->cardNo = $req->cardNo;
  $reply->accNo = $req->accNo;
  $reply->cred = $req->cred;
  $reply->amount = $req->amount;
  $reply->outAmount = $req->amount;
  */
} else if (strcmp($req->msgType, "DepositReq") === 0) {

  //get var from req
  $reqCardNo = $req->cardNo;
  $reqCred = $req->cerd;
  $reqAmount = $req->amount;
  $reqAccNo = $req->accNo;
  //End get var from req
  $sql = "SELECT * FROM `Account` WHERE `acctNum` = '$reqAccNo'";
  $result = $conn->query($sql);
  $row = mysqli_fetch_array($result,MYSQLI_BOTH);
  $balance = $row['credit'];
  
  //conform balance and reqAmount is float
    $balance = floatval($balance);
    $reqAmount = floatval($reqAmount);
  //Deposit the amount
  $balance = $balance + $reqAmount;
  $sql = "UPDATE `Account` SET `credit`='$balance' WHERE acctNum = '$reqAccNo'";

  if($conn->query($sql)){
    $reply->msgType = "DepositReply";
    $reply->cardNo = $req->cardNo;
    $reply->accNo = $req->accNo;
    $reply->cred = $req->cred;
    $reply->amount = $reqAmount;
    $reply->depAmount = $reqAmount;
}else{
    //Unknown fail query
    $reply->msgType = "DepositReply";
    $reply->cardNo = $req->cardNo;
    $reply->accNo = $req->accNo;
    $reply->cred = $req->cred;
    $reply->amount = $reqAmount;
    $reply->depAmount = 0;
}

  /*
  $reply->msgType = "DepositReply";
  $reply->cardNo = $req->cardNo;
  $reply->accNo = $req->accNo;
  $reply->cred = $req->cred;
  $reply->amount = $req->amount;
  $reply->depAmount = $req->amount;
  */
} else if (strcmp($req->msgType, "EnquiryReq") === 0) {

  //reuse the code from above
  $reqCardNo = $req->cardNo;
  $reqCred = $req->cerd;
  $reqAccNo = $req->accNo;

  $sql = "SELECT * FROM `Account` WHERE `acctNum` = '$reqAccNo'";
  $result = $conn->query($sql);
  $row = mysqli_fetch_array($result,MYSQLI_BOTH);
  $balance = $row['credit'];
  $balance = floatval($balance);

  $reply->msgType = "EnquiryReply";
  $reply->cardNo = $req->cardNo;
  $reply->accNo = $req->accNo;
  $reply->cred = $req->cred;
  $reply->amount = $balance;

  /* 
  $reply->msgType = "EnquiryReply";
  $reply->cardNo = $req->cardNo;
  $reply->accNo = $req->accNo;
  $reply->cred = $req->cred;
  $reply->amount = "109700";
  */

} else if (strcmp($req->msgType, "TransferReq") === 0) {
    //get var from req
    $fromAcct = $req->fromAcc;
    $toAcct = $req->toAcc;
    $reqAmount = $req->amount;

    $sql = "SELECT * FROM `Account` WHERE `acctNum` = '$fromAcct'";
    $result = $conn->query($sql);
    $fromRow = mysqli_fetch_array($result,MYSQLI_BOTH);
    $fromBalance = $fromRow['credit'];

    $sql = "SELECT * FROM `Account` WHERE `acctNum` = '$toAcct'";
    $result = $conn->query($sql);
    $toRow = mysqli_fetch_array($result,MYSQLI_BOTH);
    $toBalance = $toRow['credit'];
    
    //conform balance and reqAmount is float
      $toBalance = floatval($toBalance);
      $fromBalance = floatval($fromBalance);
      $reqAmount = floatval($reqAmount);

    if($fromBalance>$reqAmount){
      //reuse withdraw code
      $fromBalance = $fromBalance - $reqAmount;
      $sql = "UPDATE `Account` SET `credit`='$fromBalance' WHERE acctNum = '$fromAcct'";
      if($conn->query($sql)){
        //reuse doposit code
        $toBalance = $toBalance + $reqAmount;
        $sql = "UPDATE `Account` SET `credit`='$toBalance' WHERE acctNum = '$toAcct'";
        if($conn->query($sql)){
          $reply->msgType = "TransferReply";
          $reply->cardNo = $req->cardNo;
          $reply->cred = $req->cred;
          $reply->fromAcc = $req->fromAcc;
          $reply->toAcc = $req->toAcc;
          $reply->amount = $req->amount;
          $reply->transAmount = $reqAmount;
        }else{
          $reply->msgType = "TransferReply";
          $reply->cardNo = $req->cardNo;
          $reply->cred = $req->cred;
          $reply->fromAcc = $req->fromAcc;
          $reply->toAcc = $req->toAcc;
          $reply->amount = $req->amount;
          $reply->transAmount = 0;
        }
      }else{
        $reply->msgType = "TransferReply";
        $reply->cardNo = $req->cardNo;
        $reply->cred = $req->cred;
        $reply->fromAcc = $req->fromAcc;
        $reply->toAcc = $req->toAcc;
        $reply->amount = $req->amount;
        $reply->transAmount = 0;
      }
    }else{
      $reply->msgType = "TransferReply";
      $reply->cardNo = $req->cardNo;
      $reply->cred = $req->cred;
      $reply->fromAcc = $req->fromAcc;
      $reply->toAcc = $req->toAcc;
      $reply->amount = $req->amount;
      $reply->transAmount = 0;
    }

  
  /*
  $reply->msgType = "TransferReply";
  $reply->cardNo = $req->cardNo;
  $reply->cred = $req->cred;
  $reply->fromAcc = $req->fromAcc;
  $reply->toAcc = $req->toAcc;
  $reply->amount = $req->amount;
  $reply->transAmount = $req->amount;
  */
} else if (strcmp($req->msgType, "AccStmtReq") === 0) {
  $reply->msgType = "AccStmtReply";
  $reply->cardNo = $req->cardNo;
  $reply->accNo = $req->accNo;
  $reply->cred = $req->cred;
  $reply->result = "succ";
} else if (strcmp($req->msgType, "ChqBookReq") === 0) {
  $reply->msgType = "ChqBookReply";
  $reply->cardNo = $req->cardNo;
  $reply->accNo = $req->accNo;
  $reply->cred = $req->cred;
  $reply->result = "succ";
} else if (strcmp($req->msgType, "ChgPinReq") === 0) {
  //get var from req
  $oldPin = $req->oldPin;
  $newPin = $req->newPin;
  $reqCardNo = $req->cardNo;

  //reuse login code
  $sql = "SELECT * FROM Client where CardNo = '$reqCardNo'";
  $result = $conn->query($sql);
  $row = mysqli_fetch_array($result);
  $printer->Pin = $row['Pin'];
  if($oldPin == $printer->Pin){
    //update old pin to new pin
    $sql = "UPDATE `Client` SET `Pin`='$newPin' WHERE `CardNo` = '$reqCardNo'";
    if($conn->query($sql)){
      $reply->msgType = "ChgPinReply";
      $reply->cardNo = $req->cardNo;
      $reply->oldPin = $req->oldPin;
      $reply->newPin = $req->newPin;
      $reply->cred = $req->cred;
      $reply->result = "succ";
    }else{
      $reply->msgType = "ChgPinReply";
      $reply->cardNo = $req->cardNo;
      $reply->oldPin = $req->oldPin;
      $reply->newPin = $req->oldPin;
      $reply->cred = $req->cred;
      $reply->result = "Unknown Fail";
    }
  }else{
    $reply->msgType = "ChgPinReply";
    $reply->cardNo = $req->cardNo;
    $reply->oldPin = $req->oldPin;
    $reply->newPin = $req->oldPin;
    $reply->cred = $req->cred;
    $reply->result = "incorrect old pin";
  }


  /*
  $reply->msgType = "ChgPinReply";
  $reply->cardNo = $req->cardNo;
  $reply->oldPin = $req->oldPin;
  $reply->newPin = $req->newPin;
  $reply->cred = $req->cred;
  $reply->result = "succ";
  */
} else if (strcmp($req->msgType, "ChgLangReq") === 0) {
  $reply->msgType = "ChgLangReply";
  $reply->cardNo = $req->cardNo;
  $reply->oldLang = $req->oldLang;
  $reply->newLang = $req->newLang;
  $reply->cred = $req->cred;
  $reply->result = "succ";
}

echo json_encode($reply);
?>