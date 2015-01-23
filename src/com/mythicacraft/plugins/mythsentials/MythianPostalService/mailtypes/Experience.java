package com.mythicacraft.plugins.mythsentials.MythianPostalService.mailtypes;


public class Experience extends Mail {

	private int xp;

	public Experience(String to, String from, String message, String timeStamp, MailStatus status, int xp) {
		super(to, from, message, timeStamp, MailType.EXPERIENCE, status);
		this.xp = xp;
	}

	public int getExperience() {
		return xp;
	}
}
