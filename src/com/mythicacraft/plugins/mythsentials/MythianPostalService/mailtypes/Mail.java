package com.mythicacraft.plugins.mythsentials.MythianPostalService.mailtypes;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class Mail implements Comparable<Object>{

	private String from;
	private String to;
	private String message;
	private String timeStamp;
	private MailType type;
	private MailStatus status;

	public Mail(String to, String from, String message, String timeStamp, MailType type, MailStatus status) {
		this.to = to;
		this.from = from;
		this.message = message;
		this.timeStamp = timeStamp;
		this.type = type;
		this.status = status;
	}

	public enum MailType {
		LETTER, PACKAGE, PAYMENT, EXPERIENCE
	}

	public enum MailStatus {
		READ, UNREAD, CLAIMED
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getMessage() {
		return message;
	}

	public String getTimeStamp() {
		return timeStamp;
	}

	public MailType getType() {
		return type;
	}

	public void setType(MailType type) {
		this.type = type;
	}

	public MailStatus getStatus() {
		return status;
	}

	public void setStatus(MailStatus status) {
		this.status = status;
	}

	@Override
	public int compareTo(Object o) {
		DateFormat formatter;
		Date date1 = null;
		Date date2 = null;
		formatter = new SimpleDateFormat("MMM d, yyyy h:mm a", Locale.ENGLISH);
		try {
			date1 = (Date) formatter.parse(timeStamp);
			Mail other = (Mail) o;
			date2 = (Date) formatter.parse(other.getTimeStamp());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		catch(NullPointerException npe){
			System.out.println("Exception thrown "+npe.getMessage()+" date1 is " + date1 + " date2 is "+date2);
		}
		return date2.compareTo(date1);
	}

}
