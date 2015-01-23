package com.mythicacraft.plugins.mythsentials.MythianPostalService.mailtypes;



public class Payment extends Mail {

	private double payment;

	public Payment(String to, String from, String message, String timeStamp, MailStatus status, double payment) {
		super(to, from, message, timeStamp, MailType.PAYMENT, status);
		this.payment = payment;
	}

	public double getPayment() {
		return payment;
	}

}
