package com.gmail.ebiggz.plugins.mythsentials.CmdExecutors;

import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.ebiggz.plugins.mythsentials.Mythsentials;
import com.gmail.ebiggz.plugins.mythsentials.Tools.Utils;

public class HelpMe implements CommandExecutor {

	private static final Logger log = Logger.getLogger("Minecraft");


	public static Mythsentials plugin;

	public HelpMe(Mythsentials plugin) {
		HelpMe.plugin = plugin;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {

		if(commandLabel.equalsIgnoreCase("mods") || commandLabel.equalsIgnoreCase("admins")) {
			Utils.modMessage((Player) sender);
			return true;
		}

		if(commandLabel.equalsIgnoreCase("helpme") || commandLabel.equalsIgnoreCase("modhelp") || commandLabel.equalsIgnoreCase("adminhelp") || commandLabel.equalsIgnoreCase("mod") || commandLabel.equalsIgnoreCase("admin")) {

			if(!sender.hasPermission("mythica.helpsend")) {
				sender.sendMessage(ChatColor.RED + "Sorry, you don't have permission to send help alerts.");
				return true;
			}

			String userName = sender.getName();
			Boolean mods = Utils.modsOnline();
			String message = ChatColor.RED + "[HelpMe] " + ChatColor.YELLOW + userName + ChatColor.GOLD + " needs help!";

			if(mods == false) {
				sender.sendMessage(ChatColor.GOLD + "We're sorry, unfortunately there isn't any moderators on at the moment. You can make an issue on our website and our staff will review it as soon as they get on.");
				log.severe(userName + " needs help! (No Mods online!)");
				return true;
			}

			if(args.length == 0) {
				Utils.playerNotify("mythica.helpreceive", message);
				log.severe(userName + " needs help! (Mods online)");
				sender.sendMessage(ChatColor.GOLD + "Moderators have been notified and will help you soon!");
				return true;
			}

			if(args.length >= 1) {
				if(args[0].equalsIgnoreCase("?") || args[0].equalsIgnoreCase("help")) {
					sender.sendMessage(ChatColor.RED + "-------------[HelpMe]-------------" + ChatColor.GOLD + "\n/helpme [reason]" + ChatColor.WHITE + " - Alerts mods/admins\nthat you need help." + ChatColor.ITALIC + "\nNote: You can leave [reason] blank.");
					return true;
				}

				String reason = "";
				for(int i = 0; i < args.length; i++){
					reason += " " + args[i];
				}
				reason = reason.substring(1);

				message = message + " Reason: " + reason;
				Utils.playerNotify("mythica.helpreceive", message);
				sender.sendMessage(ChatColor.GOLD + "Moderators have been notified and will help you soon!");
				log.severe("[HelpMe] " + userName + ": " + reason + "(Mods online)");
				return true;
			}
		}
		return false;
	}
}


