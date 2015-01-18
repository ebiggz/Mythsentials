package com.mythicacraft.plugins.mythsentials.AdminTools;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;


public class PIMenuData {

	private Inventory inventory;
	private Player mod;
	private String target;
	private boolean targetIsOnline;

	PIMenuData(Inventory i, Player m, String t, boolean online) {
		inventory = i;
		mod = m;
		target = t;
		targetIsOnline = online;
	}

	public Inventory getInventory() {
		return inventory;
	}

	public Player getMod() {
		return mod;
	}

	public String getTarget() {
		return target;
	}

	public boolean isTargetIsOnline() {
		return targetIsOnline;
	}

}
