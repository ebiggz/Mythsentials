package com.mythicacraft.plugins.mythsentials.MythiboardAPI;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Scoreboard;

import com.mythicacraft.plugins.mythsentials.Mythsentials;


public class BoardListener implements Listener{

	@EventHandler (priority = EventPriority.MONITOR)
	public void onJoin(PlayerJoinEvent event) {

		final Player p = event.getPlayer();

		if(!p.hasPlayedBefore()) return;

		final Scoreboard sb = Mythsentials.getMythiboardManager().getMythiboard(p);
		p.setScoreboard(sb);

		final BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
		scheduler.runTaskLater(Mythsentials.getPlugin(), new Runnable() {
			@Override
			public void run() {
				sb.clearSlot(DisplaySlot.SIDEBAR);
				Scoreboard emptyBoard = Bukkit.getScoreboardManager().getNewScoreboard();
				p.setScoreboard(emptyBoard);
			}
		}, 600L);

	}
}
