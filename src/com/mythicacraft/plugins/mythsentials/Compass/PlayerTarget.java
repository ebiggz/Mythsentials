package com.mythicacraft.plugins.mythsentials.Compass;



import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.mythicacraft.plugins.mythsentials.Mythsentials;


public class PlayerTarget {

	private Mythsentials plugin;
	private List<Player> trackers = new ArrayList<Player>();
	private Player target;

	public PlayerTarget(Player tracker, Player target, Mythsentials plugin) {
		if(!trackers.contains(tracker)) {
			trackers.add(tracker);
		}
		this.target = target;
		this.plugin = plugin;
	}

	public List<Player> getTrackers() {
		return trackers;
	}

	public boolean hasTracker(Player tracker) {
		return trackers.contains(tracker);
	}

	public void addTracker(Player tracker) {
		if(!trackers.contains(tracker)) {
			trackers.add(tracker);
		}
	}

	public void removeTracker(Player tracker) {
		if(trackers.contains(tracker)) {
			trackers.remove(tracker);
			target.sendMessage(ChatColor.YELLOW + tracker.getName() + ChatColor.GREEN + " is no longer compass tracking you.");
		}
		if(trackers.isEmpty()) {
			plugin.playerTargets.remove(target);
		}
	}

	public Player getTarget() {
		return target;
	}
}
