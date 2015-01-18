package com.mythicacraft.plugins.mythsentials.MythiboardAPI;

import org.bukkit.entity.Player;


public interface ScoreboardEntry {

	public String getKey();

	public String getValue(Player player);

	public String getCommand();

}
