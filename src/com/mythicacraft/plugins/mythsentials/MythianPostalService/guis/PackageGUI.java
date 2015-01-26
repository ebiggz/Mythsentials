package com.mythicacraft.plugins.mythsentials.MythianPostalService.guis;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
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
import com.mythicacraft.plugins.mythsentials.MythianPostalService.mailtypes.Mail.MailStatus;
import com.mythicacraft.plugins.mythsentials.MythianPostalService.mailtypes.PackageObj;
import com.mythicacraft.plugins.mythsentials.Utilities.Utils;


public class PackageGUI implements GUI {

	private PackageObj pack;
	private int index;
	private MailboxType previous;
	private int prevPage;

	public PackageGUI(PackageObj pack, int index, MailboxType previous, int prevPage) {
		this.pack = pack;
		this.index = index;
		this.previous = previous;
		this.prevPage = prevPage;
	}

	@Override
	public Inventory createInventory(Player player) {
		Inventory inventory = Bukkit.createInventory(null, 9*5, ChatColor.BLUE + "Package Contents");

		List<ItemStack> dbItems = pack.getItems();
		for(ItemStack item : dbItems) {
			inventory.addItem(item);
		}
		ItemStack seperator = GUIUtils.createButton(Material.STONE_BUTTON, ChatColor.STRIKETHROUGH + "---", null);
		for(int i = 27; i < 36; i++) {
			inventory.setItem(i, seperator);
		}

		if(previous == MailboxType.INBOX) {
			ItemStack claim = GUIUtils.createButton(
					Material.CHEST,
					ChatColor.YELLOW +""+ ChatColor.BOLD + "Take Items",
					Arrays.asList(
							ChatColor.RED + "Left-Click to " + ChatColor.BOLD + "Take Items",
							ChatColor.DARK_RED + "Right-Click to " + ChatColor.BOLD + "Return"));
			inventory.setItem(40, claim);
		} else {
			ItemStack mainMenu = GUIUtils.createButton(
					Material.CHEST,
					ChatColor.YELLOW +""+ ChatColor.BOLD + "Main Menu",
					Arrays.asList(
							ChatColor.RED + "Left-Click to " + ChatColor.BOLD + "Return"));
			inventory.setItem(40, mainMenu);
		}
		// TODO Auto-generated method stub
		return inventory;
	}

	@Override
	public void onInventoryClick(Player whoClicked, InventoryClickEvent clickedEvent) {
		if(clickedEvent.getSlot() == 40) {
			if(previous == MailboxType.INBOX) {
				if(clickedEvent.getClick() == ClickType.LEFT) {
					int openSpots = Utils.getPlayerOpenInvSlots(whoClicked);
					if(pack.getItems().size() > openSpots) {
						whoClicked.closeInventory();
						whoClicked.sendMessage(ChatColor.RED + "[Mythica] There is not enough empty room in your inventory. Please clear space and try again.");
					} else {
						Mailbox mailbox = MailboxManager.getInstance().getPlayerMailbox(whoClicked.getName());
						pack.setStatus(MailStatus.CLAIMED);
						mailbox.updateMailItem(pack, index, previous);
						whoClicked.closeInventory();
						List<ItemStack> drops = pack.getItems();
						for(ItemStack item : drops) {
							whoClicked.getInventory().addItem(item);
						}
						whoClicked.sendMessage(ChatColor.YELLOW + "[Mythica]" + ChatColor.DARK_AQUA +" You have claimed the contents of this package.");
					}
				} else {
					GUIManager.getInstance().showGUI(new MailboxTypeGUI(whoClicked.getName(), MailboxType.INBOX, prevPage), whoClicked);
				}
			} else {
				GUIManager.getInstance().showGUI(new MailboxTypeGUI(whoClicked.getName(), MailboxType.SENT, prevPage), whoClicked);
			}
		}
		// TODO Auto-generated method stub

	}

	@Override
	public void onInventoryClose(Player whoClosed, InventoryCloseEvent closeEvent) {
		// TODO Auto-generated method stub

	}

}
