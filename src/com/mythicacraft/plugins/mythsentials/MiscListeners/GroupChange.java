package com.mythicacraft.plugins.mythsentials.MiscListeners;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.meta.FireworkMeta;

import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.events.PermissionEntityEvent;
import ru.tehkode.permissions.events.PermissionEntityEvent.Action;

import com.mythicacraft.plugins.mythsentials.SpirebotIRC.IRCBot;
import com.mythicacraft.plugins.mythsentials.SpirebotIRC.IRCUtils;
import com.mythicacraft.plugins.mythsentials.Utilities.Utils;


public class GroupChange implements Listener {

	@SuppressWarnings("deprecation")
	@EventHandler(priority= EventPriority.MONITOR)
	public void onGroupChange(PermissionEntityEvent event) {
		if(event.getAction() == Action.INHERITANCE_CHANGED) {
			PermissionUser user = (PermissionUser) event.getEntity();
			String[] groups = user.getGroupsNames();
			for(int i = 0; i < groups.length; i++) {
				if(groups[i].equalsIgnoreCase("Member")) {
					Player p = user.getPlayer();
					if(p == null) return;
					if(p.isOnline()) {
						Utils.playerNotify("mythica.helpreceive", ChatColor.RED + "[ModMessage] " + ChatColor.GOLD + p.getDisplayName() + ChatColor.YELLOW + " has just successfully registered!");
						p.sendMessage(ChatColor.AQUA + "Congrats " + p.getDisplayName() + ", you are now a member of " + ChatColor.YELLOW + "Mythica" + ChatColor.GREEN + "!\n" + ChatColor.DARK_AQUA + "Consult the guide books for help getting started. Also have a look at our online map to find a place to start your new adventure. Lastly, don't forget to check out our wiki and tutorials! Have fun!");
						p.performCommand("ch g");
						Location fireworkLoc = p.getLocation();
						fireworkLoc.setY(p.getLocation().getY()+2);
						randomFireWork(fireworkLoc);
						Player[] onlinePs = Bukkit.getOnlinePlayers();
						for(int x = 0; x < onlinePs.length; x++) {
							if(onlinePs[x] == p) continue;
							onlinePs[x].sendMessage(ChatColor.YELLOW + p.getDisplayName() +" is now a member of Mythica. Welcome!");
						}

						IRCBot bot = IRCBot.getBot();
						String[] mods = IRCUtils.getMods();
						for(String mod : mods) {
							bot.sendMessage(mod, "[ModMessage] " + p.getDisplayName() +" is now a member of Mythica");
						}
					}
				}
				if(groups[i].equalsIgnoreCase("Donator")) {
					Player p = user.getPlayer();
					if(p == null) return;
					if(p.isOnline()) {
						p.sendMessage(ChatColor.AQUA + "You are now a donator! Thank you so much for your support!");
					}
				}
				if(groups[i].equalsIgnoreCase("Subscriber")) {
					Player p = user.getPlayer();
					if(p == null) return;
					if(p.isOnline()) {
						p.sendMessage(ChatColor.AQUA + "You are now a subscriber! Thank you so much for your support!");
					}
				}
			}
		}
	}
	public static void randomFireWork(Location loc) {
		Firework fw = (Firework)loc.getWorld().spawnEntity(loc, EntityType.FIREWORK);

		FireworkMeta fwm = fw.getFireworkMeta();

		Random r = new Random();

		int rt = r.nextInt(4) + 1;
		FireworkEffect.Type type = FireworkEffect.Type.BALL;
		if (rt == 1) type = FireworkEffect.Type.BALL;
		if (rt == 2) type = FireworkEffect.Type.BALL_LARGE;
		if (rt == 3) type = FireworkEffect.Type.BURST;
		if (rt == 4) type = FireworkEffect.Type.CREEPER;
		if (rt == 5) type = FireworkEffect.Type.STAR;

		int r1i = r.nextInt(17) + 1;
		int r2i = r.nextInt(17) + 1;
		Color c1 = getColor(r1i);
		Color c2 = getColor(r2i);

		FireworkEffect effect = FireworkEffect.builder().flicker(r.nextBoolean()).withColor(c1).withFade(c2).with(type).trail(r.nextBoolean()).build();

		fwm.addEffect(effect);

		int rp = r.nextInt(2) + 1;
		fwm.setPower(rp);

		fw.setFireworkMeta(fwm);
	}
	private static Color getColor(int i) {
		Color c = null;
		if (i == 1) {
			c = Color.AQUA;
		}
		if (i == 2) {
			c = Color.BLACK;
		}
		if (i == 3) {
			c = Color.BLUE;
		}
		if (i == 4) {
			c = Color.FUCHSIA;
		}
		if (i == 5) {
			c = Color.GRAY;
		}
		if (i == 6) {
			c = Color.GREEN;
		}
		if (i == 7) {
			c = Color.LIME;
		}
		if (i == 8) {
			c = Color.MAROON;
		}
		if (i == 9) {
			c = Color.NAVY;
		}
		if (i == 10) {
			c = Color.OLIVE;
		}
		if (i == 11) {
			c = Color.ORANGE;
		}
		if (i == 12) {
			c = Color.PURPLE;
		}
		if (i == 13) {
			c = Color.RED;
		}
		if (i == 14) {
			c = Color.SILVER;
		}
		if (i == 15) {
			c = Color.TEAL;
		}
		if (i == 16) {
			c = Color.WHITE;
		}
		if (i == 17) {
			c = Color.YELLOW;
		}
		return c;
	}
}


