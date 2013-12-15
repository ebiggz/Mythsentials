package com.mythicacraft.plugins.mythsentials.admintools;

import java.text.ParseException;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import ru.tehkode.permissions.bukkit.PermissionsEx;

import com.bekvon.bukkit.residence.Residence;
import com.bekvon.bukkit.residence.protection.ClaimedResidence;
import com.dthielke.herochat.Herochat;
import com.mythicacraft.plugins.mythsentials.Tools.ConfigAccessor;
import com.mythicacraft.plugins.mythsentials.Tools.Paginate;
import com.mythicacraft.plugins.mythsentials.Tools.Time;
import com.mythicacraft.plugins.mythsentials.Tools.Utils;

public class AdminTools implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		ConfigAccessor playerData = new ConfigAccessor("players.yml");
		if(!sender.hasPermission("mythica.mod")) {
			sender.sendMessage(ChatColor.RED + "You don't have permission for this!");
			return true;
		}
		if(commandLabel.equalsIgnoreCase("toolbox")) {

		}
		if(commandLabel.equalsIgnoreCase("loginlocation")) {

			Player mod = (Player) sender;
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
				setLoginLocation(args[0], mod.getLocation());
				sender.sendMessage(ChatColor.AQUA + "You have set the login location of " + ChatColor.YELLOW + args[0] + ChatColor.AQUA + " to where you're standing.");
			}
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
		if(commandLabel.equalsIgnoreCase("deathdrops")) {

			Paginate playerDeathDrops = new Paginate();

			if(args.length == 0) {
				sender.sendMessage(ChatColor.RED + "Please type in a players name.");
				return true;
			}
			if(args.length <= 3) {

				args[0] = completeName(args[0]);
				if(args[0] == null) {
					sender.sendMessage(ChatColor.RED + "Not a known player.");
					return true;
				}
				List<DeathDrops> dropsList = Utils.getPlayerDeathDrops(args[0]);
				if(dropsList.isEmpty()) {
					sender.sendMessage(ChatColor.RED + "No prior deathdrops saved for " + args[0] + " yet.");
					return true;
				}
				playerDeathDrops.setHeader(args[0] + " Death Drops");
				playerDeathDrops.setFooter("deathdrops " + args[0] + " next");
				playerDeathDrops.setPaginateString(getDeathDropsMenu(dropsList));
				if(args.length == 1) {
					playerDeathDrops.sendPage(1, sender);
				}
				if(args.length > 1) {
					if(args[1].equalsIgnoreCase("next")) {
						if(playerDeathDrops.pageTotal() > 1) {
							playerDeathDrops.sendPage(2, sender);
						} else {
							sender.sendMessage(ChatColor.RED + "There's not another page!");
						}
						return true;
					}
					if(args[1].equalsIgnoreCase("setmyinv")) {
						if(args.length == 3) {
							Player player = (Player) sender;
							int ddNum = 0;
							try {
								ddNum = Integer.parseInt(args[2])-1;
							} catch (Exception e) {
								sender.sendMessage(ChatColor.RED + "Thats not a valid number!");
								return true;
							}

							DeathDrops playerDD = dropsList.get(ddNum);

							if(playerDD.hasArmor()) {

								//get and set inventory
								List<ItemStack> drops = playerDD.getDropsSanArmor();
								ItemStack[] dropArray = new ItemStack[drops.size()];
								drops.toArray(dropArray);
								player.getInventory().setContents(dropArray);

								//get armor
								ItemStack[] armor = new ItemStack[playerDD.getArmor().size()];
								List<ItemStack> armorList = playerDD.getArmor();
								armorList.toArray(armor);
								System.out.println(armor.length);
								System.out.println(armor[0].getType().toString());

								//set armor
								for(int i = 0; i < armor.length; i++) {
									if(armor[i].getType().toString().toLowerCase().contains("helmet")) {
										player.getInventory().setHelmet(armor[i]);
									}
									if(armor[i].getType().toString().toLowerCase().contains("chestplate")) {
										player.getInventory().setChestplate(armor[i]);
									}
									if(armor[i].getType().toString().toLowerCase().contains("leggings")) {
										player.getInventory().setLeggings(armor[i]);
									}
									if(armor[i].getType().toString().toLowerCase().contains("boots")) {
										player.getInventory().setBoots(armor[i]);
									}
								}
								sender.sendMessage(ChatColor.AQUA + "You have set your inventory to " + args[0] + "'s death drop number " + args[2]);
								return true;
							}
							List<ItemStack> dd = playerDD.getDrops();
							ItemStack[] dropArray = new ItemStack[dd.size()];
							dd.toArray(dropArray);
							sender.sendMessage(ChatColor.AQUA + "You have set your inventory to " + args[0] + "'s death drop number " + args[2]);
							player.getInventory().setContents(dropArray);
							return true;
						} else {
							sender.sendMessage(ChatColor.RED + "Please include a deathdrop number from the list of " + args[0] + "'s deathdrops. Type \"/deathdrops " + args[0] + "\" to see that list.");
						}
						return true;
					}
					if(args[1].equalsIgnoreCase("setplayerinv")) {
						if(args.length == 3) {
							Player[] onlinePlayers = Bukkit.getOnlinePlayers();
							boolean foundPlayer = false;
							for(int i = 0; i < onlinePlayers.length; i++) {
								if(onlinePlayers[i].getName().equals(args[0])) {
									foundPlayer = true;
									break;
								}
							}
							if(!foundPlayer) {
								sender.sendMessage(ChatColor.RED + "Player is not online! You can set the invetory to yourself and use /inv playername");
								return true;
							}
							Player player = Bukkit.getPlayerExact(args[0]);
							try {
								int ddNum = Integer.parseInt(args[2])-1;
								DeathDrops playerDD = dropsList.get(ddNum);
								if(playerDD.hasArmor()) {

									//get and set inventory
									List<ItemStack> drops = playerDD.getDropsSanArmor();
									ItemStack[] dropArray = new ItemStack[drops.size()];
									drops.toArray(dropArray);
									player.getInventory().setContents(dropArray);

									//get armor
									ItemStack[] armor = new ItemStack[playerDD.getArmor().size()];
									List<ItemStack> armorList = playerDD.getArmor();
									armorList.toArray(armor);

									//set armor
									for(int i = 0; i < armor.length; i++) {
										if(armor[i].getType().toString().toLowerCase().contains("helmet")) {
											player.getInventory().setHelmet(armor[i]);
										}
										if(armor[i].getType().toString().toLowerCase().contains("chestplate")) {
											player.getInventory().setChestplate(armor[i]);
										}
										if(armor[i].getType().toString().toLowerCase().contains("leggings")) {
											player.getInventory().setLeggings(armor[i]);
										}
										if(armor[i].getType().toString().toLowerCase().contains("boots")) {
											player.getInventory().setBoots(armor[i]);
										}
									}
									sender.sendMessage(ChatColor.AQUA + "You have set " + args[0] + "'s inventoy to death drop number " + args[2]);
									player.sendMessage(ChatColor.AQUA + "A moderator has reset your inventoy to before a previous death.");
									return true;
								}
								List<ItemStack> dd = playerDD.getDrops();
								ItemStack[] dropArray = new ItemStack[dd.size()];
								dd.toArray(dropArray);
								sender.sendMessage(ChatColor.AQUA + "You have set " + args[0] + "'s inventory to death drop number " + args[2]);
								player.getInventory().setContents(dropArray);
								player.sendMessage(ChatColor.AQUA + "A moderator has reset your inventoy to before a previous death.");
								return true;
							} catch (Exception e) {
								sender.sendMessage(ChatColor.RED + "Thats not a valid number!");
							}
						} else {
							sender.sendMessage(ChatColor.RED + "Please include a deathdrop number from the list of " + args[0] + "'s deathdrops. Type \"/deathdrops " + args[0] + "\" to see that list.");
						}
						return true;
					}

					try {
						int ddNum = Integer.parseInt(args[1])-1;
						if(ddNum <= dropsList.size()) {
							DeathDrops dd = dropsList.get(ddNum);
							sender.sendMessage(ChatColor.GREEN + "-----" + ChatColor.YELLOW + args[0] + ChatColor.GREEN + " Death Drop #" + ChatColor.YELLOW + args[1] + ChatColor.GREEN + "-----");
							sender.sendMessage(ChatColor.GOLD + "Time: " + ChatColor.GRAY + dd.getDeathTime());
							sender.sendMessage(ChatColor.GOLD + "Location: " + ChatColor.GRAY + dd.getDeathLoc());
							sender.sendMessage(ChatColor.GOLD + "Reason: " + ChatColor.GRAY + dd.getReason());
							sender.sendMessage(ChatColor.GOLD + "Items: "  + ChatColor.DARK_AQUA + Utils.deathDropsItems(dd.getDrops()));
						} else {
							sender.sendMessage(ChatColor.RED + "Not a valid deathdrop number!");
						}
					} catch(Exception e) {
						sender.sendMessage(ChatColor.RED + "Not a valid deathdrop number!");
					}

				}
			}
		}
		if(args.length > 3) {
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
	public String getDeathDropsMenu(List<DeathDrops> dropsList) {
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < dropsList.size(); i++) {
			int dropNum = i + 1;
			DeathDrops dd = dropsList.get(i);
			sb.append(dropNum + ": " + dd.getDeathTime() + "  Drops: " + dd.getDrops().size() + "\n");
		}
		return sb.toString();
	}
	public void setLoginLocation(String playername, Location location) {
		ConfigAccessor playerData = new ConfigAccessor("players.yml");
		String loginLocStr = Integer.toString((int) location.getX()) + "," + Integer.toString((int) location.getY()) + "," + Integer.toString((int) location.getZ()) + "," + location.getWorld().getName();
		playerData.getConfig().set(playername + ".newLoginLoc", loginLocStr);
		playerData.saveConfig();
	}
}
