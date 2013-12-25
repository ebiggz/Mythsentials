package com.mythicacraft.plugins.mythsentials.JsonAPI;

import org.json.simpleForBukkit.JSONObject;

import com.alecgorge.minecraft.jsonapi.api.JSONAPIStreamMessage;

public class NotificationStreamMessage extends JSONAPIStreamMessage {
	String type, player, message;
	public NotificationStreamMessage(String type, String player, String message) {
		this.type = type;
		this.player = player;
		this.message = message;
	}

	public String streamName() {
		return "notifications";
	}

	public JSONObject toJSONObject() {
		// important: make sure you import org.json.simpleForBukkit.JSONObject not import org.json.simple.JSONObject.
		JSONObject o = new JSONObject();
		o.put("time", getTime());
		o.put("type", type);
		o.put("player", player);
		o.put("message", message);
		return o;
	}
}
