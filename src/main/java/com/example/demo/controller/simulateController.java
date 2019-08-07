package com.example.demo.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dao.AccountRepo;
import com.example.demo.dao.CardRepo;
import com.example.demo.dao.TransactionsRepo;
import com.example.demo.dao.UserRepo;
import com.example.demo.model.MyModel;
import com.example.demo.model.Response;
import com.example.demo.model.Transactions;
import com.example.demo.model.account;
import com.example.demo.model.card;


@RestController
public class simulateController {
	
	@Autowired
	CardRepo cardRepo;
	
	@Autowired
	AccountRepo accountRepo;
	
	@Autowired
	UserRepo userRepo;
	
	@Autowired
	TransactionsRepo transactionsRepo;
	
	@PostMapping(path= "/paymentSimulator")
	public String hit(@RequestBody MyModel model) {
		
		Response res = new Response();
		
		System.out.println("this is in bank simulator");
		String merchantId=model.getMerchantId();
		String cardNo=model.getCardNo();
		String expiryDate=model.getExpiryDate();
		String cvv=model.getCvv();
		String pin=model.getPin();
		String bill=model.getBill();
		/*
		 * LocalDate localDate = LocalDate.now(); String local =
		 * DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss").format(localDate).toString
		 * ();
		 */
		
		DateTimeFormatter FOMATTER = DateTimeFormatter.ofPattern("MM/dd/yyyy 'at' hh:mm a");
		LocalDateTime localDateTime = LocalDateTime.now();
		String ldtString = FOMATTER.format(localDateTime);
		
			card userCardObj = cardRepo.validate(cardNo, expiryDate, cvv, pin);
			account merchantAccountObj=accountRepo.findByUserId(merchantId);
			
			

			if (userCardObj == null)
			{
				res.setResponseCode("100");
				res.setResponseMessage("Card details does not match");
				
				return res.getResponseCode()+"_"+res.getResponseMessage();
			}
			
			String userAccountNo=userCardObj.getAccount().getAccountNo();
			String merchantAccountNo = merchantAccountObj.getAccountNo();
			
			String userAccountBalance=userCardObj.getAccount().getAccountBalance();
			String merchantAccountBalance=merchantAccountObj.getAccountBalance();
			
			Transactions t = new Transactions();
			t.setSenderAccountNo(userAccountNo);
			t.setReceiverAccountNo(merchantAccountNo);
			t.setPayment(bill);
			t.setDate(ldtString);
			t.setSenderName(userCardObj.getHolderName());
			
			int userBal=Integer.parseInt(userAccountBalance);
			int merchantBal=Integer.parseInt(merchantAccountBalance);
			int userBill=Integer.parseInt(bill);
			
			if(userBal>userBill) {
			  userBal=userBal-userBill; 
			}else {
				t.setStatus("failed");
				transactionsRepo.save(t);
				res.setResponseCode("300");
				res.setResponseMessage("Insufficient Funds");
				return res.getResponseCode()+"_"+res.getResponseMessage()+"_"+t.getSenderName()+"_"+t.getTransactionId()+"_"+t.getSenderAccountNo()+"_"+t.getPayment()+"_"+t.getDate();	
			}
			merchantBal=merchantBal+userBill;
			
			String finalUserBalance=userBal+"";
			String finalMerchantBalance=merchantBal+"";
			
			userCardObj.getAccount().setAccountBalance(finalUserBalance);
			merchantAccountObj.setAccountBalance(finalMerchantBalance);
			
			accountRepo.save(userCardObj.getAccount());
			accountRepo.save(merchantAccountObj);
			
			res.setResponseCode("200");
			res.setResponseMessage("Payment successful");
			
			t.setStatus("success");
			transactionsRepo.save(t);
			return res.getResponseCode()+"_"+res.getResponseMessage()+"_"+t.getSenderName()+"_"+t.getTransactionId()+"_"+t.getSenderAccountNo()+"_"+t.getPayment()+"_"+t.getDate();			
		
	}
	
}
