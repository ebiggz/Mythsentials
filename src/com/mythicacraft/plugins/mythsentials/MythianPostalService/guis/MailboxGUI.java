package com.mythicacraft.plugins.mythsentials.MythianPostalService.guis;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import com.mythicacraft.plugins.mythsentials.Mythian;
import com.mythicacraft.plugins.mythsentials.Mythsentials;
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
		Mythian mythian = Mythsentials.getMythianManager().getMythian(player.getName());
		int unread = playerMailbox.getUnreadMailCount();
		int inboxSize = playerMailbox.getBoxFromType(MailboxType.INBOX).size();
		int sentSize = playerMailbox.getBoxFromType(MailboxType.SENT).size();

		//Inventory inventory = GUIUtils.generateInventory(9, ChatColor.BLUE + "Mythian Postal Service");
		Inventory inventory = Bukkit.createInventory(null, InventoryType.HOPPER, ChatColor.BLUE + "Mythian Postal Service");


		ItemStack infoSign = GUIUtils.createButton(
				Material.SIGN,
				ChatColor.YELLOW + "" + ChatColor.BOLD + "Account Info",
				Arrays.asList(
						ChatColor.GRAY + "Mailboxes: " + ChatColor.WHITE + mythian.getMailboxLocs().size() + " of " + MailboxManager.getInstance().getMaxMailboxCount(player.getName()),
						ChatColor.GRAY + "Inbox/Sent Size: " + ChatColor.WHITE + 100,
						ChatColor.RED + "Left-Click for " + ChatColor.BOLD + "Help"));

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
						ChatColor.GRAY + "[" + inboxSize + "% full]",
						ChatColor.WHITE + "" + unread + " Unread",
						ChatColor.RED + "Left-Click to " + ChatColor.BOLD + "Open"));

		ItemStack sentEnderchest = GUIUtils.createButton(
				Material.ENDER_CHEST,
				ChatColor.YELLOW + "" + ChatColor.BOLD + "Sent",
				Arrays.asList(
						ChatColor.GRAY + "[" + sentSize + "% full]",
						ChatColor.RED + "Left-Click to " + ChatColor.BOLD + "Open"));

		ItemStack tradingPost = GUIUtils.createButton(
				Material.FENCE,
				ChatColor.YELLOW + "" + ChatColor.BOLD + "Trading Post",
				Arrays.asList(
						ChatColor.GRAY + "*Coming Soon*"));

		inventory.setItem(0, infoSign);
		inventory.setItem(1, composeBook);
		inventory.setItem(2, inboxChest);
		inventory.setItem(3, sentEnderchest);
		inventory.setItem(4, tradingPost);

		return inventory;
	}

	@Override
	public void onInventoryClick(Player whoClicked, InventoryClickEvent clickedEvent) {

		int slot = clickedEvent.getSlot();
		switch(slot) {
			case 0:
				if(clickedEvent.getClick() == ClickType.LEFT) {
					whoClicked.closeInventory();
					whoClicked.performCommand("mail help");
				}
				break;
			case 1:
				if(clickedEvent.getClick() == ClickType.LEFT) {
					if(clickedEvent.getCursor() != null) {
						if(clickedEvent.getCursor().getType() == Material.BOOK_AND_QUILL) {
							BookMeta bm = (BookMeta) clickedEvent.getCursor().getItemMeta();
							if(bm.hasPages()) {
								String pageData = bm.getPage(1).replaceAll(System.getProperty("line.separator"), "");
								String regex = "[tT][oO]:(\\s)?(\\w+)\\b|$";
								Matcher matcher = Pattern.compile(regex).matcher(pageData);
								if(matcher.find()) {
									String[] split = matcher.group().split(":");
									String to = split[1].trim();
									String message = pageData.replace(matcher.group(), "");
									whoClicked.performCommand("mail letter to:" + to + " message:" + message.trim());
									MailboxManager.getInstance().willDropBook.remove(whoClicked);
									MailboxManager.getInstance().willDropBook.add(whoClicked);
									whoClicked.closeInventory();
									break;
								}
							}
						}
					}
					whoClicked.closeInventory();
					whoClicked.performCommand("mail compose");
				}
				else if(clickedEvent.getClick() == ClickType.RIGHT) {
					GUIManager.getInstance().showGUI(new DropboxGUI(), whoClicked);
				}
				break;
			case 2:
				Mailbox mb = MailboxManager.getInstance().getPlayerMailbox(whoClicked.getName());
				for(Mail mail : mb.getBoxFromType(MailboxType.INBOX)) {
					if(mail.getStatus() == MailStatus.UNREAD) {
						mail.setStatus(MailStatus.READ);
					}
				}
				mb.saveBoxType(MailboxType.INBOX);
				GUIManager.getInstance().showGUI(new MailboxTypeGUI(whoClicked.getName(), MailboxType.INBOX, 1), whoClicked);
				break;
			case 3:
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

	@Override
	public boolean ignoreForeignItems() {
		// TODO Auto-generated method stub
		return false;
	}


}
