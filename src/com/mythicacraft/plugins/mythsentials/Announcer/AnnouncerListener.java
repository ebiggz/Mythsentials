package com.mythicacraft.plugins.mythsentials.Announcer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitScheduler;

import com.mythicacraft.plugins.mythsentials.Mythsentials;


public class AnnouncerListener implements Listener {

	@EventHandler (priority = EventPriority.MONITOR)
	public void onJoin(final PlayerJoinEvent event) {
		BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
		scheduler.scheduleSyncDelayedTask(Mythsentials.getPlugin(), new Runnable() {
			@Override
			public void run() {
				if(event.getPlayer().getName().equals("scribbles08")) {
					event.getPlayer().sendMessage(ChatColor.LIGHT_PURPLE + "Welcome back, Princess <3");
				}
				event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', everyoneAnnouncement()));
				if(Mythsentials.usePermGroupLoginAnnouncements && Mythsentials.hasPermPlugin) {
					String[] playerGroups = Mythsentials.permission.getPlayerGroups(event.getPlayer());
					for(String group: playerGroups) {
						event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', permGroupAnnouncement(group)));
					}
				}
			}
		}, 10L);
	}

	String everyoneAnnouncement() {
		String pluginFolderPath = Mythsentials.getPlugin().getDataFolder().getAbsolutePath() + File.separator + "data" + File.separator + "announcer" + File.separator + "Login Announcements";
		String message = "";
		File everyoneFile = new File(pluginFolderPath + File.separator + "everyone.txt");

		try {

			BufferedReader reader = new BufferedReader(
					new FileReader(everyoneFile));

			String line = reader.readLine();
			while(line != null) {
				message += line;
				line = reader.readLine();
			}
			reader.close();
		}
		catch(FileNotFoundException e) {
			(new File(pluginFolderPath)).mkdirs();
			try {
				everyoneFile.createNewFile();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		catch(IOException e) {
			Mythsentials.log.severe("[Announcer] An IO exception occured!");
		}
		return message;
	}
	String permGroupAnnouncement(String permGroup) {
		String pluginFolderPath = Mythsentials.getPlugin().getDataFolder().getAbsolutePath() + File.separator + "data" + File.separator + "announcer" + File.separator + "Login Announcements" + File.separator + "Permission Groups";
		String message = "";
		File everyoneFile = new File(pluginFolderPath + File.separator + permGroup + ".txt");

		try {

			BufferedReader reader = new BufferedReader(
					new FileReader(everyoneFile));

			String line = reader.readLine();
			while(line != null) {
				message += line;
				line = reader.readLine();
			}
			reader.close();
		}
		catch(FileNotFoundException e) {
			(new File(pluginFolderPath)).mkdirs();
			try {
				everyoneFile.createNewFile();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		catch(IOException e) {
			Mythsentials.log.severe("[Announcer] An IO exception occured!");
		}
		return message;
	}
}
