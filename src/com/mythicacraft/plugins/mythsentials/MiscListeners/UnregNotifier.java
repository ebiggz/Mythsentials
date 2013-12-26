package com.mythicacraft.plugins.mythsentials.MiscListeners;

//import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class UnregNotifier implements Listener {

	@EventHandler (priority = EventPriority.MONITOR)
	public void onInteract(PlayerInteractEvent event) {
		//Player p = event.getPlayer();
	}
}
