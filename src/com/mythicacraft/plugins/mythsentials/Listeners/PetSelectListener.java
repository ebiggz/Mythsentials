package com.mythicacraft.plugins.mythsentials.Listeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import com.mythicacraft.plugins.mythsentials.Mythsentials;
import com.mythicacraft.plugins.mythsentials.CmdExecutors.CmdProcessor;


public class PetSelectListener implements Listener {
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPetSelect(EntityDamageByEntityEvent e) {
		if(!(e.getDamager() instanceof Player)) return;
		Player player = (Player) e.getDamager();
		if(!(e.getEntity() instanceof Ocelot || e.getEntity() instanceof Wolf)) return;
		Tameable pet = (Tameable) e.getEntity();
		if(Mythsentials.petSelector.containsKey(player)) {
			if(!pet.isTamed()) {
				player.sendMessage(ChatColor.RED + "This animal isn't tamed yet! Pet selection canceled.");
			} else {
				CmdProcessor.processCommand(player, Mythsentials.petSelector.get(player), pet);
			}
			Mythsentials.petSelector.remove(player);
			e.setCancelled(true);
			return;
		}
	}
}
