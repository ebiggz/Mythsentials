package com.mythicacraft.plugins.mythsentials.MythiboardAPI;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;


public class MythiboardManager {

	private ArrayList<ScoreboardEntry> entries = new ArrayList<ScoreboardEntry>();
	//private Logger log = Bukkit.getPluginManager().getPlugin("Mythsentials").getLogger();

	protected MythiboardManager() { /*exists to block instantiation*/ }
	private static MythiboardManager instance = null;
	public static MythiboardManager getInstance() {
		if(instance == null) {
			instance = new MythiboardManager();
		}
		return instance;
	}

	public void registerScoreboardEntry(ScoreboardEntry se) {
		entries.add(se);
	}

	public void reregisterScoreboardEntry(ScoreboardEntry se) {
		entries.remove(se);
	}

	public Scoreboard getMythiboard(Player player) {
		Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
		board.registerNewObjective("InfoPanel", "dummy");
		Objective objective = board.getObjective("InfoPanel");
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		objective.setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + "MythicaCraft");

		/*int commandCount = 0;
		for(ScoreboardEntry entry : entries) {
			String command = entry.getCommand();
			if(command != null && !command.isEmpty()) commandCount++;
		}*/

		int count = (entries.size()*3) - 1 /*+ commandCount*/;
		int blankCount = 1;

		for(ScoreboardEntry entry : entries) {

			String key = ChatColor.AQUA + "" + ChatColor.BOLD + ChatColor.stripColor(entry.getKey());
			String value = ChatColor.stripColor(entry.getValue(player));

			if(key == null || key.isEmpty() || value == null || value.isEmpty()) {count -= 2; continue;}

			/*if(key.length() > 16) {
				key = key.substring(0, 13) + "...";
			}
			if(value.length() > 16) {
				value = value.substring(0, 13) + "...";
				continue;
			}*/

			objective.getScore(Bukkit.getOfflinePlayer(key)).setScore(count);
			count--;
			objective.getScore(Bukkit.getOfflinePlayer(value)).setScore(count);
			count--;

			/*String command = entry.getCommand();
			if(command != null && !command.isEmpty()) {
				objective.getScore(Bukkit.getOfflinePlayer(ChatColor.GRAY + command)).setScore(count);
				count--;
			}*/

			if(count > 1) {
				objective.getScore(Bukkit.getOfflinePlayer(emptyScore(blankCount))).setScore(count);
				blankCount++;
				count--;
			}

		}
		return board;
	}

	String emptyScore(int amount) {
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < amount; i++) {
			sb.append(ChatColor.RESET);
		}
		return sb.toString();
	}
}
