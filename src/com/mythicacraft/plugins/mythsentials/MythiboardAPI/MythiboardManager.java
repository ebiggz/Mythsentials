package com.mythicacraft.plugins.mythsentials.MythiboardAPI;

import java.util.ArrayList;
import java.util.List;

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

	public void deregisterScoreboardEntry(ScoreboardEntry se) {
		entries.remove(se);
	}

	public List<ScoreboardEntry> getEntries() {
		return entries;
	}

	@SuppressWarnings("deprecation")
	public Scoreboard getMythiboard(Player player) {
		Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
		board.registerNewObjective("InfoPanel", "dummy");
		Objective objective = board.getObjective("InfoPanel");
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		objective.setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + "MythicaCraft");

		int count = (entries.size()*3);
		int blankCount = 1;

		for(ScoreboardEntry entry : entries) {

			String key = ChatColor.AQUA + "" + ChatColor.BOLD + ChatColor.stripColor(entry.getKey());
			String value = ChatColor.stripColor(entry.getValue(player));

			if(key == null || key.isEmpty() || value == null || value.isEmpty()) {count -= 2; continue;}

			objective.getScore(Bukkit.getOfflinePlayer(key)).setScore(count);
			count--;
			objective.getScore(Bukkit.getOfflinePlayer(value)).setScore(count);
			count--;
			objective.getScore(Bukkit.getOfflinePlayer(emptyScore(blankCount))).setScore(count);
			blankCount++;
			count--;

		}
		objective.getScore(Bukkit.getOfflinePlayer(ChatColor.GRAY + "Type " + ChatColor.WHITE + "/me" + ChatColor.GRAY + " for more.")).setScore(count);
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
