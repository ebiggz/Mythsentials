package com.gmail.ebiggz.plugins.mythsentials.Listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class CommandAliases implements Listener {

	@EventHandler(priority= EventPriority.HIGHEST)
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
		String command = event.getMessage();
		if(command.contains("/money")) {
			String newCommand = command.replace("/money", "/econ");
			event.setMessage(newCommand);
			return;
		}
		
		if(command.equalsIgnoreCase("/res tool")) {
			String newCommand = "/restool";
			event.setMessage(newCommand);
			return;
		}

	}
}
