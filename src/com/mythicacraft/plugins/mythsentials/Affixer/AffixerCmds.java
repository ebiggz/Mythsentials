package com.mythicacraft.plugins.mythsentials.Affixer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AffixerCmds implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {

		PlayerName playerName = new PlayerName((Player) sender);

		if(commandLabel.equalsIgnoreCase("affixer")) { //helpmenu commands
			if(args.length == 0 || ((args[0].equalsIgnoreCase("?") || args[0].equalsIgnoreCase("help")) && args.length < 2)) {
				helpMenu(sender);
				return true;
			} else {
				sender.sendMessage(ChatColor.RED + "Type /Affixer ? for help");
				return true;
			}
		}

		else if(commandLabel.equalsIgnoreCase("colors") || commandLabel.equalsIgnoreCase("formats")) {
			if(!sender.hasPermission("affixer.colors")) { //Check if player has the correct permission node
				sender.sendMessage(ChatColor.RED + "You don't have permission to view color and format codes!");
				return true;
			}
			sender.sendMessage(ChatColor.WHITE + "Colors:" + ChatColor.BLACK + " &0"  + ChatColor.DARK_BLUE + " &1" + ChatColor.DARK_GREEN + " &2" + ChatColor.DARK_AQUA + " &3" + ChatColor.DARK_RED + " &4" + ChatColor.DARK_PURPLE + " &5" + ChatColor.GOLD + " &6" + ChatColor.GRAY + " &7" + ChatColor.DARK_GRAY + " &8" + ChatColor.BLUE + " &9" + ChatColor.GREEN + " &a" + ChatColor.AQUA + " &b" + ChatColor.RED + " &c" + ChatColor.LIGHT_PURPLE + " &d" + ChatColor.YELLOW + " &e" + ChatColor.WHITE + " &f");
			sender.sendMessage(ChatColor.WHITE + "Formats:" + ChatColor.BOLD + " &l" + ChatColor.RESET + " " + ChatColor.STRIKETHROUGH + "&m" + ChatColor.RESET + " " + ChatColor.UNDERLINE + "&n" + ChatColor.RESET + " " + ChatColor.ITALIC + "&o"  + ChatColor.RESET + " &r(reset)");
		}

		else if(commandLabel.equalsIgnoreCase("colornames") || commandLabel.equalsIgnoreCase("colorname")) { //color names command
			if(!sender.hasPermission("affixer.colornames")) { //Check if player has the correct permission node
				sender.sendMessage(ChatColor.RED + "You don't have permission to view the color names!");
				return true;
			}
			//send the color and format codes
			sender.sendMessage(ChatColor.WHITE + "Colors:" + ChatColor.BLACK + " Black"  + ChatColor.DARK_BLUE + " DarkBlue" + ChatColor.DARK_GREEN + " DarkGreen" + ChatColor.DARK_AQUA + " DarkAqua" + ChatColor.DARK_RED + " DarkRed" + ChatColor.DARK_PURPLE + " Purple" + ChatColor.GOLD + " Orange" + ChatColor.GRAY + " Grey" + ChatColor.DARK_GRAY + " DarkGray" + ChatColor.BLUE + " Blue" + ChatColor.GREEN + " Green" + ChatColor.AQUA + " Aqua" + ChatColor.RED + " Red" + ChatColor.LIGHT_PURPLE + " Pink" + ChatColor.YELLOW + " Yellow" + ChatColor.WHITE + " White");

		}

		else if(commandLabel.equalsIgnoreCase("prefix")) {
			if(!sender.hasPermission("affixer.prefix")) {
				//Message for player if they don't have the prefix permission
				sender.sendMessage(ChatColor.RED + "You must be a donator or subscriber to set your prefix!");
				return true;
			}
			if(args.length == 0){
				//If command is just "/prefix", clear the players prefix
				if(playerName.hasPrefix()) {//But check if player even has a prefix first
					playerName.setPrefix("");
					sender.sendMessage(ChatColor.BLUE + "Your prefix has been removed!");
				} else {
					//Message for player if he doesn't have a prefix to begin with
					sender.sendMessage(ChatColor.RED + "You don't have a prefix to remove!");
					return true;
				}
			} else {
				//If prefix command has arguments after it
				String userPrefix = "";
				for(int i = 0; i < args.length; i++){ //Combine arguments into one string
					userPrefix += " " + args[i];
				}
				userPrefix = userPrefix.substring(1);
				playerName.setPrefix(userPrefix);
				sender.sendMessage(ChatColor.BLUE + "Your prefix has been updated!");
			}
			sender.sendMessage(ChatColor.BLUE + "Your name now looks like: " + ChatColor.RESET + playerName.getPreview());
		}

		else if(commandLabel.equalsIgnoreCase("suffix")) { //same process as prefix, only using suffix methods.
			if(!sender.hasPermission("affixer.suffix")) {
				sender.sendMessage(ChatColor.RED + "You must be a donator or subscriber to set your suffix!");
				return true;
			}
			if(args.length == 0) {
				if(playerName.hasSuffix()) {
					playerName.setSuffix("");
					sender.sendMessage(ChatColor.BLUE + "Your suffix has been removed!");
				} else {
					sender.sendMessage(ChatColor.RED + "You don't have a suffix to remove!");
					return true;
				}
			} else {
				String userSuffix = "";
				for(int i = 0; i < args.length; i++) {
					userSuffix += " " + args[i];
				}
				userSuffix = userSuffix.substring(1);

				playerName.setSuffix(userSuffix);
				sender.sendMessage(ChatColor.BLUE + "Your suffix has been updated!");
			}
			sender.sendMessage(ChatColor.BLUE + "Your name now looks like: " + ChatColor.RESET + playerName.getPreview());
		}

		else if(commandLabel.equalsIgnoreCase("color") && args.length == 2) { //shortcut command: "/color [prefix/name/suffix] [colorName]"

			String whatToEdit = args[0].toLowerCase(); //arg0 will contain what to edit (prefix, name, suffix);
			String color = args[1].toLowerCase(); //arg1 will contain the colorname
			String colorCode = colorConvert(color); //this converts the colorname into the correct colorcode

			if(colorCode == "format") { //if colorConvert returns with the string "format".
				sender.sendMessage(ChatColor.BLUE + "Sorry, formats are currently not supported with the shortcut command.");
				return true;
			}
			if(colorCode == "false") { //if colorConvert returns false, it didn't find a match.
				sender.sendMessage(ChatColor.BLUE + "That's not a reconginzed color. See /colornames");
				return true;
			} else {

				switch(whatToEdit) {

				case "prefix": //if player specified prefix
					if(!sender.hasPermission("affixer.prefix")) {
						sender.sendMessage(ChatColor.RED + "You must be a donator or subscriber to set your prefix!");
						break;
					}
					String newPrefix;
					if(playerName.getPrefix().length() < 2) { //if players current prefix is less than 2 characters, then it cant have a colorcode needing to be replaced
						newPrefix = colorCode + playerName.getPrefix();
					}
					else if(playerName.getPrefix().length() >= 2 && playerName.getPrefix().substring(0, 2).matches("&[a-fA-F0-9]")) { //if player current prefix is two or longer, check first two characters for a colorcode
						newPrefix = playerName.getPrefix().replaceFirst("&[a-fA-F0-9]", colorCode); //replace the first colorcode
					} else {
						newPrefix = colorCode + playerName.getPrefix();
					}
					playerName.setPrefix(newPrefix);
					sender.sendMessage(ChatColor.BLUE + "Your name now looks like: " + ChatColor.RESET + playerName.getPreview());
					break;

				case "name": //if player specified name
					if(!sender.hasPermission("affixer.prefix")) {
						sender.sendMessage(ChatColor.RED + "You must be a donator or subscriber to set your prefix!");
						break;
					}
					if(playerName.getPrefix().length() >= 4) {
						/* To change the name color, it's actually placing a color code at the end of the prefix.
						 * Therefore, (if needed) we want to replace the last color code and not any others.
						 * If the current player prefix is 4 chars or greater, that means it has the possibility of containing two color codes.
						 * So we will cut out the first two characters, assuming they are the prefix color code.
						 * Then check the rest of the string for a color code.
						 */
						int l = playerName.getPrefix().length();
						String removeFront = playerName.getPrefix().substring(2, l);
						String regex = "&[a-fA-F0-9]";
						Matcher matcher = Pattern.compile(regex).matcher(removeFront);
						if(matcher.find()) {
							String nameColor = playerName.getPrefix().replaceFirst("(?s)"+"&[a-fA-f0-9]"+"(?!.*?"+"&[a-fA-f0-9]"+")", colorCode);
							playerName.setPrefix(nameColor);
						} else {
							/* If we don't find any color codes to replace in players current prefix, we must add in new colorcode at the end
							 * However, format codes will break if a color code is placed AFTER them.
							 * So we must check the string for a format code, and place the color code BEFORE it.
							 */
							String nameColor = insertColorCode(playerName.getPrefix(), colorCode);
							playerName.setPrefix(nameColor);
						}
					} else {  //if current prefix is less than 4
						playerName.setPrefix(playerName.getPrefix() + colorCode);
					}
					sender.sendMessage(ChatColor.BLUE + "Your name now looks like: " + ChatColor.RESET + playerName.getPreview());
					break;

				case "suffix": //if player specified suffix
					if(!sender.hasPermission("affixer.suffix")) {
						sender.sendMessage(ChatColor.RED + "You must be a donator or subscriber to set your prefix!");
						break;
					}
					String newSuffix;
					if(playerName.getSuffix().length() < 2) { //if current suffix is less than 2, add the colorcode at beginning
						newSuffix = colorCode + playerName.getSuffix();
					}
					else if(playerName.getSuffix().length() >= 2 && playerName.getSuffix().substring(0, 2).matches("&[a-fA-F0-9]")) { //if suffix is greaterthan or equalto 2, check first two for a colorcode.
						newSuffix = playerName.getSuffix().replaceFirst("&[a-fA-F0-9]", colorCode);
					} else {  //if no color code is found, add new one to front
						newSuffix = colorCode + playerName.getSuffix();
					}
					playerName.setSuffix(newSuffix);
					sender.sendMessage(ChatColor.BLUE + "Your name now looks like: " + ChatColor.RESET + playerName.getPreview());
					break;
				default:
					return false;
				}
			}
		}
		return true;
	}

	public static void helpMenu(CommandSender player) { //Function for sending help menu. Checking permissions to dictate what to display to player
		if(!player.hasPermission("affixer.colors") && !player.hasPermission("affixer.prefix") && !player.hasPermission("affixer.suffix") && !player.hasPermission("affixer.colornames")) {
			player.sendMessage(ChatColor.RED + "You do not have permissions to any commands for Affixer!");
			player.sendMessage(ChatColor.RED + "Remember: You must be a donator or subscriber to set your prefix/suffix.");
			return;
		}

		player.sendMessage(ChatColor.BLUE + "Affixer Help:");

		if(player.hasPermission("affixer.colors")) {
			player.sendMessage(ChatColor.GOLD + "/colors" + ChatColor.WHITE + "  - Displays all color and format text codes.");
		}
		if(player.hasPermission("affixer.colornames")) {
			player.sendMessage(ChatColor.GOLD + "/colornames" + ChatColor.WHITE + "  - Displays all color names in color.");
		}
		if(player.hasPermission("affixer.prefix")) {
			player.sendMessage(ChatColor.GOLD + "/prefix [prefix]" + ChatColor.WHITE + " - Changes your prefix." + ChatColor.ITALIC + " Ex: /prefix &4Dr. &9" + ChatColor.RESET + "  (Leave blank to clear current prefix)");
		}
		if(player.hasPermission("affixer.suffix")) {
			player.sendMessage(ChatColor.GOLD + "/suffix [suffix]" + ChatColor.WHITE + " - Changes your suffix." + ChatColor.ITALIC + " Ex: /suffix is cool" + ChatColor.RESET + "   (Leave blank to clear current suffix)");
		}
		if(player.hasPermission("affixer.prefix") && player.hasPermission("affixer.suffix")) {
			player.sendMessage(ChatColor.GOLD + "/color [prefix/name/suffix] [colorname]" + ChatColor.WHITE + "  - Shortcut to quickly change colors.");
		}
		player.sendMessage(ChatColor.GOLD + "/Affixer help" + ChatColor.WHITE + "  - Shows this help menu.");
		player.sendMessage(ChatColor.GOLD + "Current full name: " + ChatColor.RESET + new PlayerName((Player) player).getPreview());
	}


	public static String colorConvert(String color)	{ //function used to convert plain english colors into corresponding color codes
		switch(color) {
		case "black":
			color = "&0";
			break;
		case "darkblue":
			color = "&1";
			break;
		case "darkgreen":
			color = "&2";
			break;
		case "darkaqua":
			color = "&3";
			break;
		case "darkred":
			color = "&4";
			break;
		case "purple":
			color = "&5";
			break;
		case "gold":
			color = "&6";
		case "orange":
			color = "&6";
			break;
		case "gray":
			color = "&7";
			break;
		case "darkgray":
			color = "&8";
			break;
		case "blue":
			color = "&9";
			break;
		case "green":
			color = "&a";
			break;
		case "aqua":
			color = "&b";
			break;
		case "red":
			color = "&c";
			break;
		case "pink":
			color = "&d";
			break;
		case "yellow":
			color = "&e";
			break;
		case "white":
			color = "&f";
			break;
		case "none":
			color = "&f";
			break;
		case "clear":
			color = "&f";
			break;
		case "remove":
			color = "&f";
			break;
		case "delete":
			color = "&f";
			break;
		case "bold":
			color = "format";
			break;
		case "strikethrough":
			color = "format";
			break;
		case "underline":
			color = "format";
			break;
		case "italic":
			color = "format";
			break;
		case "reset":
			color = "format";
			break;
		default:
			color = "false";
			break;
		}
		return color;
	}

	public static String insertColorCode(String str2Check, String colorcode) { //function used to check for format codes. If it finds one, it places color code before it.
		Pattern checkRegex = Pattern.compile("&[l-oL-ORrKk]");
		Matcher regexMatcher = checkRegex.matcher(str2Check);
		if(regexMatcher.find()) {
			if(regexMatcher.group().length() <= 2){
				int index1 = regexMatcher.start();
				int indexEnd = str2Check.length();
				String addCode = str2Check.substring(0, index1) + colorcode + str2Check.substring(index1, indexEnd);
				return addCode;
			} else {
				String addCode = str2Check + colorcode;
				return addCode;
			}
		}
		String addCode = str2Check + colorcode;
		return addCode;
	}

}

