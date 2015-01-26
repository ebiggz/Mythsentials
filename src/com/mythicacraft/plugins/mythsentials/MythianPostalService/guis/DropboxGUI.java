package com.mythicacraft.plugins.mythsentials.MythianPostalService.guis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.mythicacraft.plugins.mythsentials.GUIAPI.GUI;
import com.mythicacraft.plugins.mythsentials.GUIAPI.GUIManager;
import com.mythicacraft.plugins.mythsentials.GUIAPI.GUIUtils;
import com.mythicacraft.plugins.mythsentials.MythianPostalService.Mailbox;
import com.mythicacraft.plugins.mythsentials.MythianPostalService.MailboxManager;
import com.mythicacraft.plugins.mythsentials.Utilities.Utils;


public class DropboxGUI implements GUI {

	@Override
	public Inventory createInventory(Player player) {
		Inventory inventory = Bukkit.createInventory(null, 9*5, player.getName() + "'s Drop Box");
		Mailbox mailbox = MailboxManager.getInstance().getPlayerMailbox(player.getName());
		List<ItemStack> dbItems = mailbox.getDropbox();
		for(ItemStack item : dbItems) {
			inventory.addItem(item);
		}
		ItemStack seperator = GUIUtils.createButton(Material.STONE_BUTTON, ChatColor.STRIKETHROUGH + "---", null);
		for(int i = 27; i < 36; i++) {
			inventory.setItem(i, seperator);
		}

		List<String> lore = new ArrayList<String>();
		String[] wrappedMessage = Utils.wrap("You can drag items from your inventory above the dotted line. All times in your drop box are used when you send a package to a player!", 30, "\n", true).split("\n");
		for(String line : wrappedMessage) {
			lore.add(ChatColor.WHITE + line);
		}
		ItemStack infoSign = GUIUtils.createButton(
				Material.SIGN,
				ChatColor.YELLOW +""+ ChatColor.UNDERLINE + "Help",
				lore);
		inventory.setItem(39, infoSign);

		ItemStack mainMenu = GUIUtils.createButton(
				Material.BOOK_AND_QUILL,
				ChatColor.YELLOW +""+ ChatColor.BOLD + "Compose Package",
				Arrays.asList(
						ChatColor.RED + "Left-Click to " + ChatColor.BOLD + "Compose Package",
						ChatColor.RED + "Right-Click to " + ChatColor.BOLD + "Return"));
		inventory.setItem(40, mainMenu);
		// TODO Auto-generated method stub
		return inventory;
	}

	@Override
	public void onInventoryClick(Player whoClicked, InventoryClickEvent clickedEvent) {
		ItemStack clickedItem = clickedEvent.getCurrentItem();
		if(clickedEvent.getSlot() < 27) {
			clickedEvent.setCancelled(false);
		} else {
			if(clickedEvent.getSlot() == 40) {
				if(clickedItem != null && clickedItem.getType() != Material.AIR) {
					GUIManager.getInstance().showGUI(new MailboxGUI(), whoClicked);
				}
			}
		}
	}

	@Override
	public void onInventoryClose(Player whoClosed, InventoryCloseEvent closeEvent) {

		List<ItemStack> items = new ArrayList<ItemStack>();
		for(int i = 0; i < 26; i++) {
			ItemStack item = closeEvent.getInventory().getItem(i);
			if(item != null && item.getType() != Material.AIR) {
				items.add(item);
			}
		}

		MailboxManager.getInstance().getPlayerMailbox(whoClosed.getName()).saveDropbox(items);
		// TODO Auto-generated method stub

	}


}
