package com.mythicacraft.plugins.mythsentials.Dragon;

import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDeathEvent;

import com.mythicacraft.plugins.mythsentials.Utilities.ConfigAccessor;
import com.mythicacraft.plugins.mythsentials.Utilities.Time;

public class DragonListener implements Listener {

	@EventHandler(priority = EventPriority.MONITOR)
	public void onEDDeath(final EntityDeathEvent event) {
		if (!(event.getEntityType() == EntityType.ENDER_DRAGON)) {
			return;
		}
		String killer = event.getEntity().getKiller().getDisplayName();
		String time = Time.getTime();
		if(event.getEntity().getWorld().getName().equalsIgnoreCase("survival_main_the_end")) {
			updateConfig(killer, time, "survival");
		}
		else if(event.getEntity().getWorld().getName().equalsIgnoreCase("pvp_main_the_end")) {
			updateConfig(killer, time, "pvp");
		}
	}
	@EventHandler(priority = EventPriority.MONITOR)
	public void onEnderDragonSpawn(final CreatureSpawnEvent event) {
		if (event.getEntityType() != EntityType.ENDER_DRAGON) return;
		ConfigAccessor dragonData = new ConfigAccessor("dragon.yml");
		String worldType = null;
		if(event.getEntity().getWorld().getName().equalsIgnoreCase("survival_main_the_end")) {
			worldType = "survival";
		}
		else if(event.getEntity().getWorld().getName().equalsIgnoreCase("pvp_main_the_end")) {
			worldType = "pvp";
		}
		if(worldType != null) {
			dragonData.getConfig().set("DragonData." + worldType + ".dragonIsAlive", true);
			dragonData.saveConfig();
		}
	}
	public void updateConfig(String player, String time, String worldType){
		ConfigAccessor dragonData = new ConfigAccessor("dragon.yml");
		dragonData.getConfig().set("DragonData." + worldType + ".dragonIsAlive", false);
		dragonData.getConfig().set("DragonData." + worldType + ".dragonLastKilledBy", player);
		dragonData.getConfig().set("DragonData." + worldType + ".deathTime", time);
		dragonData.getConfig().set("Kills." + worldType + "." + player, dragonData.getConfig().getInt("Kills." + worldType + "." + player, 0)+1);
		dragonData.saveConfig();
	}
}

