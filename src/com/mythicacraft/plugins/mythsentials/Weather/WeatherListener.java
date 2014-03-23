package com.mythicacraft.plugins.mythsentials.Weather;

import org.bukkit.ChatColor;
import org.bukkit.WeatherType;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

import com.mythicacraft.plugins.mythsentials.Mythian;
import com.mythicacraft.plugins.mythsentials.Mythsentials;



public class WeatherListener implements Listener {

	@EventHandler(priority = EventPriority.MONITOR)
	public void onLogin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		Mythian mythian = Mythsentials.getMythianManager().getMythian(player.getName());
		if(player.getWorld().hasStorm()) {
			if(mythian.getAutohideWeather()) {
				player.setPlayerWeather(WeatherType.CLEAR);
				player.sendMessage(ChatColor.AQUA + "It's currently storming in this world but the weather has been autohidden.");
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onWorldChange(PlayerChangedWorldEvent event) {
		Player player = event.getPlayer();
		Mythian mythian = Mythsentials.getMythianManager().getMythian(player.getName());
		if(player.getWorld().hasStorm()) {
			if(mythian.getAutohideWeather()) {
				player.setPlayerWeather(WeatherType.CLEAR);
				player.sendMessage(ChatColor.AQUA + "It's currently storming in this world but the weather has been autohidden.");
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onLogin(WeatherChangeEvent event) {
		if(!event.isCancelled()) {
			if(event.toWeatherState()) {
				World w = event.getWorld();
				for(Player player : w.getPlayers()) {
					Mythian mythian = Mythsentials.getMythianManager().getMythian(player.getName());
					if(player.getWorld().hasStorm()) {
						if(mythian.getAutohideWeather()) {
							player.setPlayerWeather(WeatherType.CLEAR);
							player.sendMessage(ChatColor.AQUA + "It started storming but the weather has been autohidden.");
						}
					}
				}
			}
		}
	}
}
