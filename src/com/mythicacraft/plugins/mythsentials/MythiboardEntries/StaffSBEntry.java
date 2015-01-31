package com.mythicacraft.plugins.mythsentials.MythiboardEntries;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.mythicacraft.plugins.mythsentials.MythiboardAPI.ScoreboardEntry;
import com.mythicacraft.plugins.mythsentials.Utilities.Utils;


public class StaffSBEntry implements ScoreboardEntry {

	@Override
	public String getKey() {
		return ChatColor.GOLD + "Staff";
	}

	@Override
	public String getValue(Player player) {
		if(Utils.modsOnline()) {
			return ChatColor.GREEN + "Available";
		} else {
			return ChatColor.RED + "Unavailable";
		}
	}

	@Override
	public String getCommand() {
		if(Utils.modsOnline()) {
			return "/helpme";
		} else {
			return "/issue create";
		}
	}

	@Override
	public ItemStack getButton(Player player) {
		// TODO Auto-generated method stub
		return null;
	}
}
