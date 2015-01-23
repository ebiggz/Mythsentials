package com.mythicacraft.plugins.mythsentials.DeathLedger;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import com.mythicacraft.plugins.mythsentials.Mythian;
import com.mythicacraft.plugins.mythsentials.Mythsentials;
import com.mythicacraft.plugins.mythsentials.Utilities.Time;


public class DeathListener implements Listener {


	@EventHandler (priority = EventPriority.MONITOR)
	public void onDeath(PlayerDeathEvent event) {
		String playerName = event.getEntity().getName();
		Mythian mythian = Mythsentials.getMythianManager().getMythian(playerName);

		List<ItemStack> drops = event.getDrops();

		if(drops.size() > 3) {

			Location death = event.getEntity().getLocation();
			String deathWorld = event.getEntity().getWorld().getName();
			String deathLoc = Integer.toString((int) death.getX()) + "," + Integer.toString((int) death.getY()) + "," + Integer.toString((int) death.getZ()) + "," + death.getWorld().getName();
			List<ItemStack> armor = Arrays.asList(event.getEntity().getInventory().getArmorContents());

			String reason = event.getDeathMessage().replace(playerName, "Player").trim();
			String time = Time.dateAndTimeFromMills(Time.timeInMillis());

			DeathLog thisDeath = new DeathLog(playerName, drops, armor, deathLoc, deathWorld, reason, time);
			mythian.addNewDeathLog(thisDeath);

		}
	}
}
