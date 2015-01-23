package com.mythicacraft.plugins.mythsentials.GUIAPI;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;


public interface GUI {

	// create the fake chest inventory for player
	public Inventory createInventory(Player player);

	// handle when a player clicks on something
	public void onInventoryClick(Player whoClicked, InventoryClickEvent clickedEvent);

	//whether or not the API should auto cancel click events
	public boolean shouldAutoCancel();

}
