package com.mythicacraft.plugins.mythsentials.GUIAPI;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;


public class GUIListener implements Listener {

	private GUIManager gm = GUIManager.getInstance();

	//listens for when a player clicks in an inventory
	@EventHandler(priority=EventPriority.MONITOR)
	public void invClick(InventoryClickEvent e) {
		if (!(e.getWhoClicked() instanceof Player)) return;
		Player p = (Player) e.getWhoClicked();
		if(gm.playerHasGUIOpen(p)){
			e.setCancelled(true);
			GUI gui = gm.getPlayersCurrentGUI(p);
			gui.onInventoryClick(p, e.getSlot(), e.getInventory().getItem(e.getSlot()));
		}
	}

	//listens for when a player closes an inventory
	@EventHandler(priority=EventPriority.MONITOR)
	public void invClose(InventoryCloseEvent e) {
		if (!(e.getPlayer() instanceof Player)) return;
		Player p = (Player) e.getPlayer();
		if(gm.playerHasGUIOpen(p)){
			gm.playerClosedGUI(p);
		}
	}
}