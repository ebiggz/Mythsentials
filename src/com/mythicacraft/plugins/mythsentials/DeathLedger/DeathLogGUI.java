package com.mythicacraft.plugins.mythsentials.DeathLedger;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.mythicacraft.plugins.mythsentials.GUIAPI.GUI;


public class DeathLogGUI implements GUI {

	private DeathLog deathDrop;
	private int dropNumber;

	public DeathLogGUI(DeathLog deathDrop, int dropNumber) {
		this.deathDrop = deathDrop;
		this.dropNumber = dropNumber;
	}

	@Override
	public Inventory createInventory(Player player) {
		List<ItemStack> drops = deathDrop.getDrops();

		int req = drops.size() + 18;
		int multOf9 = 9;
		while(req > multOf9) {
			multOf9 += 9;
		}

		Inventory inventory = Bukkit.createInventory(null, multOf9, ChatColor.BLACK + deathDrop.getPlayer() + " Death Log #" + dropNumber);

		for(ItemStack drop : drops) {
			inventory.addItem(drop);
		}

		//ItemStack stainedGlass = new ItemStack(Material.getMaterial(160), 1, (short)15); //create itemstack with a Material type.
		ItemStack stainedGlass = new ItemStack(Material.STONE_BUTTON);
		ItemMeta sgIM = stainedGlass.getItemMeta();
		sgIM.setDisplayName(ChatColor.STRIKETHROUGH + "---");
		stainedGlass.setItemMeta(sgIM);

		int barRowBegin = multOf9-18;
		int barRowEnd = multOf9-9;
		for(int i = barRowBegin; i < barRowEnd; i++) {
			inventory.setItem(i, stainedGlass);
		}

		ItemStack infoSign = new ItemStack(Material.BOOK_AND_QUILL);
		ItemMeta signIM = infoSign.getItemMeta(); //Item meta contains options for display name and lore text
		signIM.setDisplayName(ChatColor.YELLOW + "" + ChatColor.UNDERLINE + "Details");
		signIM.setLore(Arrays.asList(ChatColor.GOLD + "When: " + ChatColor.GRAY + deathDrop.getDeathTime(),ChatColor.GOLD + "Where: " + ChatColor.GRAY + deathDrop.getDeathLoc(),ChatColor.GOLD + "Why: " + ChatColor.GRAY + deathDrop.getReason(),"*Click to go back to log list*"));
		infoSign.setItemMeta(signIM); // apply item meta to item stack
		inventory.setItem(multOf9-6,infoSign); // Add item to inventory

		ItemStack enderChest = new ItemStack(Material.ENDER_CHEST);
		ItemMeta ecIM = enderChest.getItemMeta();
		ecIM.setDisplayName(ChatColor.YELLOW + "Set " + deathDrop.getPlayer()+  "'s Inventory");
		ecIM.setLore(Arrays.asList("*Click to set " + deathDrop.getPlayer() + "'s","inventory to this death log*"));
		enderChest.setItemMeta(ecIM);
		inventory.setItem(multOf9-5,enderChest);

		ItemStack chest = new ItemStack(Material.CHEST);
		ItemMeta cIM = chest.getItemMeta();
		cIM.setDisplayName(ChatColor.YELLOW + "Set Your Inventory");
		cIM.setLore(Arrays.asList("*Click to set your inventory","to this death log*"));
		chest.setItemMeta(cIM);
		inventory.setItem(multOf9-4,chest);

		return inventory;
	}

	@Override
	public void onInventoryClick(Player whoClicked, InventoryClickEvent clickEvent) {

		int clickedSlot = clickEvent.getSlot();
		ItemStack clickedItem = clickEvent.getInventory().getItem(clickedSlot);

		int invSize = clickEvent.getInventory().getSize();

		if(clickedSlot == invSize-6) {
			if(clickedItem != null && clickedItem.getType() == Material.BOOK_AND_QUILL) {
				whoClicked.closeInventory();
				whoClicked.performCommand("deathlogs " + deathDrop.getPlayer());
			}
		}
		else if(clickedSlot == invSize-5) {
			if(clickedItem != null && clickedItem.getType() == Material.ENDER_CHEST) {
				whoClicked.closeInventory();
				whoClicked.performCommand("deathlogs " + deathDrop.getPlayer() + " setplayerinv " + dropNumber);
			}
		}
		else if(clickedSlot == invSize-4) {
			if(clickedItem != null && clickedItem.getType() == Material.CHEST) {
				whoClicked.closeInventory();
				whoClicked.performCommand("deathlogs " + deathDrop.getPlayer() + " setmyinv " + dropNumber);
			}
		}
	}

	@Override
	public void onInventoryClose(Player whoClosed, InventoryCloseEvent closeEvent) {}

	@Override
	public boolean ignoreForeignItems() {
		return false;
	}
}
