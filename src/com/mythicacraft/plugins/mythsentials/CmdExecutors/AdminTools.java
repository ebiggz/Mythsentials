package com.mythicacraft.plugins.mythsentials.CmdExecutors;

import java.text.ParseException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ru.tehkode.permissions.bukkit.PermissionsEx;

import com.bekvon.bukkit.residence.Residence;
import com.bekvon.bukkit.residence.protection.ClaimedResidence;
import com.dthielke.herochat.Herochat;
import com.mythicacraft.plugins.mythsentials.Tools.ConfigAccessor;
import com.mythicacraft.plugins.mythsentials.Tools.Time;

public class AdminTools implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		ConfigAccessor playerData = new ConfigAccessor("players.yml");
		if(!sender.hasPermission("mythica.mod")) {
			sender.sendMessage(ChatColor.RED + "You don't have permission for this!");
			return true;
		}
		if(commandLabel.equalsIgnoreCase("playerinfo")) {
			if(args.length == 0) {
				sender.sendMessage(ChatColor.RED + "Please type in a players name.");
				return true;
			}
			if(args.length == 1) {

				args[0] = completeName(args[0]);
				if(args[0] == null) {
					sender.sendMessage(ChatColor.RED + "Not a known player.");
					return true;
				}

				Player p = Bukkit.getPlayerExact(completeName(args[0]));
				if(p == null) {
					OfflinePlayer offp = Bukkit.getOfflinePlayer(args[0]);
					sender.sendMessage(ChatColor.GREEN + "-------" + args[0] + " Player Info-------");
					sender.sendMessage(ChatColor.YELLOW + "Online:" + ChatColor.GOLD +  " False");
					sender.sendMessage(ChatColor.YELLOW + "First Joined: " + ChatColor.GOLD + Time.dateFromMills(offp.getFirstPlayed()));
					sender.sendMessage(ChatColor.YELLOW + "Last Played: " + ChatColor.GOLD + Time.dateFromMills(offp.getLastPlayed()));
					sender.sendMessage(ChatColor.YELLOW + "Perm Group: " + ChatColor.GOLD + getPermGroupStr(offp.getName()));
					return true;
				}

				sender.sendMessage(ChatColor.GREEN + "-------" + args[0] + " Player Info-------");
				sender.sendMessage(ChatColor.YELLOW + "Online:" + ChatColor.GOLD +  " True");
				if(playerData.getConfig().contains(args[0] + ".joinTime")) {
					String joinTime = playerData.getConfig().getString(args[0] + ".joinTime");
					try {
						sender.sendMessage(ChatColor.YELLOW + "Playtime: " + ChatColor.GOLD + Time.timeString(Time.compareTimeMills(joinTime)));
					} catch (ParseException e) {
						e.printStackTrace();
					}
				}
				sender.sendMessage(ChatColor.YELLOW + "First Joined: " + ChatColor.GOLD + Time.dateFromMills(p.getFirstPlayed()));
				sender.sendMessage(ChatColor.YELLOW + "Last Played: " + ChatColor.GOLD + Time.dateFromMills(p.getLastPlayed()));
				String ip = p.getAddress().toString().replace("/", "");
				if(ip.contains(":")) {
					int index = ip.indexOf(":");
					ip = ip.substring(0, index - 1);
				}
				sender.sendMessage(ChatColor.YELLOW + "IP: " + ChatColor.GOLD + ip);
				sender.sendMessage(ChatColor.YELLOW + "Perm Group: " + ChatColor.GOLD + getPermGroupStr(p));
				sender.sendMessage(ChatColor.YELLOW + "Active Chat Channel: " + ChatColor.GOLD + Herochat.getChatterManager().getChatter(p).getActiveChannel().getName().replace(args[0], " with "));
				Location loc = p.getLocation();
				ClaimedResidence res = Residence.getResidenceManager().getByLoc(loc);
				String locMessage = ChatColor.YELLOW + "Current Location: "  + ChatColor.GOLD + "X:" + (int)(p.getLocation().getX()) + " Y:" + (int)(p.getLocation().getY()) + " Z:" + (int)(p.getLocation().getZ()) + ", " + p.getWorld().getName().replace("_", " ");
				if(res != null) {
					locMessage = locMessage + ", " + res.getName();
				}
				sender.sendMessage(locMessage);
				sender.sendMessage(ChatColor.YELLOW + "Stats: " + ChatColor.GOLD + "Health - " + ChatColor.GOLD + p.getHealth()/2 + "/10, Food - " + p.getFoodLevel()/2 + "/10, XPLevel - " + p.getLevel());
			}
		}
		if(args.length > 1) {
			sender.sendMessage(ChatColor.RED + "Too many arguements!");
		}
		return false;
	}

	public String completeName(String playername) {
		Player[] onlinePlayers = Bukkit.getOnlinePlayers();
		for(int i = 0; i < onlinePlayers.length; i++) {
			if(onlinePlayers[i].getName().toLowerCase().startsWith(playername.toLowerCase())) {
				return onlinePlayers[i].getName();
			}
		}
		OfflinePlayer[] offlinePlayers = Bukkit.getOfflinePlayers();
		for(int i = 0; i < offlinePlayers.length; i++) {
			if(offlinePlayers[i].getName().toLowerCase().startsWith(playername.toLowerCase())) {
				return offlinePlayers[i].getName();
			}
		}
		return null;
	}

	public String getPermGroupStr(Player player) {
		String[] groups = PermissionsEx.getUser(player).getGroupsNames();
		String groupsStr = "";
		for(int i = 0; i < groups.length; i++) {
			groupsStr = groupsStr + groups[i];
			if(i+1 < groups.length) {
				groupsStr = groupsStr + ", ";
			}
		}
		return groupsStr;
	}
	public String getPermGroupStr(String playername) {
		String[] groups = PermissionsEx.getUser(playername).getGroupsNames();
		String groupsStr = "";
		for(int i = 0; i < groups.length; i++) {
			groupsStr = groupsStr + groups[i];
			if(i+1 < groups.length) {
				groupsStr = groupsStr + ", ";
			}
		}
		return groupsStr;
	}
}
