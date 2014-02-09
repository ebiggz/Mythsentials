package com.mythicacraft.plugins.mythsentials.Unenchant;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.gmail.mythicacraft.mythicaspawn.SpawnManager.Universe;
import com.mythicacraft.plugins.mythsentials.Mythian;
import com.mythicacraft.plugins.mythsentials.MythianManager;
import com.mythicacraft.plugins.mythsentials.Mythsentials;
import com.mythicacraft.plugins.mythsentials.Utilities.Utils;


public class UnenchantCmd implements CommandExecutor {

	public Mythsentials plugin;

	public UnenchantCmd(Mythsentials plugin) {
		this.plugin = plugin;
	}
	public boolean onCommand(final CommandSender sender, Command cmd, String commandLabel, String[] args) {

		MythianManager mm = Mythsentials.getMythianManager();
		Mythian mythian = mm.getMythian(sender.getName());

		if(commandLabel.equalsIgnoreCase("runes") || commandLabel.equalsIgnoreCase("rune")) {
			//buy, give, set, help
			if(args.length == 0) {
				int runes = mythian.getRunes();
				sender.sendMessage(ChatColor.GOLD + "You have " + ChatColor.YELLOW + runes + ChatColor.GOLD + " Runes.");
				sender.sendMessage(ChatColor.GOLD + "Type /runes ? for info on Runes.");
				//show runes
			}
			else if(args.length >= 1) {
				if(args[0].equalsIgnoreCase("?")) {
					sender.sendMessage(ChatColor.YELLOW + "[Rune help]");
					sender.sendMessage(ChatColor.GOLD + "Runes allow you to unenchant a specific enchantment on an item you are holding.");
					sender.sendMessage(ChatColor.GOLD + "Runes cost $50 each.");
					sender.sendMessage(ChatColor.GOLD + "\"/runes\" shows how many Runes you have.");
					sender.sendMessage(ChatColor.GOLD + "\"/runes buy [amount]\" allows you to buy Runes.");
					sender.sendMessage(ChatColor.GOLD + "\"/runes give [player] [amount]\" allows you to give Runes to others.");
					if(sender.hasPermission("mythica.mod")) {
						sender.sendMessage(ChatColor.GOLD + "\"/runes set [player] +/-[amount]\" allows you to set others Runes.");
					}
				}
				if(args[0].equalsIgnoreCase("buy")) {
					if(args.length == 1) {
						sender.sendMessage(ChatColor.GOLD + "Runes cost $50 each. Type \"/runes buy [amount]\" to purchase some.");
					}
					else if(args.length == 2) {
						try {
							int amount = Integer.parseInt(args[1]);
							if(amount < 1) {
								sender.sendMessage(ChatColor.RED + "You must buy at least one Rune!");
								return true;
							}

							double cost = 50.0*amount;
							double balance = Mythsentials.economy.getBalance(sender.getName());

							if(balance >= cost) {
								Mythsentials.economy.withdrawPlayer(sender.getName(), cost);
								mythian.setRunes(mythian.getRunes() + amount);
								String confirmation = ChatColor.GOLD + "You have bought " + ChatColor.YELLOW + amount;
								if(amount > 1) {
									confirmation += " Runes";
								} else {
									confirmation += " Rune";
								}
								confirmation += ChatColor.GOLD + ", bringing you to a total of " + ChatColor.YELLOW + mythian.getRunes() + ".";
								sender.sendMessage(confirmation);
							} else {
								sender.sendMessage(ChatColor.RED + "You do not have enough in your bank to purchase this!");
							}
						} catch (Exception e) {
							sender.sendMessage(ChatColor.RED + "\"" + args[1] + "\" is not a valid number!");
						}
					}
				} //end of buy command
				if(args[0].equalsIgnoreCase("give")) {
					if(args.length != 3) {
						sender.sendMessage(ChatColor.RED + "Invaild give command. \"/runes give [player] [amount]\"");
					} else {
						try {
							String otherPlayer = Utils.completeName(args[1]);
							int amount = Integer.parseInt(args[2]);
							if(otherPlayer == null) {
								sender.sendMessage(ChatColor.RED + "Couldn't find a player by the name: " + args[1]);
								return true;
							}
							if(amount < 1) {
								sender.sendMessage(ChatColor.RED + "You must give at least one Rune!");
								return true;
							}
							if(mythian.getRunes() < amount) {
								sender.sendMessage(ChatColor.RED + "You don't have " + amount + " Runes to give!");
								return true;
							}

							mythian.setRunes(mythian.getRunes() - amount);
							Mythian otherMythian = mm.getMythian(otherPlayer);
							otherMythian.setRunes(otherMythian.getRunes() + amount);

							sender.sendMessage(ChatColor.GOLD + "You have given " + ChatColor.YELLOW + otherPlayer + ChatColor.GOLD + " " + amount + " Rune(s).");

							Player otherP = Bukkit.getPlayer(otherPlayer);
							if(otherP != null && otherP.isOnline()) {
								otherP.sendMessage(ChatColor.YELLOW + sender.getName() + ChatColor.GOLD + " just gave you \"" + ChatColor.YELLOW + amount + ChatColor.GOLD + "\" Rune(s)!");
							}
						} catch (Exception e) {
							sender.sendMessage(ChatColor.RED + "\"" + args[2] + "\" is not a valid number!");
						}
					}
				} //end of give command
				if(args[0].equalsIgnoreCase("set")) {
					if(!sender.hasPermission("mythica.mod")) {
						sender.sendMessage(ChatColor.RED + "You don't have permission for this!");
						return true;
					}
					if(args.length != 3) {
						sender.sendMessage(ChatColor.RED + "Invaild set command. \"/runes set [player] +/-[amount]\"");
					} else {
						String otherPlayer = Utils.completeName(args[1]);
						if(otherPlayer == null) {
							sender.sendMessage(ChatColor.RED + "Couldn't find a player by the name: " + args[1]);
							return true;
						}
						String amount = args[2];
						try {
							int runes = Integer.parseInt(amount);
							Mythian otherMythian = mm.getMythian(otherPlayer);
							if(amount.startsWith("+") || amount.startsWith("-")) {
								if(otherMythian.getRunes() + runes < 0) {
									sender.sendMessage(ChatColor.RED + args[1] + " doesn't have that many Runes to remove!");
									return true;
								}
								otherMythian.setRunes(otherMythian.getRunes() + runes);
							} else {
								otherMythian.setRunes(runes);
							}
							if(sender instanceof Player) {
								sender.sendMessage(ChatColor.YELLOW + otherPlayer + ChatColor.GOLD + "'s Rune count is now " + ChatColor.YELLOW + otherMythian.getRunes() + ChatColor.GOLD + ".");
							}
						} catch (Exception e) {
							sender.sendMessage(ChatColor.RED + "\"" + args[2] + "\" is not a valid number!");
						}
					}
				}
			}
		}//end of Rune commands
		if(commandLabel.equalsIgnoreCase("unenchant")) {
			if(!(sender instanceof Player)) {
				return true;
			}
			Player player = (Player) sender;
			if(args.length == 1) {
				Enchantment enchant = Utils.getEnchantEnumFromName(args[0]);
				ItemStack inHand = player.getItemInHand();
				if(enchant == null) {
					sender.sendMessage(ChatColor.RED + "That is not a recognized enchantment!");
					return true;
				}
				if(inHand.containsEnchantment(enchant)) {
					if(mythian.getCurrentUniverse() == Universe.SURVIVAL) {
						if(enchant == Enchantment.PROTECTION_FALL || enchant == Enchantment.DURABILITY) {
							player.giveExpLevels(Utils.getEnchantXpWorthSurvival(enchant, inHand.getEnchantmentLevel(enchant)));
							inHand.removeEnchantment(enchant);
							if(sender.getName().equals("scribbles08")) {
								sender.sendMessage(ChatColor.LIGHT_PURPLE + "Congrats princess, you've unenchanted " + ChatColor.YELLOW + Utils.getNameFromEnchant(enchant) + ChatColor.LIGHT_PURPLE + ", and I didn't even charge you for it <3");
							} else {
								sender.sendMessage(ChatColor.GOLD + "You successfully unenchanted " + ChatColor.YELLOW + Utils.getNameFromEnchant(enchant) + ChatColor.GOLD + ", for free!");
								return true;
							}
						}
					}
					if(mythian.getRunes() < 1) {
						sender.sendMessage(ChatColor.RED + "You don't have a Rune to use for an unenchantment! Type \"/runes ?\" for more info.");
						return true;
					}
					player.giveExpLevels(Utils.getEnchantXpWorth(enchant, inHand.getEnchantmentLevel(enchant)));
					inHand.removeEnchantment(enchant);
					mythian.setRunes(mythian.getRunes() - 1);
					if(sender.getName().equals("scribbles08")) {
						sender.sendMessage(ChatColor.LIGHT_PURPLE + "Congrats princess, you've unenchanted " + ChatColor.YELLOW + Utils.getNameFromEnchant(enchant) + ChatColor.LIGHT_PURPLE + ", using a Rune <3");
					} else {
						sender.sendMessage(ChatColor.GOLD + "You successfully unenchanted " + ChatColor.YELLOW + Utils.getNameFromEnchant(enchant) + ChatColor.GOLD + ", using a Rune!");
						return true;
					}
				} else {
					sender.sendMessage(ChatColor.RED + "That item doesn't have the enchantment you specified!");
				}
			}
			else {
				sender.sendMessage(ChatColor.RED + "Typing \"/unenchant [enchantment]\" while holding an item with that enchantment, it will remove the enchant at the cost of 1 Rune. Learn about Runes by typing \"/runes ?\"");
			}
		}
		return true;
	}
}