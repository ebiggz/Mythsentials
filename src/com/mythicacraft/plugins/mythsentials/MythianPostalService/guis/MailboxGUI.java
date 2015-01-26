package com.mythicacraft.plugins.mythsentials.MythianPostalService.guis;

import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.mythicacraft.plugins.mythsentials.GUIAPI.GUI;
import com.mythicacraft.plugins.mythsentials.GUIAPI.GUIManager;
import com.mythicacraft.plugins.mythsentials.GUIAPI.GUIUtils;
import com.mythicacraft.plugins.mythsentials.MythianPostalService.Mailbox;
import com.mythicacraft.plugins.mythsentials.MythianPostalService.MailboxManager;
import com.mythicacraft.plugins.mythsentials.MythianPostalService.MailboxManager.MailboxType;
import com.mythicacraft.plugins.mythsentials.MythianPostalService.mailtypes.Mail;
import com.mythicacraft.plugins.mythsentials.MythianPostalService.mailtypes.Mail.MailStatus;


public class MailboxGUI implements GUI {

	@Override
	public Inventory createInventory(Player player) {

		Mailbox playerMailbox = MailboxManager.getInstance().getPlayerMailbox(player.getName());
		int unread = playerMailbox.getUnreadMailCount();
		int inboxSize = playerMailbox.getBoxFromType(MailboxType.INBOX).size();
		int sentSize = playerMailbox.getBoxFromType(MailboxType.SENT).size();

		Inventory inventory = GUIUtils.generateInventory(9, ChatColor.BLUE + "Mythian Postal Service");

		ItemStack composeBook = GUIUtils.createButton(
				Material.BOOK_AND_QUILL,
				ChatColor.YELLOW + "" + ChatColor.BOLD + "Compose",
				Arrays.asList(
						ChatColor.RED + "Left-Click to " + ChatColor.BOLD + "Compose",
						ChatColor.DARK_RED + "Right-Click to " + ChatColor.BOLD + "Open Drop Box",
						ChatColor.GRAY + "(Drop Box is used for sending packages)"));

		ItemStack inboxChest = GUIUtils.createButton(
				Material.CHEST,
				ChatColor.YELLOW + "" + ChatColor.BOLD + "Inbox",
				Arrays.asList(
						ChatColor.GRAY + "" + unread + " Unread (" + inboxSize + "% Full)",
						ChatColor.RED + "Left-Click to " + ChatColor.BOLD + "Open"));

		ItemStack sentEnderchest = GUIUtils.createButton(
				Material.ENDER_CHEST,
				ChatColor.YELLOW + "" + ChatColor.BOLD + "Sent",
				Arrays.asList(
						ChatColor.GRAY + "" + sentSize + "% Full",
						ChatColor.RED + "Left-Click to " + ChatColor.BOLD + "Open"));

		inventory.setItem(3, composeBook);
		inventory.setItem(4, inboxChest);
		inventory.setItem(5, sentEnderchest);

		return inventory;
	}

	@Override
	public void onInventoryClick(Player whoClicked, InventoryClickEvent clickedEvent) {

		int slot = clickedEvent.getSlot();
		switch(slot) {
			case 3:
				if(clickedEvent.getClick() == ClickType.LEFT) {
					whoClicked.closeInventory();
					whoClicked.performCommand("mail compose");
				}
				else if(clickedEvent.getClick() == ClickType.RIGHT) {
					GUIManager.getInstance().showGUI(new DropboxGUI(), whoClicked);
				}
				break;
			case 4:
				Mailbox mb = MailboxManager.getInstance().getPlayerMailbox(whoClicked.getName());
				for(Mail mail : mb.getBoxFromType(MailboxType.INBOX)) {
					if(mail.getStatus() == MailStatus.UNREAD) {
						mail.setStatus(MailStatus.READ);
					}
				}
				mb.saveBoxType(MailboxType.INBOX);
				GUIManager.getInstance().showGUI(new MailboxTypeGUI(whoClicked.getName(), MailboxType.INBOX, 1), whoClicked);
				break;
			case 5:
				GUIManager.getInstance().showGUI(new MailboxTypeGUI(whoClicked.getName(), MailboxType.SENT, 1), whoClicked);
				//open sent
				break;
		}
		// TODO Auto-generated method stub

	}

	@Override
	public void onInventoryClose(Player whoClosed, InventoryCloseEvent closeEvent) {
		// TODO Auto-generated method stub

	}


}
