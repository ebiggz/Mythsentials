package com.mythicacraft.plugins.mythsentials.Dragon;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;

import com.mythicacraft.plugins.mythsentials.Mythsentials;
import com.mythicacraft.plugins.mythsentials.Utilities.ConfigAccessor;
import com.mythicacraft.plugins.mythsentials.Utilities.Time;

public class DragonChecker implements CommandExecutor {
	private final Mythsentials plugin;

	public DragonChecker(final Mythsentials instance) {
		this.plugin = instance;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		ConfigAccessor dragonData = new ConfigAccessor("dragon.yml");
		if(commandLabel.equalsIgnoreCase("dragon")) {
			if(dragonCheck()) {
				sender.sendMessage(ChatColor.GOLD + "The dragon is " + ChatColor.GREEN +"alive" + ChatColor.GOLD + "!");
			} else {
				String killer = dragonData.getConfig().getString("DragonData.dragonLastKilledBy");
				String dragonDeathTime = dragonData.getConfig().getString("DragonData.deathTime");
				if(killer.equalsIgnoreCase("playername")) {
					sender.sendMessage(ChatColor.GOLD + "The dragon is " + ChatColor.RED +"dead" + ChatColor.GOLD + ".");
				} else {
					sender.sendMessage(ChatColor.GOLD + "The dragon is " + ChatColor.RED +"dead" + ChatColor.GOLD + ". It was last slain by " + ChatColor.YELLOW + killer + ChatColor.GOLD + "," + deathTimeMessage(dragonDeathTime));
				}
			}
		}
		return true;
	}

	public boolean dragonCheck(){ //Checks for dragon by loading some chunks in the End and looking for dragon
		World theEnd = plugin.getServer().getWorld("The_Realm_the_end");
		if(theEnd.getPlayers().isEmpty()) {
			for (int x = -3; x <= 3; x++) {
				for (int z = -3; z <= 3; z++) {
					theEnd.loadChunk(x, z);
				}
			}
			for(Entity e : theEnd.getEntities()) {
				if(e instanceof EnderDragon) {
					for (int x = -3; x <= 3; x++) {
						for (int z = -3; z <= 3; z++) {
							theEnd.unloadChunk(x, z);
						}
					}
					return true;
				}
			}
		} else {
			for(Entity e : theEnd.getEntities()) {
				if(e instanceof EnderDragon) {
					return true;
				}
			}
		}
		return false;
	}

	public static String deathTimeMessage(String deathTime) { //creates a string that describes how long ago the dragon died
		int allMinutes = Time.returnMins(deathTime);
		int hours = allMinutes/60;
		int rMinutes = allMinutes - (60*hours);
		String minOrS = null;
		if(rMinutes == 1) {
			minOrS = " minute";
		}
		if(rMinutes > 1) {
			minOrS = " minutes";
		}
		if(hours < 1 ) {
			if(rMinutes < 1) {
				String message = " just moments ago.";
				return message;
			}
			String message = " " + rMinutes + minOrS + " ago.";
			return message;
		}
		if(hours == 1) {
			if(rMinutes == 0) {
				String message = " an hour ago.";
				return message;
			}
			if(rMinutes > 0) {
				String message = " 1 hour and " + rMinutes + minOrS + " ago.";
				return message;
			}
		}
		if(hours > 1) {
			if(rMinutes == 0) {
				String message = " " + hours + " hours ago.";
				return message;
			}
			if(rMinutes > 0) {
				String message = " " + hours + " hours and " + rMinutes + minOrS + " ago.";
				return message;
			}
		}
		return null;
	}
}
