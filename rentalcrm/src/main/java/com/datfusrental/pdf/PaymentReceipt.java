package com.datfusrental.pdf;

import org.springframework.beans.factory.annotation.Autowired;

import com.datfusrental.entities.LeadDetails;

public class PaymentReceipt {

	@Autowired
//	private AmountToWordsConverter amountToWordsConverter;

    public byte[] generateInvoice(LeadDetails leadDetails) {
		return null;
	}
}
