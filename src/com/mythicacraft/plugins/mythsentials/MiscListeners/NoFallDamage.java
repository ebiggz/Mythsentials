package com.mythicacraft.plugins.mythsentials.MiscListeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class NoFallDamage implements Listener {

	@EventHandler (priority = EventPriority.HIGH)
	public void onEntityDamage(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player && event.getCause() == DamageCause.FALL) {
			if(event.getEntity().getWorld().getName().startsWith("survival") || event.getEntity().getWorld().getName().startsWith("The_Realm")) {
				event.setCancelled(true);
			}
		}
	}
}