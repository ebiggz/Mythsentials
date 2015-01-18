package com.mythicacraft.plugins.mythsentials.GUIAPI;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;


public interface GUI {

	// create the fake chest inventory for player
	public Inventory createInventory(Player player);

	// handle when a player clicks on something
	public void onInventoryClick(Player whoClicked, int clickedSlot, ItemStack clickedItem);

}
