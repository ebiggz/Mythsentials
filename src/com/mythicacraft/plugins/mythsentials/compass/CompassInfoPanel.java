package com.mythicacraft.plugins.mythsentials.Compass;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import com.mythicacraft.plugins.mythsentials.Utilities.Utils;


public class CompassInfoPanel extends BukkitRunnable {

	private Player user;
	private Scoreboard board;
	private Objective objective;
	private Score distance;
	private Score height;
	private ScoreboardManager manager = Bukkit.getScoreboardManager();

	public CompassInfoPanel(Player user) {
		this.user = user;
		board = manager.getNewScoreboard();
		board.registerNewObjective("InfoPanel", "dummy");
		objective = board.getObjective("InfoPanel");
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		objective.setDisplayName(ChatColor.GOLD + Utils.getTargetName(user));
		distance = objective.getScore(Bukkit.getOfflinePlayer(ChatColor.GREEN + "Distance"));
		height = objective.getScore(Bukkit.getOfflinePlayer(ChatColor.GREEN + "Height"));

	}

	@Override
	public void run() {
		if(user.isOnline()) {
			updateBoard();
			user.setScoreboard(board);
		}
	}

	private void updateBoard() {
		distance.setScore(getDistance());
		height.setScore(getHeight());
	}

	public void setTargetName(String name) {
		objective.setDisplayName(ChatColor.GOLD + name);
	}

	private int getDistance() {
		double distance = user.getLocation().distance(user.getCompassTarget());
		return (int) Math.floor(distance);
	}

	private int getHeight() {
		int playerY = user.getLocation().getBlockY();
		int targetY = user.getCompassTarget().getBlockY();
		return targetY - playerY;
	}
	public void clearBoard() {
		board.clearSlot(DisplaySlot.SIDEBAR);
		Scoreboard emptyBoard = manager.getNewScoreboard();
		user.setScoreboard(emptyBoard);
	}
}
