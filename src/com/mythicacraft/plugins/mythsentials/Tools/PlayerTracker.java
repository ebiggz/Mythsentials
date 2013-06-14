package com.mythicacraft.plugins.mythsentials.Tools;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.kitteh.vanish.staticaccess.VanishNoPacket;
import org.kitteh.vanish.staticaccess.VanishNotLoadedException;

import com.mythicacraft.plugins.mythsentials.Mythsentials;


public class PlayerTracker extends BukkitRunnable {

	private Player user;
	private Player target;
	public Mythsentials plugin;
	World realm = Bukkit.getWorld("The_Realm");
	Location spawn = new Location(realm, -37, 133, -5);

	public PlayerTracker(Mythsentials plugin, Player user, Player target) {
		this.plugin = plugin;
		this.setUser(user);
		this.setTarget(target);
	}
	public void run() {
		if(user == null || !user.isOnline()) {
			Bukkit.getScheduler().cancelTask(this.getTaskId());
			return;
		}
		if(target != null && target.isOnline()) {
			try {
				if(VanishNoPacket.isVanished(target.getDisplayName())) {
					if(!user.hasPermission("mythica.mod")) {
						user.sendMessage(ChatColor.RED + "Player target no longer available! Resetting compass to spawn.");
						cancelAndReset();
						return;
					}
				}
			} catch (VanishNotLoadedException e) {
				e.printStackTrace();
			}
			if(target.getWorld() != user.getWorld()) {
				user.sendMessage(ChatColor.RED + "Player target has moved to a different world! Resetting compass to spawn.");
				cancelAndReset();
				return;
			}
			if(user.getWorld().getEnvironment() == Environment.NETHER) {
				user.sendMessage(ChatColor.RED + "Compasses do not work in the nether! Resetting compass.");
				cancelAndReset();
				return;
			}
			user.setCompassTarget(target.getLocation());
		} else {
			user.sendMessage(ChatColor.RED + "Player target no longer available! Resetting compass to spawn.");
			cancelAndReset();
		}
	}
	private void cancelAndReset() {
		plugin.playerTrackers.remove(user);
		Bukkit.getScheduler().cancelTask(this.getTaskId());
		user.setCompassTarget(spawn);
	}
	public Player getUser() {
		return user;
	}
	public void setUser(Player user) {
		this.user = user;
	}
	public Player getTarget() {
		return target;
	}
	public void setTarget(Player target) {
		this.target = target;
	}
}
