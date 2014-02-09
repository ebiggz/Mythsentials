package com.mythicacraft.plugins.mythsentials;

import java.text.ParseException;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.gmail.mythicacraft.mythicaspawn.MythicaSpawn;
import com.gmail.mythicacraft.mythicaspawn.SpawnManager.Universe;
import com.mythicacraft.plugins.mythsentials.Utilities.ConfigAccessor;
import com.mythicacraft.plugins.mythsentials.Utilities.Time;


public class Mythian {

	private String playerName;
	//private SpawnManager sm = MythicaSpawn.getSpawnManager();

	public Mythian(String playerName) {
		this.playerName = playerName;
	}

	public long getPlayTime() {
		ConfigAccessor playerData = new ConfigAccessor("players.yml");
		if(playerData.getConfig().contains(playerName + ".joinTime")) {
			String joinTime = playerData.getConfig().getString(playerName + ".joinTime");
			try {
				return Time.compareTimeMills(joinTime);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return 0;
	}

	public int getRunes() {
		ConfigAccessor playerData = new ConfigAccessor("players.yml");
		return playerData.getConfig().getInt(playerName + ".runes", 0);
	}

	public void setRunes(int amount) {
		ConfigAccessor playerData = new ConfigAccessor("players.yml");
		playerData.getConfig().set(playerName + ".runes", amount);
		playerData.saveConfig();
	}

	public Universe getCurrentUniverse() {
		Player p = Bukkit.getPlayer(playerName);
		if(p != null && p.isOnline()) {
			return MythicaSpawn.getSpawnManager().getWorldsUniverse(p.getWorld());
		} else {
			return null;
		}
	}
}
