package com.mythicacraft.plugins.mythsentials.JsonAPI;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;


public class AppToChannelEvent extends Event {

	private static final HandlerList handlers = new HandlerList();
	private String channel, sender, message;

	public AppToChannelEvent(String channel, String sender, String message) {
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
