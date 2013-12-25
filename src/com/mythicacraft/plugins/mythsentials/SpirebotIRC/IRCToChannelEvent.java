package com.mythicacraft.plugins.mythsentials.SpirebotIRC;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;


public class IRCToChannelEvent extends Event {
	private static final HandlerList handlers = new HandlerList();
	private String channel, sender, message;

	public IRCToChannelEvent(String channel, String sender, String message) {
		this.sender = sender;
		this.message = message;
		this.channel = channel;
	}

	public String getMessage() {
		return message;
	}

	public String getChannel() {
		return channel;
	}

	public String getSender() {
		return sender;
	}

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

}
