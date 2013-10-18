package com.mythicacraft.plugins.mythsentials.CmdExecutors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mythicacraft.plugins.mythsentials.Mythsentials;
import com.mythicacraft.plugins.mythsentials.CmdExecutors.CmdProperties.Type;
import com.mythicacraft.plugins.mythsentials.Tools.Utils;

public class PetCmds implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		Player player = (Player) sender;
		if(commandLabel.equalsIgnoreCase("pet")) {
			if(args.length >= 1) {
				if(args[0].equalsIgnoreCase("give")) {
					if(args.length == 1) {
						sender.sendMessage(ChatColor.RED + "You must include a playername to set the owner of your pet to!");
					} else {
						String name = Utils.completeName(args[1]);
						if(name != null) {
							Player newOwner = Bukkit.getPlayer(name);
							CmdProperties CmdP;
							if(newOwner == null) {
								OfflinePlayer newOwnerO = Bukkit.getOfflinePlayer(name);
								CmdP = new CmdProperties(Type.GIVE, newOwnerO);
							} else {
								CmdP = new CmdProperties(Type.GIVE, newOwner);
							}
							if(Mythsentials.petSelector.containsKey(player)) {
								Mythsentials.petSelector.remove(player);
							}
							Mythsentials.petSelector.put(player, CmdP);
							sender.sendMessage(ChatColor.AQUA + "Please left-click a pet to apply " + name + " as new owner now. Damage will be canceled.");
						} else {
							sender.sendMessage(ChatColor.RED + args[1] + " is not a recongized player name.");
						}
					}
				}
				else if(args[0].equalsIgnoreCase("info")) {
					CmdProperties CmdP = new CmdProperties(Type.INFO);
					if(Mythsentials.petSelector.containsKey(player)) {
						Mythsentials.petSelector.remove(player);
					}
					Mythsentials.petSelector.put(player, CmdP);
					sender.sendMessage(ChatColor.AQUA + "Please left-click a pet to get info now. Damage will be canceled.");
				} else {
					sender.sendMessage(ChatColor.RED + "Error. /pet (info|give [playername])");
				}
			}
		}
		return true;
	}
}