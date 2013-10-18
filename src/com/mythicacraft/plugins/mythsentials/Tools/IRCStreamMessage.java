package com.mythicacraft.plugins.mythsentials.Tools;

import org.json.simpleForBukkit.JSONObject;

import com.alecgorge.minecraft.jsonapi.api.JSONAPIStreamMessage;

public class IRCStreamMessage extends JSONAPIStreamMessage {
	String channel, sender, message;
	public IRCStreamMessage(String channel, String sender, String message) {
		this.sender = sender;
		this.channel = channel;
		this.message = message;
	}

	public String streamName() {
		return "irc";
	}

	public JSONObject toJSONObject() {
		// important: make sure you import org.json.simpleForBukkit.JSONObject not import org.json.simple.JSONObject.
		JSONObject o = new JSONObject();
		o.put("time", getTime());
		o.put("channel", channel);
		o.put("sender", sender);
		o.put("message", message);
		return o;
	}
}
