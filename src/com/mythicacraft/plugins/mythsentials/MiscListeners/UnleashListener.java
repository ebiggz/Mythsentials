package com.mythicacraft.plugins.mythsentials.MiscListeners;

import org.bukkit.Location;
import org.bukkit.entity.LeashHitch;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.PlayerUnleashEntityEvent;

import com.bekvon.bukkit.residence.Residence;
import com.bekvon.bukkit.residence.protection.ClaimedResidence;

public class UnleashListener implements Listener {

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onHitchBreak(HangingBreakByEntityEvent event) {
		if(event.getEntity() instanceof LeashHitch) {
			Player player = null;
			if(event.getRemover() instanceof Player) {
				player = (Player) event.getRemover();
			}
			if(event.getRemover() instanceof Projectile) {
				Projectile p = (Projectile) event.getRemover();
				if(p.getShooter() instanceof Player) {
					player = (Player) p.getShooter();
				}
			}
			if(player != null) {
				Location loc = event.getEntity().getLocation();
				ClaimedResidence res = Residence.getResidenceManager().getByLoc(loc);
				if(res != null) {
					boolean hasPermission = res.getPermissions().playerHas(player.getName(), "build", true);
					if(!hasPermission) {
						if(Residence.getPermissionManager().isResidenceAdmin(player)) return;
						event.setCancelled(true);
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerUnleash(PlayerUnleashEntityEvent event) {
		Player player = event.getPlayer();
		Location loc = event.getEntity().getLocation();
		ClaimedResidence res = Residence.getResidenceManager().getByLoc(loc);
		if(res != null) {
			boolean hasPermission = res.getPermissions().playerHas(player.getName(), "use", true);
			if(!hasPermission) {
				if(Residence.getPermissionManager().isResidenceAdmin(player)) return;
				event.setCancelled(true);
			}
		}
	}
}

