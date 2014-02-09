package com.mythicacraft.plugins.mythsentials.Affixer;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.mythicacraft.plugins.mythsentials.Mythsentials;


public class PlayerName {

	private Player player;
	private String world = null;

	PlayerName(Player player) {
		this.player = player;
	}

	public String getName() {
		return player.getName();
	}

	public String getFullName() {
		return getPrefix() + player.getName() + getSuffix();
	}

	public String getPreview() {
		return ChatColor.translateAlternateColorCodes('&', getPrefix() + player.getName() + ChatColor.RESET + getSuffix());
	}

	public boolean hasPrefix() {
		if(getPrefix().isEmpty()) return false;
		return true;
	}

	public String getPrefix() {
		return Mythsentials.chat.getPlayerPrefix(player);
	}

	public void setPrefix(String prefix) {
		Mythsentials.chat.setPlayerPrefix(world, player.getName(), prefix);
	}

	public boolean hasSuffix() {
		if(getSuffix().isEmpty()) return false;
		return true;
	}

	public String getSuffix() {
		return Mythsentials.chat.getPlayerSuffix(player);
	}

	public void setSuffix(String suffix) {
		Mythsentials.chat.setPlayerSuffix(world, player.getName(), suffix);
	}
}
