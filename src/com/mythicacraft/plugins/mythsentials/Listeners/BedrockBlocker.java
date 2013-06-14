package com.mythicacraft.plugins.mythsentials.Listeners;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import com.mythicacraft.plugins.mythsentials.Mythsentials;

public class BedrockBlocker implements Listener {

	private final Mythsentials plugin;

	public BedrockBlocker(Mythsentials instance) {

		plugin = instance;
	}

	@EventHandler (priority = EventPriority.NORMAL)
	public void bedrockBreak(BlockBreakEvent event) {
		World dreamWorld = plugin.getServer().getWorld("Creative");
		Player player = event.getPlayer();
		if (event.getPlayer().getWorld() != dreamWorld) {
			return;
		}
		if (event.getBlock().getType() == Material.BEDROCK) {
			if(!player.hasPermission("mythica.admin")) {
				event.setCancelled(true);
			}
		}
	}
	@EventHandler (priority = EventPriority.NORMAL)
	public void bedrockPlace(BlockPlaceEvent event) {
		World dreamWorld = plugin.getServer().getWorld("Creative");
		Player player = event.getPlayer();
		if (event.getPlayer().getWorld() != dreamWorld) {
			return;
		}
		if (event.getBlock().getType() == Material.BEDROCK) {
			if(!player.hasPermission("mythica.admin")) {
				event.setCancelled(true);
			}
		}
	}
}
