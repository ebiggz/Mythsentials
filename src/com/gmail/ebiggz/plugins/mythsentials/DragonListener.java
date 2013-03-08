package com.gmail.ebiggz.plugins.mythsentials;

import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDeathEvent;

public class DragonListener implements Listener{
	
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
	    updateConfig(false, killer);
	}
   @EventHandler(priority = EventPriority.MONITOR)
   public void onEnderDragonSpawn(final CreatureSpawnEvent event) {
       if (!(event.getEntityType() == EntityType.ENDER_DRAGON)) {
    	   return;
       }
       updateConfig(true, null);
    }
    public void updateConfig(Boolean b, String player){
	   if(b.equals(false)) {
		   plugin.getConfig().set("DragonData.dragonIsAlive", false);
		   plugin.getConfig().set("DragonData.dragonLastKilledBy", player);
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
