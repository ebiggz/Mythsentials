package com.mythicacraft.plugins.mythsentials.CmdExecutors;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ResTool implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if(commandLabel.equalsIgnoreCase("restool")) {
			if(sender.hasPermission("mythica.restool")) {
				giveTool((Player) sender);
			} else {
				sender.sendMessage(ChatColor.RED + "Sorry, you don't have permission to use the res tool command.");
			}
		}
		return true;
	}
	public boolean giveTool(Player player)
	{
		ItemStack handitem = player.getItemInHand();
		Inventory inv = player.getInventory();

		if (!handitem.getType().equals(Material.WOOD_HOE))
		{
			if (!handitem.getType().equals(Material.AIR))
			{
				if (inv.firstEmpty() == -1)
				{
					player.sendMessage(ChatColor.RED + "No space in your inventory.");
					return false;
				}

				inv.setItem(inv.firstEmpty(), handitem);
			}

			if (inv.contains(Material.WOOD_HOE))
			{
				Integer slotId = inv.first(Material.WOOD_HOE);
				ItemStack stack = inv.getItem(slotId);
				Integer stackAmount = stack.getAmount();

				if (stackAmount > 1)
				{
					stack.setAmount(stackAmount - 1);
				}
				else
				{
					inv.clear(slotId);
				}
			}

			player.setItemInHand(new ItemStack(Material.WOOD_HOE, 1));
			player.sendMessage(ChatColor.GOLD + "Here's your res tool.");
		}
		else
		{
			player.sendMessage(ChatColor.RED + "You already have the res tool in your hand!");
			return false;
		}

		return true;
	}
}