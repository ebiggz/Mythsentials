package com.mythicacraft.plugins.mythsentials.CmdExecutors;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.entity.Wolf;

import com.mythicacraft.plugins.mythsentials.CmdExecutors.CmdProperties.Type;



public class CmdProcessor {

	public CmdProcessor() {

	}

	public static void processCommand(Player player, CmdProperties CmdP, Tameable pet) {

		Type cmdType = CmdP.getType();
		String owner = pet.getOwner().getName();
		String petType = null;
		Ocelot cat = null;
		Wolf wolf = null;
		if(pet instanceof Ocelot) {
			cat = (Ocelot) pet;
			petType = "Cat";
		} else {
			wolf = (Wolf) pet;
			petType = "Wolf";
		}
		if(cmdType == Type.GIVE) {
			if(!owner.equals(player.getName())) {
				player.sendMessage(ChatColor.RED + "You are not allowed to give this " + petType.toLowerCase() + " to someone else!");
				return;
			}
			String newOwnerName = null;
			if(CmdP.useOfflinePlayer()) {
				OfflinePlayer newOwnerO = CmdP.getNewOwnerO();
				newOwnerName = newOwnerO.getName();
				pet.setOwner((AnimalTamer) newOwnerO);
			} else {
				Player newOwner = CmdP.getNewOwner();
				pet.setOwner((AnimalTamer) newOwner);
				newOwnerName = newOwner.getName();
				if(newOwner.isOnline()) {
					newOwner.sendMessage(ChatColor.AQUA + player.getName() + " has given ownership of a " + petType.toLowerCase() + " to you.");
				}
			}
			player.sendMessage(ChatColor.AQUA + "You have given ownership of this " + petType.toLowerCase() + " to " +  newOwnerName + ".");
		}

		if(cmdType == Type.INFO) {
			player.sendMessage(ChatColor.YELLOW + petType + " info:");
			player.sendMessage(ChatColor.GOLD + "Owner: " + ChatColor.AQUA + pet.getOwner().getName());
			if(petType.equalsIgnoreCase("wolf")) {
				player.sendMessage(ChatColor.GOLD + "Health: " + ChatColor.AQUA + Math.round(wolf.getHealth()) + "/" + Math.round(wolf.getMaxHealth()));
			}
			if(petType.equalsIgnoreCase("cat")) {
				player.sendMessage(ChatColor.GOLD + "Type: " + ChatColor.AQUA + cat.getCatType().toString().toLowerCase().replace("_", "").replace("red", "tabby"));
				player.sendMessage(ChatColor.GOLD + "Health: " + ChatColor.AQUA + Math.round(cat.getHealth()) + "/" + Math.round(cat.getMaxHealth()));
			}
		}
	}
}
