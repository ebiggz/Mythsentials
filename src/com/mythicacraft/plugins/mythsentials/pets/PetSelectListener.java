package com.mythicacraft.plugins.mythsentials.Pets;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

import com.mythicacraft.plugins.mythsentials.Mythsentials;
import com.mythicacraft.plugins.mythsentials.Pets.PetCmdProperties.Type;
/*
 * This listener checks for a player left-clicking (punching)
 *  a cat or wolf when they are using the /pet command
 *  and cancels the damage.
 */
public class PetSelectListener implements Listener {

	private Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("Mythsentials");

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPetSelect(EntityDamageByEntityEvent e) {
		//check if the entity applying the damage is a player and the entity receiving damage is a cat/wolf
		if(!(e.getDamager() instanceof Player && (e.getEntity() instanceof Ocelot || e.getEntity() instanceof Wolf))) return;
		//cast damager entity to player object
		final Player player = (Player) e.getDamager();
		//check if the player is trying to select a pet (just typed a pet command)
		if(Mythsentials.petSelector.containsKey(player)) {
			final Tameable pet = (Tameable) e.getEntity();
			if(!pet.isTamed()) {
				player.sendMessage(ChatColor.RED + "This animal isn't tamed yet! Pet selection canceled.");
			} else {
				BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
				scheduler.runTaskAsynchronously(plugin, new Runnable() {
					@Override
					public void run() {
						processCommand(player, Mythsentials.petSelector.get(player), pet);
					}
				});
			}
			Mythsentials.petSelector.remove(player);
			e.setCancelled(true);
		}
		return;
	}

	public static void processCommand(Player player, PetCmdProperties CmdP, Tameable pet) {

		Type cmdType = CmdP.getType();
		String owner = pet.getOwner().getName();
		String petType = null;
		Ocelot cat = null;
		Wolf wolf = null;

		if(pet instanceof Ocelot) {
			cat = (Ocelot) pet;
			petType = "Cat";
		} else {
			wolf = (Wolf) pet;
			petType = "Wolf";
		}

		if(cmdType == Type.GIVE) {
			if(!owner.equals(player.getName())) {
				player.sendMessage(ChatColor.RED + "You are not allowed to give this " + petType.toLowerCase() + " to someone else!");
				return;
			}
			String newOwnerName = null;
			if(CmdP.useOfflinePlayer()) {
				OfflinePlayer newOwnerO = CmdP.getNewOwnerO();
				newOwnerName = newOwnerO.getName();
				pet.setOwner((AnimalTamer) newOwnerO);
			} else {
				Player newOwner = CmdP.getNewOwner();
				pet.setOwner((AnimalTamer) newOwner);
				newOwnerName = newOwner.getName();
				if(newOwner.isOnline()) {
					newOwner.sendMessage(ChatColor.AQUA + player.getName() + " has given ownership of a " + petType.toLowerCase() + " to you.");
				}
			}
			player.sendMessage(ChatColor.AQUA + "You have given ownership of this " + petType.toLowerCase() + " to " +  newOwnerName + ".");
		}

		if(cmdType == Type.INFO) {
			player.sendMessage(ChatColor.YELLOW + petType + " info:");
			player.sendMessage(ChatColor.GOLD + "Owner: " + ChatColor.AQUA + pet.getOwner().getName());
			if(petType.equalsIgnoreCase("wolf")) {
				player.sendMessage(ChatColor.GOLD + "Health: " + ChatColor.AQUA + Math.round(wolf.getHealth()) + "/" + Math.round(wolf.getMaxHealth()));
			}
			if(petType.equalsIgnoreCase("cat")) {
				player.sendMessage(ChatColor.GOLD + "Type: " + ChatColor.AQUA + cat.getCatType().toString().toLowerCase().replace("_", "").replace("red", "tabby"));
				player.sendMessage(ChatColor.GOLD + "Health: " + ChatColor.AQUA + Math.round(cat.getHealth()) + "/" + Math.round(cat.getMaxHealth()));
			}
		}
	}
}
