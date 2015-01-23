package com.mythicacraft.plugins.mythsentials.MythianPostalService;

import org.bukkit.Location;

import com.mythicacraft.plugins.mythsentials.MythianPostalService.exceptions.MailException;
import com.mythicacraft.plugins.mythsentials.MythianPostalService.exceptions.MailException.Reason;



public class MailboxManager {

	protected MailboxManager() { /*exists to block instantiation*/ }
	private static MailboxManager instance = null;
	public static MailboxManager getInstance() {
		if(instance == null) {
			instance = new MailboxManager();
		}
		return instance;
	}


	public Mailbox getPlayerMailbox(String player) {
		return new Mailbox(player);
	}

	public boolean locationHasMailbox(Location location) {
		return true;
	}

	public void addMailboxAtLoc(Location location, String owner) throws MailException {
		if(this.locationHasMailbox(location)) {
			throw new MailException(Reason.MAILBOX_ALREADY_EXISTS);
		}
	}

	public enum MailboxType {
		INBOX, SENT
	}


	//getMailboxOwner(Location location)


}
