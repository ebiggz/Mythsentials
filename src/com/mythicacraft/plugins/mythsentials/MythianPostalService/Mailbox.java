package com.mythicacraft.plugins.mythsentials.MythianPostalService;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

import com.mythicacraft.plugins.mythsentials.Mythian;
import com.mythicacraft.plugins.mythsentials.Mythsentials;
import com.mythicacraft.plugins.mythsentials.MythianPostalService.MailboxManager.MailboxType;
import com.mythicacraft.plugins.mythsentials.MythianPostalService.mailtypes.Mail;
import com.mythicacraft.plugins.mythsentials.MythianPostalService.mailtypes.Mail.MailStatus;
import com.mythicacraft.plugins.mythsentials.Utilities.Utils;


public class Mailbox {

	private String owner;
	private List<Mail> inbox;
	private List<Mail> sent;
	private List<ItemStack> dropbox;

	public Mailbox(String owner) {
		this.owner = owner;
		Mythian mythian = Mythsentials.getMythianManager().getMythian(owner);
		inbox = mythian.loadPlayerMailboxType(MailboxType.INBOX);
		sent = mythian.loadPlayerMailboxType(MailboxType.SENT);
		dropbox = mythian.getMailDropbox();
	}

	public String getOwner() {
		return owner;
	}

	public List<Mail> getInbox() {
		return inbox;
	}

	public List<Mail> getOutbox() {
		return sent;
	}

	public List<ItemStack> getDropbox() {
		return dropbox;
	}

	public void saveDropbox(List<ItemStack> items) {
		Mythian mythian = Mythsentials.getMythianManager().getMythian(owner);
		mythian.saveMailDropbox(items);
	}

	public void clearDropbox() {
		List<ItemStack> empty = new ArrayList<ItemStack>();
		saveDropbox(empty);
	}

	public int getUnreadMailCount() {
		int unreadCount = 0;
		for(Mail mail : inbox) {
			if(mail.getStatus() == MailStatus.UNREAD) unreadCount++;
		}
		return unreadCount;
	}

	public void sendMail(String to, Mail mail) {
		Mythian mythian = Mythsentials.getMythianManager().getMythian(owner);
		MailboxManager.getInstance().getPlayerMailbox(to).receiveMail(mail);
		sent.add(mail);
		mythian.savePlayerMailboxType(sent, MailboxType.SENT);
	}

	public void receiveMail(Mail mail) {
		Mythian mythian = Mythsentials.getMythianManager().getMythian(owner);
		inbox.add(mail);
		mythian.savePlayerMailboxType(inbox, MailboxType.INBOX);
		Utils.messagePlayerIfOnline(owner, ChatColor.YELLOW + "[Mythica] " + ChatColor.GOLD + "You just recieved mail! Visit your nearest mailbox to read it.");
	}
}
