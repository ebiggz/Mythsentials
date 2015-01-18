package com.mythicacraft.plugins.mythsentials.Friends;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mythicacraft.plugins.mythsentials.Mythian;
import com.mythicacraft.plugins.mythsentials.Mythsentials;
import com.mythicacraft.plugins.mythsentials.GUIAPI.GUIManager;
import com.mythicacraft.plugins.mythsentials.Utilities.FancyMenu;


public class FriendsCmds implements CommandExecutor {

	String[] commandData = {
			"&7&o(Hover over a &a&ocommand &7&ofor info, click to run it)",
			"TELLRAW run>>/friends>>/friends>>See your friends list.",
			"TELLRAW run>>/friends help>>/friends help>>See this help menu.",
			"TELLRAW run>>/friends requests>>/friends requests>>Toggle enabling/disabling of friend requests.",
			"TELLRAW suggest>>/friends add [player]>>/friends add >>Send a friend request",
			"TELLRAW suggest>>/friends remove [player]>>/friends remove >>Remove a player as a friend"
	};

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {

		if(commandLabel.equalsIgnoreCase("friends")) {
			if(args.length == 0) {
				Player p = (Player) sender;
				GUIManager.getInstance().showGUI(new FriendsGUI(), p);
				//show friends list
			}
			else if(args.length > 0) {
				if(args[0].equalsIgnoreCase("help")) {
					FancyMenu.showClickableCommandList(sender, commandLabel, "Friends Commands", commandData, 1);
					//show help menu
				}
				else if(args[0].equalsIgnoreCase("accept")) {
					if(Mythsentials.friendRequests.containsKey((Player) sender)) {
						Player requestorP = Mythsentials.friendRequests.get((Player) sender);
						if(requestorP.isOnline()) {
							Mythian accepter = Mythsentials.getMythianManager().getMythian(sender.getName());
							Mythian requester = Mythsentials.getMythianManager().getMythian(requestorP.getName());
							accepter.addFriend(requestorP.getName());
							requester.addFriend(sender.getName());
							Player senderPlayer = (Player) sender;
							if(!requestorP.canSee(senderPlayer)) {
								requestorP.showPlayer(senderPlayer);
							}
							if(!senderPlayer.canSee(requestorP)) {
								senderPlayer.showPlayer(requestorP);
							}
							requestorP.sendMessage(ChatColor.GREEN + "[Mythica] You are now friends with " + ChatColor.YELLOW + sender.getName());
							sender.sendMessage(ChatColor.GREEN + "[Mythica] You are now friends with " + ChatColor.YELLOW + requestorP.getName());
						} else {
							sender.sendMessage(ChatColor.RED + "An error occurred attempting to add friend. Did they go offline?");
						}
						Mythsentials.friendRequests.remove((Player) sender);
					}
				}
				if(args[0].equalsIgnoreCase("deny")) {
					if(Mythsentials.friendRequests.containsKey((Player) sender)) {
						Player requestorP = Mythsentials.friendRequests.get((Player) sender);
						sender.sendMessage(ChatColor.GREEN + "[Mythica] You denied the friend request from " + ChatColor.YELLOW + requestorP.getName());
						Mythsentials.friendRequests.remove((Player) sender);
					}
				}
				else if(args[0].equalsIgnoreCase("requests")) {
					Mythian mythian = Mythsentials.getMythianManager().getMythian(sender.getName());
					if(mythian.isAcceptingFriendRequests()) {
						mythian.setAcceptFriendRequests(false);
						sender.sendMessage(ChatColor.GREEN + "[Mythica] Disabled friend requests!");
						Player p = (Player) sender;
						if(Mythsentials.friendRequests.containsKey(p)) {
							Mythsentials.friendRequests.remove(p);
						}
					} else {
						mythian.setAcceptFriendRequests(true);
						sender.sendMessage(ChatColor.GREEN + "[Mythica] Enabled friend requests!");
					}
				}
				else if(args[0].equalsIgnoreCase("add")) {
					if(args.length == 1) {
						sender.sendMessage(ChatColor.RED + "[Mythica] You must specify a player's name! /friends add [player]");
					}
					else {
						final Player newFriend = Bukkit.getPlayerExact(args[1]);
						if(newFriend != null && newFriend.isOnline()) {
							Mythian newMythian = Mythsentials.getMythianManager().getMythian(args[1]);
							if(newMythian.isFriendsWith(sender.getName())) {
								sender.sendMessage(ChatColor.RED + "[Mythica] You are already friends with that player!");
							}
							else if(sender.getName().equals(args[1])) {
								sender.sendMessage(ChatColor.RED + "[Mythica] You can't be friends with yourself...");
							}
							else if(!newMythian.isAcceptingFriendRequests()) {
								sender.sendMessage(ChatColor.RED + "[Mythica] This player is not taking friend requests at this time.");
							}
							else if(Mythsentials.friendRequests.containsKey(newFriend) && Mythsentials.friendRequests.get(newFriend) == (Player) sender) {
								sender.sendMessage(ChatColor.RED + "[Mythica] You've already sent a friend request to this player recently!");
							}
							else {
								sender.sendMessage(ChatColor.GREEN + "[Mythica] Sent a friend request to " + ChatColor.YELLOW + args[1]);
								Mythsentials.friendRequests.put(newFriend, (Player) sender);
								String command = "tellraw %player% {\"text\":\"\",\"extra\":[{\"text\":\"[Mythica] \",\"color\":\"green\"},{\"text\":\"%requestor%\",\"color\":\"yellow\"},{\"text\":\" wants to be friends! \"},{\"text\":\"Click one: \",\"color\":\"gray\"},{\"text\":\"Accept\",\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/friends accept\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"Click to accept request.\"}}},{\"text\":\", \"},{\"text\":\"Deny\",\"color\":\"red\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/friends deny\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"Click to deny request.\"}}},{\"text\":\", or \"},{\"text\":\"Disable Requests\",\"color\":\"gold\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/friends requests\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"Click to disable friend requests\"}}}]}";
								command = command.replace("%player%", newFriend.getName()).replace("%requestor%", sender.getName());
								Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), command);
								Bukkit.getScheduler().scheduleSyncDelayedTask(Mythsentials.getPlugin(), new Runnable() {
									@Override
									public void run() {
										if(Mythsentials.friendRequests.containsKey(newFriend)) {
											Mythsentials.friendRequests.remove(newFriend);
											newFriend.sendMessage(ChatColor.GREEN + "[Mythica] Friend request timed out.");
										}
									}
								}, 600L);
							}
						} else {
							sender.sendMessage(ChatColor.RED + "[Mythica] You can only send friend requests to online players.");
						}
						//send friend request
					}
				}
				else if(args[0].equalsIgnoreCase("remove")) {
					if(args.length == 1) {
						sender.sendMessage(ChatColor.RED + "[Mythica] You must specify a player's name! /friends remove [player]");
					}
					else {
						Mythian mythian = Mythsentials.getMythianManager().getMythian(sender.getName());
						if(mythian.isFriendsWith(args[1])) {
							mythian.removeFriend(args[1]);
							Mythsentials.getMythianManager().getMythian(args[1]).removeFriend(sender.getName());
							sender.sendMessage(ChatColor.GREEN + "[Mythica] You are no longer friends with " + ChatColor.YELLOW + args[1]);
						} else {
							sender.sendMessage(ChatColor.RED + "[Mythica] That player is not on your friends list.");
						}
					}
				}
			}
		}
		return true;
	}
}
