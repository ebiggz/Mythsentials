package com.mythicacraft.plugins.mythsentials.MythiboardEntries;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.mythicacraft.plugins.mythsentials.Mythsentials;
import com.mythicacraft.plugins.mythsentials.MythiboardAPI.ScoreboardEntry;


public class FriendsSBEntry implements ScoreboardEntry {

	@Override
	public String getKey() {
		return ChatColor.GOLD + "Friends";
	}

	@Override
	public String getValue(Player player) {

		List<String> friends = Mythsentials.getMythianManager().getMythian(player.getName()).getFriendList();
		int onlineCount = 0;
		for(String friendName : friends) {
			Player friendPlayer = Bukkit.getPlayer(friendName);
			if(friendPlayer != null && friendPlayer.isOnline()) {
				onlineCount++;
			}
		}
		return ChatColor.GREEN + Integer.toString(onlineCount) + "/" + Integer.toString(friends.size()) + " Online";
	}

	@Override
	public String getCommand() {
		// TODO Auto-generated method stub
		return "/friends";
	}

	@Override
	public ItemStack getButton(Player player) {
		// TODO Auto-generated method stub
		return null;
	}
}
