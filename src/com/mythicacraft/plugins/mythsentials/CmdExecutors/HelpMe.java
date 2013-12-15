package com.mythicacraft.plugins.mythsentials.CmdExecutors;

import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mythicacraft.plugins.mythsentials.Mythsentials;
import com.mythicacraft.plugins.mythsentials.Tools.Utils;
import com.mythicacraft.plugins.mythsentials.jsonapi.NotificationStreamMessage;

public class HelpMe implements CommandExecutor {

	private static final Logger log = Logger.getLogger("Minecraft");

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {

		//Command for checking online status of staff
		if(commandLabel.equalsIgnoreCase("mods") || commandLabel.equalsIgnoreCase("admins")) {
			Utils.modMessage((Player) sender);
		}
		//Command for sending help alert to staff
		else if(commandLabel.equalsIgnoreCase("helpme") || commandLabel.equalsIgnoreCase("modhelp") || commandLabel.equalsIgnoreCase("adminhelp") || commandLabel.equalsIgnoreCase("mod") || commandLabel.equalsIgnoreCase("admin")) {

			//Permission check
			if(sender.hasPermission("mythica.helpsend")) {
				String message = " needs help!";
				//If sender left reason for help, combine arguments into string
				if (args.length >= 1) {
					//If first arg is "?" or "help", show help menu.
					if(args[0].equalsIgnoreCase("?") || args[0].equalsIgnoreCase("help")) {
						sender.sendMessage(ChatColor.RED + "-------------[HelpMe]-------------" + ChatColor.GOLD + "\n/helpme [reason]" + ChatColor.WHITE + " - Alerts mods/admins\nthat you need help." + ChatColor.ITALIC + "\nNote: You can leave [reason] blank.");
						return true;
					} else {
						String reason = "";
						for(int i = 0; i < args.length; i++){
							reason += " " + args[i];
						}
						reason = reason.substring(1);
						message += " Reason: " + reason;
					}
				}

				//Send help alert to staff and JSONAPI stream
				Utils.playerNotify("mythica.helpreceive",  ChatColor.RED + "[HelpMe] " + ChatColor.YELLOW + sender.getName() + ChatColor.GOLD  + message);
				Mythsentials.notificationStream.addMessage(new NotificationStreamMessage("helpme", sender.getName(), message));

				//Alert player if mods received alert or not
				if(Utils.modsOnline()) {
					sender.sendMessage(ChatColor.GOLD + "Available staff have been notified and will help you soon!");
				} else {
					sender.sendMessage(ChatColor.GOLD + "Unfortunately there isn't any staff available at the moment. You can make an issue on our website and our staff will review it as soon as they get on.");
					//Severe log to console so notification goes to ebiggz phone
					log.severe(sender.getName() + " needs help! (No Mods online!)");
				}
			} else {
				sender.sendMessage(ChatColor.RED + "You don't have permission to send help alerts.");
			}
		}
		return true;
	}
}


