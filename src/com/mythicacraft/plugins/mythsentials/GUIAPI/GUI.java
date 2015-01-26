package com.mythicacraft.plugins.mythsentials.GUIAPI;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;


public interface GUI {

	// Create and return the fake chest inventory that will be the GUI for a player
	public Inventory createInventory(Player player);

	// Do something when a player clicks on something. The event is auto canceled before firing this method.
	// You can un-cancel the event with clickedEvent.setCanceled(false);
	public void onInventoryClick(Player whoClicked, InventoryClickEvent clickedEvent);

	// Do something when a player closes a GUI. It is not possible to cancel this event.
	public void onInventoryClose(Player whoClosed, InventoryCloseEvent closeEvent);

}
