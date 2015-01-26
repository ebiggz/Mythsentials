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

	Mailbox(String owner) {
		this.owner = owner;
		Mythian mythian = Mythsentials.getMythianManager().getMythian(owner);
		inbox = mythian.loadPlayerMailboxType(MailboxType.INBOX);
		sent = mythian.loadPlayerMailboxType(MailboxType.SENT);
		dropbox = mythian.getMailDropbox();
	}

	public String getOwner() {
		return owner;
	}

	public List<Mail> getBoxFromType(MailboxType type) {
		if(type == MailboxType.INBOX) {
			return inbox;
		} else {
			return sent;
		}
	}

	public void updateMailItem(Mail mail, int index, MailboxType type) {
		reloadBoxType(type);
		if(type == MailboxType.INBOX) {
			inbox.set(index, mail);
		} else {
			sent.set(index, mail);
		}
		saveBoxType(type);
	}

	public void deleteMailItem(int index, MailboxType type) {
		reloadBoxType(type);
		if(type == MailboxType.INBOX) {
			inbox.remove(index);
		} else {
			sent.remove(index);
		}
		saveBoxType(type);
	}

	private void reloadBoxType(MailboxType type) {
		Mythian mythian = Mythsentials.getMythianManager().getMythian(owner);
		if(type == MailboxType.INBOX) {
			inbox = mythian.loadPlayerMailboxType(MailboxType.INBOX);
		} else {
			sent = mythian.loadPlayerMailboxType(MailboxType.SENT);
		}
	}

	public void saveBoxType(MailboxType type) {
		Mythian mythian = Mythsentials.getMythianManager().getMythian(owner);
		if(type == MailboxType.INBOX) {
			mythian.savePlayerMailboxType(inbox, MailboxType.INBOX);
		} else {
			mythian.savePlayerMailboxType(sent, MailboxType.SENT);
		}
	}

	public List<ItemStack> getDropbox() {
		return dropbox;
	}

	public void saveDropbox(List<ItemStack> items) {
		Mythian mythian = Mythsentials.getMythianManager().getMythian(owner);
		mythian.saveMailDropbox(items);
	}

	public void clearDropbox() {
		saveDropbox(new ArrayList<ItemStack>());
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
		Utils.messagePlayerIfOnline(owner, ChatColor.YELLOW + "[Mythica] " + ChatColor.DARK_AQUA + "You just recieved mail from " + ChatColor.AQUA + mail.getFrom() + ChatColor.DARK_AQUA + "! Visit your nearest mailbox to read it.");
	}
}
