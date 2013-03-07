package com.gmail.ebiggz.plugins.mythsentials;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class OnlineModNotifier implements Listener {
	
	
	@EventHandler (priority = EventPriority.MONITOR)
    public void onInterect(PlayerJoinEvent event) {
		Player p = event.getPlayer();
		Boolean mods = HelpMe.modsOnline();
		if(p.hasPermission("mythica.notnoob")) {
		    if(mods == true) {
			    p.sendMessage(ChatColor.GOLD + "Mods are " + ChatColor.GREEN + "Online" + ChatColor.GOLD + "! Type /helpme if you need us.");
		    } else {
			    p.sendMessage(ChatColor.GOLD + "Mods are " + ChatColor.RED + "Offline" + ChatColor.GOLD + ". If you need help, make an Issue on the website for mods to review as soon as they get on.");
		    }
		}
	}	
}	