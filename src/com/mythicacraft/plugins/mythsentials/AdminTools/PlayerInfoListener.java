package com.mythicacraft.plugins.mythsentials.AdminTools;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.mythicacraft.plugins.mythsentials.Mythsentials;

public class PlayerInfoListener implements Listener {

	@EventHandler(priority=EventPriority.MONITOR)
	public void invClick(InventoryClickEvent e) {
		if (!(e.getWhoClicked() instanceof Player)) return;
		Player p = (Player) e.getWhoClicked();
		if (Mythsentials.playerInfoMenus.containsKey(p)) {
			PIMenuData data = Mythsentials.playerInfoMenus.get(p);
			if(!data.getInventory().contains(e.getCurrentItem())) return;
			e.setCancelled(true);
			String playerTarget = data.getTarget();
			int slot = e.getSlot();
			if(data.isTargetIsOnline()) {
				if(slot == 10) {
					p.closeInventory();
					p.performCommand("v on");
					p.performCommand("tp " + playerTarget);
				}
				else if(slot == 11) {
					p.closeInventory();
					p.performCommand("tp here " + playerTarget);
				}
				else if(slot == 12) {
					p.closeInventory();
					p.performCommand("inv " + playerTarget);
				}
				else if(slot == 13) {
					p.closeInventory();
					p.performCommand("openender " + playerTarget);
				}
				else if(slot == 14) {
					p.closeInventory();
					p.performCommand("ch mute " + playerTarget);
				}
				else if(slot == 15) {
					p.closeInventory();
					p.performCommand("kick " + playerTarget);
				}
				else if(slot == 16) {
					p.closeInventory();
					if(e.getCurrentItem().getItemMeta().getDisplayName().toLowerCase().contains("unban")) {
						p.performCommand("unban " + playerTarget);
					} else {
						p.performCommand("ban " + playerTarget + " Make an issue to appeal ban");
					}
				}
			} else {
				if(slot == 12) {
					p.closeInventory();
					p.performCommand("inv " + playerTarget);
				}
				else if(slot == 13) {
					p.closeInventory();
					p.performCommand("openender " + playerTarget);
				}
				else if(slot == 14) {
					p.closeInventory();
					if(e.getCurrentItem().getItemMeta().getDisplayName().toLowerCase().contains("unban")) {
						p.performCommand("unban " + playerTarget);
					} else {
						p.performCommand("ban " + playerTarget + " Make an issue to appeal ban");
					}
				}
			}
		}
	}

	@EventHandler(priority=EventPriority.HIGHEST)
	public void invClose(InventoryCloseEvent e) {
		if (!(e.getPlayer() instanceof Player)) return;
		Player p = (Player) e.getPlayer();
		if(Mythsentials.playerInfoMenus.containsKey(p)) {
			Mythsentials.playerInfoMenus.remove(p);
		}
	}

	@EventHandler (priority = EventPriority.MONITOR)
	public void onQuit(PlayerQuitEvent event) {

		if(Mythsentials.playerInfoMenus.containsKey(event.getPlayer())) {
			Mythsentials.playerInfoMenus.remove(event.getPlayer());
		}

	}
}