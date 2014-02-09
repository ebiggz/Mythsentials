package com.mythicacraft.plugins.mythsentials.MiscCommands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class RepairCmd implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel,
			String[] args) {
		Player senderPlayer = Bukkit.getPlayer(sender.getName().toString());
		if(commandLabel.equalsIgnoreCase("mythicarepair")){
			if(!sender.hasPermission("mythica.repair")){
				sender.sendMessage(ChatColor.RED + "You do not have permissions to use that command!");
				return true;
			}
			else{
				ItemStack item = senderPlayer.getItemInHand();
				int ITEM_COST = 0;
				int XP_COST = 0;
				if(!isRepairable(item)){
					sender.sendMessage(ChatColor.GOLD + "You may only repair diamond tools or bows!");
					return true;
				}
				else if(item.getDurability() < 10){
					sender.sendMessage(ChatColor.GOLD + "That item is not damaged enough to repair.");
					return true;
				}
				else if(!item.getItemMeta().hasEnchants()){
					sender.sendMessage(ChatColor.GOLD + "You may only repair items that are enchanted!");
				}
				else if(item.getDurability() > 66){
					ITEM_COST = 3;
					XP_COST = getRepairXPCost(item.getDurability());
				}
				else if(item.getDurability() > 33){
					ITEM_COST = 2;
					XP_COST = getRepairXPCost(item.getDurability());
				}
				else if(item.getDurability() > 0){
					ITEM_COST = 1;
					XP_COST = getRepairXPCost(item.getDurability());
				}
				ITEM_COST = getArmorCostModifier(item, ITEM_COST);
				if(!checkIngredients(senderPlayer, item, ITEM_COST)){
					sender.sendMessage(ChatColor.GOLD + "You need at least " + ChatColor.BLUE + ITEM_COST +
							ChatColor.GOLD + " of the required material in your inventory to repair this item!");
					return true;
				}
				if(!checkRepairXPCost(senderPlayer, XP_COST)){
					sender.sendMessage(ChatColor.GOLD + "You need at least " + ChatColor.BLUE + XP_COST +
							ChatColor.GOLD + " XP to repair this item!");
					return true;
				}
				Material mat = (item.getType().equals(Material.BOW)) ? Material.BOW : Material.DIAMOND;
				senderPlayer.getInventory().remove(new ItemStack(mat, ITEM_COST));
				senderPlayer.setLevel(senderPlayer.getLevel() - XP_COST);
				item.setDurability((short) -1);
				sender.sendMessage(ChatColor.GREEN + "Your item has been repaired!");
				return true;
			}
		}
		return false;
	}

	private int getArmorCostModifier(ItemStack item, int ItemCost){
		if(item.getType().equals(Material.DIAMOND_CHESTPLATE))
			ItemCost += 5;
		else if(item.getType().equals(Material.DIAMOND_HELMET))
			ItemCost += 2;
		else if(item.getType().equals(Material.DIAMOND_LEGGINGS))
			ItemCost += 4;
		else if(item.getType().equals(Material.DIAMOND_BOOTS))
			ItemCost += 1;
		return ItemCost;
	}

	private boolean checkIngredients(Player player, ItemStack item, int amount){
		Material mat = (item.getType().equals(Material.BOW)) ? Material.BOW : Material.DIAMOND;
		return player.getInventory().contains(mat, amount);
	}

	private boolean isRepairable(ItemStack item){
		return item.getType().equals(Material.DIAMOND_AXE) || item.getType().equals(Material.DIAMOND_AXE) ||
				item.getType().equals(Material.DIAMOND_PICKAXE) || item.getType().equals(Material.DIAMOND_SPADE) ||
				item.getType().equals(Material.DIAMOND_HOE) || item.getType().equals(Material.DIAMOND_SWORD) ||
				item.getType().equals(Material.DIAMOND_HELMET) || item.getType().equals(Material.DIAMOND_CHESTPLATE) ||
				item.getType().equals(Material.DIAMOND_LEGGINGS) || item.getType().equals(Material.DIAMOND_BOOTS) ||
				item.getType().equals(Material.BOW);
	}

	private boolean checkRepairXPCost(Player player, int xpCost){
		return player.getLevel() > xpCost;
	}

	private int getRepairXPCost(int modifier){
		return modifier/2;
	}
}