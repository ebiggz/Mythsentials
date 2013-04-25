package com.gmail.ebiggz.plugins.mythsentials.Listeners;

import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDeathEvent;

import com.gmail.ebiggz.plugins.mythsentials.Mythsentials;
import com.gmail.ebiggz.plugins.mythsentials.Tools.Time;

public class DragonListener implements Listener {
	
	private final Mythsentials plugin;

	public DragonListener(final Mythsentials instance) {
	    this.plugin = instance;
	}

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
       if (!(event.getEntityType() == EntityType.ENDER_DRAGON)) {
    	   return;
       }
       updateConfig(true, null, null);
    }
    public void updateConfig(Boolean b, String player, String time){
	   if(b.equals(false)) {
		   plugin.getConfig().set("DragonData.dragonIsAlive", false);
		   plugin.getConfig().set("DragonData.dragonLastKilledBy", player);
		   plugin.getConfig().set("DragonData.deathTime", time);
		   plugin.saveConfig();
		   return;
	   }
	   if(b.equals(true)) {
		   plugin.getConfig().set("DragonData.dragonIsAlive", true);
		   plugin.saveConfig();
		   return;
	   }
   }
}
