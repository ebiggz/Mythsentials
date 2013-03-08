package com.gmail.ebiggz.plugins.mythsentials;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;

public class EnderDragonChecker implements CommandExecutor {
	public static Mythsentials plugin;
	 
	public EnderDragonChecker(Mythsentials plugin) {
		EnderDragonChecker.plugin = plugin;
	}
 
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if(commandLabel.equalsIgnoreCase("dragon")) {
			if(dragonCheck() == true) {
				sender.sendMessage(ChatColor.GOLD + "The dragon is " + ChatColor.GREEN +"alive" + ChatColor.GOLD + "!");
			} else {
				String killer = plugin.getConfig().getString("DragonData.dragonLastKilledBy");
				if(killer.equalsIgnoreCase("playername")) {
					sender.sendMessage(ChatColor.GOLD + "The dragon is " + ChatColor.RED +"dead" + ChatColor.GOLD + ".");
					return true;
				}
				sender.sendMessage(ChatColor.GOLD + "The dragon is " + ChatColor.RED +"dead" + ChatColor.GOLD + ". It was last slain by " + ChatColor.YELLOW + killer + ChatColor.GOLD + ".");
			}
			return true;
		}
		return false;		
	}
	public boolean dragonCheck(){
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
}
