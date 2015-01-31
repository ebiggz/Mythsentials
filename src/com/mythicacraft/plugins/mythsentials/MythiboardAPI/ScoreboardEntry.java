package com.mythicacraft.plugins.mythsentials.MythiboardAPI;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;


public interface ScoreboardEntry {

	public String getKey();

	public String getValue(Player player);

	public String getCommand();

	public ItemStack getButton(Player player);

}
