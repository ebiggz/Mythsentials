package com.mythicacraft.plugins.mythsentials.SpirebotIRC;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

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
			bot.sendMessage("#MythicaStaff", ChatColor.stripColor(concat));
		}
	}
}
