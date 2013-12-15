package com.mythicacraft.plugins.mythsentials.CmdExecutors;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.mythicacraft.plugins.mythsentials.Tools.Paginate;


public class HelpMenu implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {

		if(commandLabel.equalsIgnoreCase("mythica")) {

			Paginate paginator = new Paginate();
			paginator.setHeader("Mythica's Command Help Menu");
			paginator.setPaginateString(playerMenu(sender));
			paginator.setFooter(commandLabel + " NEXTPAGE");

			if(args.length == 0) {
				paginator.sendPage(1, sender);
			} else if(args.length > 0) {
				if(args[0].equalsIgnoreCase("mod") || args[0].equalsIgnoreCase("staff")) {
					if(sender.hasPermission("mythica.mod")){

						Paginate staffPaginator = new Paginate();
						staffPaginator.setHeader("Staff's Command Help Menu");
						staffPaginator.setPaginateString(staffMenu());
						staffPaginator.setFooter(commandLabel + " " + args[0] + " NEXTPAGE");

						if(args.length == 1) {
							staffPaginator.sendPage(1, sender);
						}
						else {
							try {
								int pageNumber = Integer.parseInt(args[1]);
								if(pageNumber <= staffPaginator.pageTotal()) {
									staffPaginator.sendPage(pageNumber, sender);
								} else {
									sender.sendMessage(ChatColor.RED + "Not a valid page number!");
								}
							} catch (Exception e) {
								sender.sendMessage(ChatColor.RED + "Not a valid page number!");
							}
						}
					}
				}
				else if(args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("?")) {
					paginator.setFooter(commandLabel + " " + args[0] +" NEXTPAGE");
					if(args.length == 1) {
						paginator.sendPage(1, sender);
					} else {
						try {
							int pageNumber = Integer.parseInt(args[1]);
							if(pageNumber <= paginator.pageTotal()) {
								paginator.sendPage(pageNumber, sender);
							} else {
								sender.sendMessage(ChatColor.RED + "Not a valid page number!");
							}
						} catch (Exception e) {
							sender.sendMessage(ChatColor.RED + "Not a valid page number!");
						}
					}
				}
				else {
					try {
						int pageNumber = Integer.parseInt(args[0]);
						if(pageNumber <= paginator.pageTotal()) {
							paginator.sendPage(pageNumber, sender);
						} else {
							sender.sendMessage(ChatColor.RED + "Not a valid page number!");
						}
					} catch (Exception e) {
						sender.sendMessage(ChatColor.RED + "Not a valid page number!");
					}
				}
			}
		}
		else if(commandLabel.equalsIgnoreCase("spirebot")) {
			if(args.length == 0) {
				sender.sendMessage(ChatColor.GREEN + "Type one of these in chat and please don't spam:");
				sender.sendMessage(ChatColor.AQUA + "?website" + ChatColor.WHITE + ", " + ChatColor.AQUA + "?map" + ChatColor.WHITE + ", " + ChatColor.AQUA + "?east" + ChatColor.WHITE + ", " + ChatColor.AQUA + "?west" + ChatColor.WHITE + ", " + ChatColor.AQUA + "?north" + ChatColor.WHITE + ", " + ChatColor.AQUA + "?south" + ChatColor.WHITE + ", " + ChatColor.AQUA + "?ground" + ChatColor.WHITE + ", " + ChatColor.AQUA + "?mainnether" + ChatColor.WHITE + ", " + ChatColor.AQUA + "?tempnether" + ChatColor.WHITE + ", " + ChatColor.AQUA + "?irc" + ChatColor.WHITE + ", " + ChatColor.AQUA + "?mumble" + ChatColor.WHITE + ", " + ChatColor.AQUA + "?social" + ChatColor.WHITE);
			}
			if(args.length == 1) {
				if(args[0].equalsIgnoreCase("mod") || args[0].equalsIgnoreCase("staff")) {
					if(sender.hasPermission("mythica.mod")) {
						sender.sendMessage(ChatColor.GREEN + "Type one of these in chat:");
						sender.sendMessage(ChatColor.AQUA + "?website" + ChatColor.WHITE + ", " + ChatColor.AQUA + "?rules" + ChatColor.WHITE + ", " + ChatColor.AQUA + "?map" + ChatColor.WHITE + ", " + ChatColor.AQUA + "?stuck" + ChatColor.WHITE + ", " + ChatColor.AQUA + "?tp" + ChatColor.WHITE + ", " + ChatColor.AQUA + "?help" + ChatColor.WHITE + ", " + ChatColor.AQUA + "?money" + ChatColor.WHITE + ", " + ChatColor.AQUA + "?colors" + ChatColor.WHITE + ", " + ChatColor.AQUA + "?prefix" + ChatColor.WHITE + ", " + ChatColor.AQUA + "?suffix" + ChatColor.WHITE + ", " + ChatColor.AQUA + "?donate" + ChatColor.WHITE + ", " + ChatColor.AQUA + "?donator" + ChatColor.WHITE + ", " + ChatColor.AQUA + "?subscriber" + ChatColor.WHITE + ", " + ChatColor.AQUA + "?register" + ChatColor.WHITE + ", " + ChatColor.AQUA + "?reg" + ChatColor.WHITE + ", " +ChatColor.AQUA + "?spam" + ChatColor.WHITE + ", " +  ChatColor.AQUA + "?res" + ChatColor.WHITE + ", " + ChatColor.AQUA + "?pay" + ChatColor.WHITE + ", " + ChatColor.AQUA + "?vote" + ChatColor.WHITE + ", " + ChatColor.AQUA + "?social" + ChatColor.WHITE + ", " + ChatColor.AQUA + "?grief" + ChatColor.WHITE + ", " + ChatColor.AQUA + "?east/west/north/south/ground/mainnether/tempnether/tempworld" + ChatColor.WHITE + ", " + ChatColor.AQUA + "?suicide" + ChatColor.WHITE + ", " + ChatColor.AQUA + "?privatechat" + ChatColor.WHITE + ", " + ChatColor.AQUA + "?issue" + ChatColor.WHITE + ", " + ChatColor.AQUA + "?flags" + ChatColor.WHITE + ", " + ChatColor.AQUA + "?wiki" + ChatColor.WHITE + ", " + ChatColor.AQUA + "?ip" + ChatColor.WHITE + ", " + ChatColor.AQUA + "?mail" + ChatColor.WHITE + ", " + ChatColor.AQUA + "?irc" + ChatColor.WHITE + ", " + ChatColor.AQUA + "?mumble" + ChatColor.WHITE + ", " + ChatColor.AQUA + "?subzones" + ChatColor.WHITE);
					}
				}
			}
		}
		return true;
	}
	public static String playerMenu(CommandSender player) {
		StringBuilder sb = new StringBuilder();
		sb.append(ChatColor.GREEN + "General Commands:\n");
		if(player.hasPermission("mythica.registered")) {
			sb.append(ChatColor.AQUA + "/mods" + ChatColor.GRAY + " - Tells you if any staff members are online.\n");
		}
		if(player.hasPermission("mythica.sendhelp")) {
			sb.append(ChatColor.AQUA + "/helpme [reason]" + ChatColor.GRAY + " - Alerts staff that you need help.\n");
		}
		sb.append(ChatColor.AQUA + "/rules" + ChatColor.GRAY + " - See the rules of Mythica.\n");
		sb.append(ChatColor.AQUA + "/dragon" + ChatColor.GRAY + " - Check if the dragon is alive or not.\n");
		sb.append(ChatColor.AQUA + "/recipe [ItemName]" + ChatColor.GRAY + " - See the crafting recipe for an item.\n");
		sb.append(ChatColor.AQUA + "/mail send [PlayerName] [message]" + ChatColor.GRAY + " - Send mail to a player.\n");
		sb.append(ChatColor.AQUA + "/pvp" + ChatColor.GRAY + " - Toggle your PvP status in survival worlds.\n");
		sb.append(ChatColor.AQUA + "/money" + ChatColor.GRAY + " - See your in-game cash.\n");
		sb.append(ChatColor.AQUA + "/pay [PlayerName] [Amount]" + ChatColor.GRAY + " - Send someone money.\n");
		sb.append(ChatColor.AQUA + "/spirebot" + ChatColor.GRAY + " - See a list of triggers you can say to Spirebot.\n");
		sb.append(ChatColor.AQUA + "/pet info" + ChatColor.GRAY + " - Shows info about a pet you select.\n");
		sb.append(ChatColor.AQUA + "/pet give [PlayerName]" + ChatColor.GRAY + " - Gives a pet you select to the given player.\n");
		sb.append(ChatColor.AQUA + "/irc users" + ChatColor.GRAY + " - Shows users connected to Mythica IRC.\n");
		sb.append(ChatColor.AQUA + "/lb tool" + ChatColor.GRAY + " - Get a Wooden Pickaxe to use LogBlock.\n");
		sb.append(ChatColor.AQUA + "/compass ?" + ChatColor.GRAY + " - Shows compass commands help menu.\n");
		sb.append(ChatColor.AQUA + "/horse ?" + ChatColor.GRAY + " - Shows horse commands help menu.\n");
		sb.append(ChatColor.AQUA + "/lwc ?" + ChatColor.GRAY + " - Shows LWC commands help menu.\n");
		sb.append(ChatColor.AQUA + "/mcmmo ?" + ChatColor.GRAY + " - Shows McMMO commands help menu.\n");
		sb.append(ChatColor.GREEN + "Residence Commands:\n");
		sb.append(ChatColor.AQUA + "/res select max" + ChatColor.GRAY + " - Makes a selection at your max res size.\n");
		if(player.hasPermission("mythica.restool")) {
			sb.append(ChatColor.AQUA + "/res tool" + ChatColor.GRAY + " - Puts the res selection tool in your hand.\n");
		}
		sb.append(ChatColor.AQUA + "/res unstuck" + ChatColor.GRAY + " - Bounce out of someone's Res.\n");
		sb.append(ChatColor.AQUA + "/resextras list" + ChatColor.GRAY + " - Lists additional flags available for Res.\n");
		sb.append(ChatColor.AQUA + "/res ?" + ChatColor.GRAY + " - Shows Residence commands help menu.\n");
		sb.append(ChatColor.GREEN + "Chat Commands:\n");
		sb.append(ChatColor.AQUA + "/ch g" + ChatColor.GRAY + " - Switch to Global chat.\n");
		sb.append(ChatColor.AQUA + "/ch l" + ChatColor.GRAY + " - Switch to Local chat.\n");
		sb.append(ChatColor.AQUA + "/tell [PlayerName]" + ChatColor.GRAY + " - Send private message.\n");
		sb.append(ChatColor.AQUA + "/r [message]" + ChatColor.GRAY + " - Reply to private message.\n");
		sb.append(ChatColor.AQUA + "/colors" + ChatColor.GRAY + " - See the chat color codes.\n");
		sb.append(ChatColor.GREEN + "Donator Commands:\n");
		sb.append(ChatColor.AQUA + "/affixer ?" + ChatColor.GRAY + " - See Prefix/Suffix command help menu.\n");
		sb.append(ChatColor.GREEN + "Subscriber Commands:\n");
		sb.append(ChatColor.AQUA + "/edit [Line#] [text]" + ChatColor.GRAY + " - Type while looking at a sign to change text.\n");
		return sb.toString();
	}
	public static String staffMenu() {
		StringBuilder sb = new StringBuilder();
		sb.append(ChatColor.GREEN + "General Commands:\n");
		sb.append(ChatColor.AQUA + "/spirebot staff" + ChatColor.GRAY + " - See full list of Spirebots ?Commands\n");
		sb.append(ChatColor.AQUA + "/kick [player] [reason]" + ChatColor.GRAY + " - Kicks the given player.\n");
		sb.append(ChatColor.AQUA + "/mute [player]" + ChatColor.GRAY + " - Mutes the given player from any and all chat.\n");
		sb.append(ChatColor.AQUA + "/ban [player] [reason]" + ChatColor.GRAY + " - Bans the given player.\n");
		sb.append(ChatColor.AQUA + "/playerinfo [PlayerName]" + ChatColor.GRAY + " - Shows a bunch of info about a player.\n");
		sb.append(ChatColor.AQUA + "/loginlocation [PlayerName]" + ChatColor.GRAY + " - Sets the location the player will spawn at on next login.\n");
		sb.append(ChatColor.AQUA + "/deathdrops [PlayerName]" + ChatColor.GRAY + " - Shows a list of a players deaths.\n");
		sb.append(ChatColor.AQUA + "/deathdrops [PlayerName] [#FromList]" + ChatColor.GRAY + " - See specific info about a players death and their drops.\n");
		sb.append(ChatColor.AQUA + "/deathdrops [PlayerName] [setmyinv/setplayerinv] [#FromList]" + ChatColor.GRAY + " - Set yours or the players inventory to the given deathdrop number.\n");
		sb.append(ChatColor.AQUA + "/ch m" + ChatColor.GRAY + " - Join the Mod Chat channel.\n");
		sb.append(ChatColor.AQUA + "/ch sc" + ChatColor.GRAY + " - Join the SpawnChat channel.\n");
		sb.append(ChatColor.AQUA + "/lb toolblock" + ChatColor.GRAY + " - Gives you a bedrock.\n");
		sb.append(ChatColor.GREEN + "Vanish Commands:\n");
		sb.append(ChatColor.AQUA + "/v" + ChatColor.GRAY + " - Disappear from other players.\n");
		sb.append(ChatColor.AQUA + "/v check" + ChatColor.GRAY + " - Check if you are currently invisibl\n");
		sb.append(ChatColor.AQUA + "/v toggle [AttributeName]" + ChatColor.GRAY + " - Toggle certain attributes.\n");
		sb.append(ChatColor.GREEN + "Teleportation Commands:\n");
		sb.append(ChatColor.AQUA + "/tp [player]" + ChatColor.GRAY + " - Teleport to the given player.\n");
		sb.append(ChatColor.AQUA + "/tp [x] [y] [z]" + ChatColor.GRAY + " - Teleport to a coordinates.\n");
		sb.append(ChatColor.AQUA + "/tp here [player]" + ChatColor.GRAY + " - Teleport given player to you.\n");
		sb.append(ChatColor.AQUA + "/tp [player] to [player]" + ChatColor.GRAY + " - Teleport a player to another player.\n");
		sb.append(ChatColor.AQUA + "/tp [player] to [x] [y] [z]" + ChatColor.GRAY + " - Teleport a player to a coordinates.\n");
		sb.append(ChatColor.AQUA + "/tp tool" + ChatColor.GRAY + " - Gives you a compass for special teleporting.\n");
		sb.append(ChatColor.AQUA + "/back" + ChatColor.GRAY + " - Return to previous point before teleporting.\n");
		return sb.toString();
	}
}