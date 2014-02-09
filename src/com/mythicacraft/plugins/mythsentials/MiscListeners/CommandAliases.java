package com.mythicacraft.plugins.mythsentials.MiscListeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

import com.mythicacraft.plugins.mythsentials.Mythsentials;

public class CommandAliases implements Listener {

	public Mythsentials plugin;

	public CommandAliases(Mythsentials plugin) {
		this.plugin = plugin;
	}

	@EventHandler(priority= EventPriority.HIGHEST)
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {

		String command = event.getMessage();
		final Player p = event.getPlayer();

		if(command.contains("/money")) {
			String newCommand = command.replace("/money", "/econ");
			event.setMessage(newCommand);
		}

		else if(command.contains("/tell")) {
			String newCommand = command.replace("/tell", "/msg");
			event.setMessage(newCommand);
		}

		else if(command.equalsIgnoreCase("/res tool")) {
			String newCommand = "/restool";
			event.setMessage(newCommand);
		}

		else if(command.equalsIgnoreCase("/res select max")) {
			String newCommand = "/resmax";
			event.setMessage(newCommand);
		}

		else if(command.equalsIgnoreCase("/sync")) {
			String newCommand = "/cbsync";
			event.setMessage(newCommand);
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
				public void run() {
					PermissionUser user = PermissionsEx.getUser(p);
					String[] groups = user.getGroupsNames();
					for(int i = 0; i < groups.length; i++) {
						if(groups[i].equalsIgnoreCase("Unregistered")) {
							p.sendMessage(ChatColor.YELLOW + "Whoops! You sync'd to the forums but you did not get promoted. If you registered from the website, a common cause of this is because your Minecraft name was typed incorrectly during registration. You can fix this by clicking \"Profile\" at the top of the forum and then click the \"Profile\" tab under User Control Panel, scroll to the bottom and fix your name, then try the \"/sync\" command again.");
						}
					}
				}
			}
			, 10L);
		}
	}
}
