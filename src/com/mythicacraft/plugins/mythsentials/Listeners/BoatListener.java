package com.mythicacraft.plugins.mythsentials.Listeners;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.inventory.ItemStack;


public class BoatListener implements Listener
{
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBoatBreak(VehicleDestroyEvent event)
	{
		if(event.getVehicle().getType().equals(EntityType.BOAT))
		{
			if(event.getAttacker() != null)
			{
				if(event.getAttacker().getType() != EntityType.PLAYER)
				{
					event.setCancelled(true);
					Location vLoc = event.getVehicle().getLocation();
					World realm = vLoc.getWorld();
					event.getVehicle().remove();
					realm.dropItem(vLoc, new ItemStack(Material.BOAT));
				}
			}
			else
			{
				event.setCancelled(true);
				Location vLoc = event.getVehicle().getLocation();
				World realm = vLoc.getWorld();
				event.getVehicle().remove();
				realm.dropItem(vLoc, new ItemStack(Material.BOAT));
			}
		}
	}
}
