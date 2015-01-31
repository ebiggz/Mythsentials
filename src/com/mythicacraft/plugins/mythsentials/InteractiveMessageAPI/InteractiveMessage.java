package com.mythicacraft.plugins.mythsentials.InteractiveMessageAPI;

import java.util.LinkedList;
import java.util.Queue;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;


public class InteractiveMessage {

	private Queue<InteractiveMessageElement> elements = new LinkedList<InteractiveMessageElement>();

	public InteractiveMessage() {}

	public InteractiveMessage(String text) {
		elements.add(new InteractiveMessageElement(text));
	}

	public void addElement(String text) {
		elements.add(new InteractiveMessageElement(text));
	}

	public void addElement(String text, ChatColor color) {
		elements.add(new InteractiveMessageElement(new FormattedText(text, color)));
	}

	public void addElement(FormattedText text) {
		elements.add(new InteractiveMessageElement(text));
	}

	public void addElement(InteractiveMessageElement element) {
		elements.add(element);
	}

	public void sendTo(Player player) {
		Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), this.getFormattedCommand(player.getName()));
	}

	private String getFormattedCommand(String playerName) {
		StringBuilder sb = new StringBuilder();
		sb.append("tellraw " + playerName + " {\"text\":\"\",\"extra\":[");
		int count = elements.size();
		for(InteractiveMessageElement element : elements) {
			sb.append("{");
			sb.append(element.getMainText().getJSONString());
			if(element.hasClickEvent()) {
				sb.append(",");
				sb.append("\"clickEvent\": {");
				sb.append("\"action\": \"" + element.getClickEventType().toString().toLowerCase() + "\",");
				sb.append("\"value\": \"" + element.getCommand() + "\"");
				sb.append("}");
			}
			if(element.hasHoverEvent()) {
				sb.append(",");
				sb.append("\"hoverEvent\": {");
				sb.append("\"action\": \"" + element.getHoverEventType().toString().toLowerCase() + "\",");
				sb.append("\"value\": {");
				sb.append("\"text\": \"\",");
				sb.append("\"extra\": [{");
				sb.append(element.getHoverText().getJSONString());
				sb.append("}]}}");
			}
			sb.append("}");
			count--;
			if(count > 0) {
				sb.append(",");
			}
		}
		sb.append("]}");
		return sb.toString();
	}
}
