package com.mythicacraft.plugins.mythsentials.MythianPostalService.exceptions;


public class MailException extends Exception {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Reason reason;

	public MailException(Reason reason) {
		this.reason = reason;
	}

	public enum Reason {
		MAILBOX_ALREADY_EXISTS, MAX_MAILBOXES_REACHED, NO_PERMISSION, NOT_CHEST, MAILBOX_DOESNT_EXIST
	}

	public Reason getReason() {
		return reason;
	}

}
