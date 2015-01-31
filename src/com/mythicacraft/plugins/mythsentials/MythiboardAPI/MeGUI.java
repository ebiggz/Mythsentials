package com.mythicacraft.plugins.mythsentials.MythiboardAPI;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.mythicacraft.plugins.mythsentials.GUIAPI.GUI;
import com.mythicacraft.plugins.mythsentials.GUIAPI.GUIUtils;


public class MeGUI implements GUI {

	@Override
	public Inventory createInventory(Player player) {
		List<ScoreboardEntry> entries = MythiboardManager.getInstance().getEntries();
		Inventory inv = GUIUtils.generateInventory(entries.size(), "MythicaCraft Notfications");
		for(ScoreboardEntry entry : entries) {
			inv.addItem(entry.getButton(player));
		}
		return inv;
	}

	@Override
	public void onInventoryClick(Player whoClicked, InventoryClickEvent clickedEvent) {
		ItemStack clickedItem = clickedEvent.getCurrentItem();
		if(clickedItem == null) return;
		List<ScoreboardEntry> entries = MythiboardManager.getInstance().getEntries();
		whoClicked.performCommand(entries.get(clickedEvent.getSlot()).getCommand());
		whoClicked.closeInventory();
	}

	@Override
	public void onInventoryClose(Player whoClosed, InventoryCloseEvent closeEvent) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean ignoreForeignItems() {
		// TODO Auto-generated method stub
		return false;
	}

}
