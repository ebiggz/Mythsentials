package com.mythicacraft.plugins.mythsentials.dragon;

import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDeathEvent;

import com.mythicacraft.plugins.mythsentials.Tools.ConfigAccessor;
import com.mythicacraft.plugins.mythsentials.Tools.Time;

public class DragonListener implements Listener {

	@EventHandler(priority = EventPriority.MONITOR)
	public void onEDDeath(final EntityDeathEvent event) {
		if (!(event.getEntityType() == EntityType.ENDER_DRAGON)) {
			return;
		}
		String killer = event.getEntity().getKiller().getDisplayName();
		String time = Time.getTime();
		updateConfig(false, killer, time);
	}
	@EventHandler(priority = EventPriority.MONITOR)
	public void onEnderDragonSpawn(final CreatureSpawnEvent event) {
		if (event.getEntityType() != EntityType.ENDER_DRAGON) return;
		updateConfig(true, null, null);
	}
	public void updateConfig(Boolean b, String player, String time){
		ConfigAccessor dragonData = new ConfigAccessor("dragon.yml");
		if(b.equals(false)) {
			dragonData.getConfig().set("DragonData.dragonIsAlive", false);
			dragonData.getConfig().set("DragonData.dragonLastKilledBy", player);
			dragonData.getConfig().set("DragonData.deathTime", time);
			dragonData.saveConfig();
			return;
		}
		dragonData.getConfig().set("DragonData.dragonIsAlive", true);
		dragonData.saveConfig();
	}
}

