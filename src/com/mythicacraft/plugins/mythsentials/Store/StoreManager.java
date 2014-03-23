package com.mythicacraft.plugins.mythsentials.Store;

import java.util.ArrayList;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.mythicacraft.plugins.mythsentials.Mythian;
import com.mythicacraft.plugins.mythsentials.MythianManager;
import com.mythicacraft.plugins.mythsentials.Mythsentials;
import com.mythicacraft.plugins.mythsentials.Utilities.Utils;


public class StoreManager {

	private static final Logger log = Logger.getLogger("Mythsentials");

	private ArrayList<StoreItem> storeItems = new ArrayList<StoreItem>();
	private MythianManager mm;

	private static Mythsentials plugin;

	public StoreManager(Mythsentials instance) {
		plugin = instance;
		mm = Mythsentials.getMythianManager();
	}

	//item methods
	public void addItem(StoreItem item) {
		storeItems.add(item);
	}

	public void clearItems() {
		storeItems.clear();
	}

	public StoreItem getItem(String itemName) {
		for(StoreItem item : storeItems) {
			if(item.getName().equals(itemName)) {
				return item;
			}
		}
		return null;
	}

	public boolean hasItem(String itemName) {
		if(getItem(itemName) == null) return false;
		return true;
	}

	public void administerItemContents(StoreItem item, String playerName) {

		Player player = Bukkit.getPlayerExact(playerName);
		Mythian mythian = mm.getMythian(playerName);

		if(player == null) {
			mythian.saveUnclaimedItem(playerName, item.getName());
			return;
		}

		String worldName = player.getWorld().getName();

		if(Utils.worldIsBlacklisted(worldName)) {
			mythian.saveUnclaimedItem(player.getName(), item.getName());
			player.sendMessage(ChatColor.RED + "You cannot claim store items in this world!");
			return;
		}
		if(item.hasWorlds()) {
			if(!item.getWorlds().contains(worldName)) {
				mythian.saveUnclaimedItem(player.getName(), item.getName());
				player.sendMessage(ChatColor.RED + "You must claim store item \"" + item.getName()  + "\"  in the world(s): " + Utils.worldsString(item.getWorlds()));
				return;
			}
		}
		if(item.hasItems()) {
			if(item.getRequiredSlots() <= Utils.getPlayerOpenInvSlots(player)) {
				Inventory inv = player.getInventory();
				item.updateLoreAndCustomNames(player.getName());
				ItemStack[] items = item.getItems();
				for(int i = 0; i < items.length; i++) {
					inv.addItem(items[i]);
				}
			} else {
				mythian.saveUnclaimedItem(player.getName(), item.getName());
				player.sendMessage(ChatColor.RED + "You don't have the required space in your inventory for this store item (" + item.getRequiredSlots() + " slots). Please type \"/ms claim\" once you have cleared enough room in your inventory.");
				return;
			}
		}
		if(item.hasCurrency()) {
			Mythsentials.economy.depositPlayer(playerName, item.getCurrency());
		}
		if(item.hasXpLevels()) {
			player.giveExpLevels(item.getXpLevels());
		}
		if(item.hasCommands()) {
			for(String command : item.getCommands()) {
				if(command.startsWith("/")) {
					command = command.replaceFirst("/", "");
				}
				Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), command.replace("%player%", player.getName()));
			}
		}
		if(plugin.MESSAGE_PLAYER) {
			if(item.hasMessage()) {
				player.sendMessage(ChatColor.translateAlternateColorCodes('&', item.getMessage().replace("%player%", player.getName())));
			}
		}
		if(plugin.LOG_TO_CONSOLE) {
			log.info("[Mythsentials] " + player.getName() + " just received the store item: " + item.getName());
		}
	}

}
