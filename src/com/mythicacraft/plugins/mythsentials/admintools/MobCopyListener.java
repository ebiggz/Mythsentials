package com.mythicacraft.plugins.mythsentials.AdminTools;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import com.mythicacraft.plugins.mythsentials.Mythsentials;


public class MobCopyListener implements Listener {

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onMobSelect(EntityDamageByEntityEvent e) {
		if(!(e.getDamager() instanceof Player)) return;
		if(e.getEntity() instanceof Player) return;
		Player player = (Player) e.getDamager();
		if(Mythsentials.mobCopy.contains(player)) {
			Mythsentials.mobCopy.remove(player);
			Mythsentials.copiedMob.put(player, e.getEntity());
			player.sendMessage(ChatColor.AQUA + "You have copied a " + e.getEntity().getType().toString().toLowerCase().replace("_", " ") + ", sah. You can now type /mobpaste");
		}
	}

}
