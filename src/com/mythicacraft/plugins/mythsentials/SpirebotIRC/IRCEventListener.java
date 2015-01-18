package com.mythicacraft.plugins.mythsentials.SpirebotIRC;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.mythicacraft.plugins.mythsentials.JsonAPI.AppToChannelEvent;


public class IRCEventListener implements Listener {

	IRCBot bot = IRCBot.getBot();

	@EventHandler
	public void onAppChat(AppToChannelEvent e) {
		String channel = e.getChannel();
		String sender = "("+e.getSender()+")";
		String message = e.getMessage();
		String concat = sender + " " + message;
		if(channel.equals("Global")) {
			bot.sendMessage("#MythicaCraft", ChatColor.stripColor(concat));
		}
		else if(channel.equals("ModChat")) {
			bot.sendMessage("##MythicaStaff", ChatColor.stripColor(concat));
		}
	}

	public void onPlayerJoin(PlayerJoinEvent event) {
		if(!event.getPlayer().hasPlayedBefore()) {
			String[] mods = IRCUtils.getMods();
			for(String mod : mods) {
				bot.sendMessage(mod, "[ModMessage] " + mod + ", " + event.getPlayer().getName() + " is new to Mythica!");
			}
		}
	}
}
