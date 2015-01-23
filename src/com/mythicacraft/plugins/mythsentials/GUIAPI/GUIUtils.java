package com.mythicacraft.plugins.mythsentials.GUIAPI;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;


public class GUIUtils {

	public static Inventory generateInventory(int neededSlots, String title) {

		int req = neededSlots;
		int multOf9 = 9;
		while(req > multOf9) {
			multOf9 += 9;
		}
		return Bukkit.createInventory(null, multOf9, title);
	}

}
