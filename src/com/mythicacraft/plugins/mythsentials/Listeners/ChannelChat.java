package com.mythicacraft.plugins.mythsentials.Listeners;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.alecgorge.minecraft.jsonapi.api.JSONAPIStream;
import com.dthielke.herochat.ChannelChatEvent;
import com.dthielke.herochat.Chatter.Result;
import com.gmail.ebiggz.plugins.spirebotirc.IRCToChannelEvent;
import com.mythicacraft.plugins.mythsentials.Mythsentials;
import com.mythicacraft.plugins.mythsentials.Tools.HerochatStreamMessage;
import com.mythicacraft.plugins.mythsentials.Tools.IRCStreamMessage;


public class ChannelChat implements Listener{

	private JSONAPIStream herochatStream = Mythsentials.herochatStream;
	private JSONAPIStream ircStream = Mythsentials.ircStream;

	@EventHandler(priority = EventPriority.MONITOR)
	public void onChat(ChannelChatEvent e) {
		if(e.getResult() == Result.ALLOWED) {
			HerochatStreamMessage streamMessage = new HerochatStreamMessage(e.getChannel().getName(), e.getSender().getName(), ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&',e.getMessage())));
			herochatStream.addMessage(streamMessage);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onIRCChat(IRCToChannelEvent e) {
		IRCStreamMessage ircMessage = new IRCStreamMessage(e.getChannel(), e.getSender(), e.getMessage());
		ircStream.addMessage(ircMessage);
	}
}