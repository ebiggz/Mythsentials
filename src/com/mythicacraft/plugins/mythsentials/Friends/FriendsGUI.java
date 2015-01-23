package com.mythicacraft.plugins.mythsentials.Friends;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import com.mythicacraft.plugins.mythsentials.Mythian;
import com.mythicacraft.plugins.mythsentials.Mythsentials;
import com.mythicacraft.plugins.mythsentials.GUIAPI.GUI;


public class FriendsGUI implements GUI {

	public Inventory createInventory(Player player) {

		String playerName = player.getName();
		Mythian mythian = Mythsentials.getMythianManager().getMythian(playerName);

		/*Calculate the required slots in the inventory
		 * each row in the inventory has 9 slots and therefor
		 * this number must be a multiple of 9.
		 */
		List<String> friends = mythian.getFriendList();
		int multOf9 = 9;
		int req = friends.size()+1;
		while(req > multOf9) {
			multOf9 += 9;
		}

		/*create the Inventory object.
		 * (1st Arg) Inventory owner - not needed, leave null
		 * (2nd Arg) Amount of slots
		 * (3nd Arg) Name Of Inventory (Shows up at top)
		 */
		Inventory inventory = Bukkit.createInventory(null, multOf9, ChatColor.YELLOW + "Friends List");

		/*Create ItemStacks to put in the Inventory we just created*/

		// Split apart offline/online friends and alphabetize them
		List<String> onlinePlayers = new ArrayList<String>();
		List<String> offlinePlayers = new ArrayList<String>();
		for(String friendName : friends) {
			Player friendPlayer = Bukkit.getPlayer(friendName);
			if(friendPlayer != null && friendPlayer.isOnline()) {
				onlinePlayers.add(friendName);
			} else {
				offlinePlayers.add(friendName);
			}
		}
		Collections.sort(onlinePlayers, String.CASE_INSENSITIVE_ORDER);
		Collections.sort(offlinePlayers, String.CASE_INSENSITIVE_ORDER);

		// a Sign item that contains general information about friends
		ItemStack infoSign = new ItemStack(Material.SIGN); //create itemstack with a Material type.
		ItemMeta im = infoSign.getItemMeta(); //Item meta contains options for display name and lore text
		im.setDisplayName(ChatColor.YELLOW + "" + ChatColor.UNDERLINE + "Details");
		im.setLore(Arrays.asList(ChatColor.GOLD + "" + onlinePlayers.size() + "/" + friends.size() + " Online", "*Click for command help*"));
		infoSign.setItemMeta(im); // apply item meta to item stack
		inventory.addItem(infoSign); // Add item to inventory

		// Generate player heads for each friend
		ItemStack playerHead = new ItemStack(Material.SKULL_ITEM, 1, (short) 3); //base head item for cloning
		for(String friendName : onlinePlayers) {

			ItemStack friendHead = playerHead.clone();
			SkullMeta sim = (SkullMeta) friendHead.getItemMeta();
			sim.setOwner(friendName);

			List<String> lore = new ArrayList<String>();
			Player friendPlayer = Bukkit.getPlayer(friendName);

			if(friendPlayer != null && friendPlayer.isOnline()) {
				sim.setDisplayName(ChatColor.GREEN + friendName);
				lore.add(ChatColor.GRAY + "Online");
				String gameType = Mythsentials.getGameTypeForWorld(friendPlayer.getWorld().getName());
				if(gameType != null) {
					lore.add(ChatColor.YELLOW + "Currently playing in " + gameType);
				} else {
					lore.add(ChatColor.YELLOW + "Currently playing in " + friendPlayer.getWorld().getName());
				}
				lore.add("*Click to private chat*");
			} else {
				sim.setDisplayName(ChatColor.RED + friendName);
				lore.add(ChatColor.GRAY + "Offline");
			}

			sim.setLore(lore);
			friendHead.setItemMeta(sim);
			inventory.addItem(friendHead);
		}

		for(String friendName : offlinePlayers) {
			ItemStack friendHead = playerHead.clone();
			SkullMeta sim = (SkullMeta) friendHead.getItemMeta();
			sim.setOwner(friendName);
			List<String> lore = new ArrayList<String>();
			sim.setDisplayName(ChatColor.RED + friendName);
			lore.add(ChatColor.GRAY + "Offline");
			sim.setLore(lore);
			friendHead.setItemMeta(sim);
			inventory.addItem(friendHead);
		}
		return inventory;

	}

	public void onInventoryClick(Player whoClicked, InventoryClickEvent clickEvent) {
		int clickedSlot = clickEvent.getSlot();
		ItemStack clickedItem = clickEvent.getInventory().getItem(clickedSlot);
		//check if clicked item is a player head
		if(clickedItem != null && clickedItem.getType() == Material.SKULL_ITEM) {
			String friendName = ChatColor.stripColor(clickedItem.getItemMeta().getDisplayName());
			Player friendPlayer = Bukkit.getPlayer(friendName);
			if(friendPlayer == null || !friendPlayer.isOnline()) return;
			whoClicked.performCommand("msg " + friendName);
			whoClicked.sendMessage(ChatColor.YELLOW + "Type " + ChatColor.WHITE + "/ch g" + ChatColor.YELLOW + " to return to Global Chat when done.");
			whoClicked.closeInventory();
		}
		else if(clickedItem != null && clickedItem.getType() == Material.SIGN) {
			whoClicked.performCommand("friends help");
			whoClicked.closeInventory();
		}
	}

	@Override
	public boolean shouldAutoCancel() {
		return true;
	}
}
