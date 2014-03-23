package com.mythicacraft.plugins.mythsentials.MiscListeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.gmail.mythicacraft.mythicaspawn.MythicaSpawn;
import com.mythicacraft.plugins.mythsentials.Mythsentials;
import com.mythicacraft.plugins.mythsentials.JsonAPI.NotificationStreamMessage;
import com.mythicacraft.plugins.mythsentials.Utilities.ConfigAccessor;
import com.mythicacraft.plugins.mythsentials.Utilities.Time;
import com.mythicacraft.plugins.mythsentials.Utilities.Utils;


public class LoginListener implements Listener {

	@EventHandler (priority = EventPriority.MONITOR)
	public void onJoin(PlayerJoinEvent event) {
		ConfigAccessor playerData = new ConfigAccessor("players.yml");
		Player p = event.getPlayer();
		if(!p.hasPlayedBefore()) {
			Mythsentials.notificationStream.addMessage(new NotificationStreamMessage("newplayer", p.getName(), null));
			Utils.playerNotify("mythica.helpreceive", ChatColor.RED + "[ModMessage] " + ChatColor.GOLD + p.getDisplayName() + ChatColor.YELLOW + " is new!");

		}
		//checkForNewLoc(p);
		String playerName = p.getDisplayName();
		Utils.offlineBalanceChange(p);
		Utils.modMessage(p);
		String time = Time.getTime();
		playerData.getConfig().set(playerName + ".joinTime", time);
		playerData.saveConfig();
		if(MythicaSpawn.getSpawnManager().getWorldType(p.getWorld()).equalsIgnoreCase("pvp")) {
			Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "pex reload");
		}
	}

}
