package com.mythicacraft.plugins.mythsentials.Tools;

import org.json.simpleForBukkit.JSONObject;

import com.alecgorge.minecraft.jsonapi.api.JSONAPIStreamMessage;

public class HerochatStreamMessage extends JSONAPIStreamMessage {
	String channel, player, message;
	public HerochatStreamMessage(String channel, String player, String message) {
		this.channel = channel;
		this.player = player;
		this.message = message;
	}

	public String streamName() {
		return "herochat";
	}

	public JSONObject toJSONObject() {
		// important: make sure you import org.json.simpleForBukkit.JSONObject not import org.json.simple.JSONObject.
		JSONObject o = new JSONObject();
		o.put("time", getTime());
		o.put("channel", channel);
		o.put("player", player);
		o.put("message", message);
		return o;
	}
}
