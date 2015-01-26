package com.mythicacraft.plugins.mythsentials.DeathLedger;

import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.mythicacraft.plugins.mythsentials.GUIAPI.GUI;
import com.mythicacraft.plugins.mythsentials.GUIAPI.GUIManager;
import com.mythicacraft.plugins.mythsentials.GUIAPI.GUIUtils;


public class LogListGUI implements GUI {

	private String selectedPlayer;
	private List<DeathLog> logList;

	public LogListGUI(String selectedPlayer, List<DeathLog> logList) {
		this.selectedPlayer = selectedPlayer;
		this.logList = logList;
	}

	@Override
	public Inventory createInventory(Player player) {

		Inventory inventory = GUIUtils.generateInventory(logList.size(), ChatColor.BLACK + selectedPlayer + "'s " + ChatColor.RED + "Death Logs");

		ItemStack book = new ItemStack(Material.BOOK_AND_QUILL);
		String name = ChatColor.YELLOW + "Death Log #%num%";

		int count = 1;
		for(DeathLog deathLog : logList) {
			ItemStack bookClone = book.clone();
			ItemMeta bookIM = bookClone.getItemMeta();
			bookIM.setDisplayName(name.replace("%num%", Integer.toString(count)));
			bookIM.setLore(Arrays.asList(ChatColor.GRAY + deathLog.getDeathTime()));
			bookClone.setItemMeta(bookIM);
			inventory.addItem(bookClone);
			count++;
		}
		return inventory;
	}

	@Override
	public void onInventoryClick(Player whoClicked, InventoryClickEvent clickEvent) {
		int clickedSlot = clickEvent.getSlot();
		ItemStack clickedItem = clickEvent.getInventory().getItem(clickedSlot);
		if(clickedItem != null && clickedItem.getType() == Material.BOOK_AND_QUILL) {
			DeathLogGUI logGUI = new DeathLogGUI(logList.get(clickedSlot), clickedSlot+1);
			GUIManager.getInstance().showGUI(logGUI, whoClicked);
		}
	}

	@Override
	public void onInventoryClose(Player whoClosed, InventoryCloseEvent closeEvent) {}
}
