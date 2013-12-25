package com.mythicacraft.plugins.mythsentials.JsonAPI;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.Event;

import ru.tehkode.permissions.bukkit.PermissionsEx;

import com.alecgorge.minecraft.jsonapi.api.APIMethodName;
import com.alecgorge.minecraft.jsonapi.api.JSONAPICallHandler;
import com.alecgorge.minecraft.jsonapi.api.JSONAPIStream;
import com.dthielke.herochat.Channel;
import com.dthielke.herochat.Herochat;
import com.mythicacraft.plugins.mythsentials.Mythsentials;


public class HerochatJSONHandler implements JSONAPICallHandler {
	private JSONAPIStream herochatStream = Mythsentials.herochatStream;
	// if you return true here, you WILL handle the API call in the handle method.
	// will be called twice for every API call, once to test if the method exists and once on the actuall call

	public boolean willHandle(APIMethodName methodName) {
		if(methodName.matches("sendChatChannelMessage")) {
			return true;
		}
		if(methodName.matches("getChatChannelNames")) {
			return true;
		}
		return false;
	}

	// the result of this method will be treated as the response to the API request, even if null is returned (some methods do return null)
	public Object handle(APIMethodName methodName, Object[] args) {
		if(methodName.matches("sendChatChannelMessage")) {

			String message = (String) args[2];
			String channel = (String) args[0];
			String sender = (String) args[1];

			String fullName = ChatColor.translateAlternateColorCodes('&',PermissionsEx.getUser(sender).getPrefix() + sender + PermissionsEx.getUser(sender).getSuffix());

			String concat = fullName + ChatColor.RESET + ": " + message;

			concat = ChatColor.translateAlternateColorCodes('&',concat);
			Channel hcChannel = Herochat.getChannelManager().getChannel(channel);

			if(channel.equalsIgnoreCase("Global")) {
				hcChannel.sendRawMessage(concat);
			}
			if(channel.equalsIgnoreCase("Local")) {
				concat = ChatColor.YELLOW + "[L] " + ChatColor.RESET + fullName + ChatColor.YELLOW + ": " + message;
				hcChannel.sendRawMessage(concat);
			}
			if(channel.equalsIgnoreCase("Survival")) {
				concat = ChatColor.WHITE + "[S] " + ChatColor.RESET + fullName + ChatColor.WHITE + ": " + message;
				hcChannel.sendRawMessage(concat);
			}
			if(channel.equalsIgnoreCase("PvP")) {
				concat = ChatColor.WHITE + "[P] " + ChatColor.RESET + fullName + ChatColor.WHITE + ": " + message;
				hcChannel.sendRawMessage(concat);
			}
			if(channel.equalsIgnoreCase("ModChat")) {
				concat = ChatColor.BLUE + "[M](App)" + ChatColor.RESET + ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&',sender)) + ChatColor.GRAY + ": " + message;
				hcChannel.sendRawMessage(concat);
			}
			if(channel.equalsIgnoreCase("SpawnChat")) {
				hcChannel.announce(concat);
			}

			Event appToChannel = new AppToChannelEvent(channel, sender, message);
			Bukkit.getServer().getPluginManager().callEvent(appToChannel);

			HerochatStreamMessage streamMessage = new HerochatStreamMessage(channel, ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&',sender)), ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&',message)));
			herochatStream.addMessage(streamMessage);

			return true;
		}
		if(methodName.matches("getChatChannelNames")) {
			List<Channel> channels = Herochat.getChannelManager().getChannels();
			String[] channelNames = new String[channels.size()];
			int i = 0;
			for(Channel channel: channels) {
				channelNames[i] = channel.getName();
				i++;
			}
			return channelNames;
		}
		return false;
	}
}
