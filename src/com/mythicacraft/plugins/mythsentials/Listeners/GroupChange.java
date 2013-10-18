package com.mythicacraft.plugins.mythsentials.Listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.events.PermissionEntityEvent;
import ru.tehkode.permissions.events.PermissionEntityEvent.Action;

import com.mythicacraft.plugins.mythsentials.Tools.Utils;


public class GroupChange implements Listener {

	@EventHandler(priority= EventPriority.MONITOR)
	public void onGroupChange(PermissionEntityEvent event) {
		if(event.getAction() == Action.INHERITANCE_CHANGED) {
			PermissionUser user = (PermissionUser) event.getEntity();
			String[] groups = user.getGroupsNames();
			for(int i = 0; i < groups.length; i++) {
				if(groups[i].equalsIgnoreCase("Member")) {
					Utils.playerNotify("mythica.helpreceive", ChatColor.RED + "[ModMessage] " + ChatColor.GOLD + user.getName() + ChatColor.YELLOW + " has just successfully registered!");
					Player p = Bukkit.getPlayerExact(user.getName());
					if(p == null) return;
					if(p.isOnline()) {
						p.sendMessage(ChatColor.AQUA + "Congrats " + p.getDisplayName() + ", you are now a member! " + ChatColor.AQUA + "\nGrab some food at the Spire farms and look at our map online to find a place to start your new adventure. Check out our wiki for more details! ");
						p.performCommand("ch g");
						Player[] onlinePs = Bukkit.getOnlinePlayers();
						for(int x = 0; x < onlinePs.length; x++) {
							if(onlinePs[x] == p) continue;
							onlinePs[x].sendMessage(ChatColor.YELLOW + p.getDisplayName() +" is now a member of Mythica. Welcome!");
						}
					}
				}
			}
		}
	}
}


