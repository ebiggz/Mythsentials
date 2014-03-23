package com.mythicacraft.plugins.mythsentials.Store;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mythicacraft.plugins.mythsentials.Mythian;
import com.mythicacraft.plugins.mythsentials.MythianManager;
import com.mythicacraft.plugins.mythsentials.Mythsentials;


public class StoreCommands implements CommandExecutor {

	private static Mythsentials plugin;

	public StoreCommands(Mythsentials instance) {
		plugin = instance;
	}

	StoreManager sm = Mythsentials.getStoreManager();
	MythianManager mm = Mythsentials.getMythianManager();

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {

		String playername = sender.getName();
		Mythian mythian = mm.getMythian(playername);

		if(commandLabel.equalsIgnoreCase("ms") || commandLabel.equalsIgnoreCase("mythicastore") || commandLabel.equalsIgnoreCase("mstore")) {
			if(args.length == 0) {
				sender.sendMessage(ChatColor.RED + "[MythicaStore] Type \"/ms claim\" for help.");
			}
			else if(args.length >= 1) {
				if(args[0].equalsIgnoreCase("reload")) {
					if(!sender.isOp()) {
						sender.sendMessage(ChatColor.RED + "[MythicaStore] You don't have permission to this command!");
						return true;
					}
					plugin.loadStoreConfig();
					sender.sendMessage(ChatColor.AQUA + "[MythicaStore] Reload complete!");
					//reload configs
				}
				else if(args[0].equalsIgnoreCase("claim")) {
					if (!(sender instanceof Player)) {
						sender.sendMessage("[MythicaStore] This command can't be used in the console!");
						return true;
					}

					int unclaimedItemsCount = mythian.getUnclaimedItemCount(playername);

					if(args.length == 1) {
						if(unclaimedItemsCount > 0) {
							sender.sendMessage(ChatColor.AQUA + "[MythicaStore] You have " + ChatColor.YELLOW + unclaimedItemsCount + ChatColor.AQUA + " unclaimed store items! Type " + ChatColor.YELLOW + "/ms claim items" + ChatColor.AQUA + " to see them.");
						} else {
							sender.sendMessage(ChatColor.RED + "[MythicaStore] You do not have unclaimed store items!");
						}
					}
					else if(args.length >= 2) {
						if(args[1].equalsIgnoreCase("items")) {

							if(unclaimedItemsCount == 0) {
								sender.sendMessage(ChatColor.RED + "[MythicaStore] You do not have unclaimed rewards!");
								return true;
							}
							List<StoreItem> unclaimedItems = mythian.getUnclaimedItems(playername);
							if(args.length == 2) {
								String[] itemMessages = new String[unclaimedItems.size()];
								int count = 0;
								for(StoreItem item: unclaimedItems) {
									itemMessages[count] = ChatColor.YELLOW + Integer.toString(count+1) + ") " + ChatColor.GOLD + "Name: \"" + ChatColor.YELLOW + item.getName() + ChatColor.GOLD + "\", Required Slots: " + ChatColor.YELLOW + item.getRequiredSlots();
									count++;
								}
								sender.sendMessage(ChatColor.AQUA + "-----[Unclaimed Store Items]-----");
								sender.sendMessage(itemMessages);
								sender.sendMessage(ChatColor.AQUA + "Type " + ChatColor.YELLOW + "/ms claim items #" + ChatColor.AQUA + " or " + ChatColor.YELLOW + "/ms claim items all");

							}
							else if(args.length == 3) {
								if(args[2].equalsIgnoreCase("all")) {
									for(StoreItem item : unclaimedItems) {
										mythian.removeUnclaimedItem(playername, item.getName());
										sm.administerItemContents(item, sender.getName());
									}
								} else {
									try {
										int itemNumber = Integer.parseInt(args[2]);
										if(itemNumber <= unclaimedItems.size()) {
											StoreItem item = unclaimedItems.get(itemNumber-1);
											mythian.removeUnclaimedItem(playername, item.getName());
											sm.administerItemContents(item, sender.getName());
										} else {
											sender.sendMessage(ChatColor.RED + "[VoteRoulette] Not a valid item number!");
										}
									} catch (Exception e) {
										sender.sendMessage(ChatColor.RED + "[VoteRoulette] Not a valid item number!");
									}
								}
							}
						}
					}
				}
			}
		}
		return true;
	}
}

