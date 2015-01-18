package com.mythicacraft.plugins.mythsentials.MiscListeners;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.bekvon.bukkit.residence.Residence;
import com.bekvon.bukkit.residence.protection.ClaimedResidence;
import com.dthielke.herochat.Channel;
import com.dthielke.herochat.ChannelChatEvent;
import com.dthielke.herochat.Chatter;
import com.dthielke.herochat.Herochat;
import com.mythicacraft.plugins.mythsentials.SpirebotIRC.IRCBot;
import com.mythicacraft.plugins.mythsentials.SpirebotIRC.IRCUtils;

public class ChatListener implements Listener {



	@EventHandler(priority= EventPriority.MONITOR)
	public void onChannelChat(ChannelChatEvent event) {

		ClaimedResidence spire = Residence.getResidenceManager().getByName("Spawn");
		ArrayList<Player> spawnPlayers = spire.getPlayersInResidence();
		Channel spawnChat = Herochat.getChannelManager().getChannel("SpawnChat");
		Channel localChat = Herochat.getChannelManager().getChannel("Local");
		IRCBot bot = IRCBot.getBot();

		if(event.getChannel() == localChat) {
			Location playerLoc = event.getSender().getPlayer().getLocation();
			if(Residence.getResidenceManager().getByLoc(playerLoc) != spire) return;
			String sendString  = spawnChat.applyFormat(spawnChat.getFormat(), "", event.getSender().getPlayer());
			sendString = sendString.replace("%1$s", event.getSender().getName());
			sendString = sendString.replace("%2$s", event.getMessage());
			sendString = sendString.replace("[SpawnChat]", "[SpawnChat](In Spawn)");
			Set<Chatter> members = spawnChat.getMembers();
			Iterator<Chatter> it = members.iterator();
			while(it.hasNext()) {
				Player p = it.next().getPlayer();
				if(Residence.getResidenceManager().getByLoc(p.getLocation()) == spire) continue;
				p.sendMessage(sendString);
			}
			String[] mods = IRCUtils.getMods();
			for(String mod : mods) {
				bot.sendMessage(mod, "[SpawnChat] " + ChatColor.stripColor(sendString.replace("[L]", "")));
			}
		}

		if(event.getChannel() == spawnChat) {

			if(spawnPlayers.size() == 0) {
				event.getSender().getPlayer().sendMessage(ChatColor.RED + "There's no one in Spawn.");
				return;
			}

			String sendString  = localChat.applyFormat(localChat.getFormat(), "", event.getSender().getPlayer());
			sendString = sendString.replace("%1$s", event.getSender().getName());
			sendString = sendString.replace("%2$s", event.getMessage());

			for(int i = 0; i < spawnPlayers.size(); i++) {
				if(spawnPlayers.get(i).hasPermission("mythica.helpreceive")) continue;
				spawnPlayers.get(i).sendMessage(sendString);
			}

			String[] mods = IRCUtils.getMods();
			for(String mod : mods) {
				bot.sendMessage(mod, ChatColor.stripColor(sendString));
			}
		}
	}
}


