package com.mythicacraft.plugins.mythsentials.Compass;

import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.kitteh.vanish.staticaccess.VanishNoPacket;
import org.kitteh.vanish.staticaccess.VanishNotLoadedException;

import com.gmail.mythicacraft.mythicaspawn.MythicaSpawn;
import com.gmail.mythicacraft.mythicaspawn.SpawnManager;
import com.mythicacraft.plugins.mythsentials.Mythian;
import com.mythicacraft.plugins.mythsentials.Mythsentials;
import com.mythicacraft.plugins.mythsentials.Utilities.ConfigAccessor;
import com.mythicacraft.plugins.mythsentials.Utilities.Paginate;


public class CompassTarget implements CommandExecutor {

	public Mythsentials plugin;

	public CompassTarget(Mythsentials plugin) {
		this.plugin = plugin;
	}

	int taskId;

	World realm = Bukkit.getWorld("The_Realm");

	public boolean onCommand(final CommandSender sender, Command cmd, String commandLabel, String[] args) {

		ConfigAccessor playerData = new ConfigAccessor("players.yml");

		final Player p = (Player) sender;
		String playerName = sender.getName();
		Location home = p.getBedSpawnLocation();
		Paginate helpPage = new Paginate(helpMenu(),"Compass Commands");
		SpawnManager SM = MythicaSpawn.getSpawnManager();

		if(commandLabel.equalsIgnoreCase("compass") || commandLabel.equalsIgnoreCase("c")) {

			if(args.length == 0) {
				helpPage.setFooter(commandLabel + " NEXTPAGE");
				if(args.length >= 2) {
					try {
						int pageNumber = Integer.parseInt(args[1]);
						if(pageNumber <= helpPage.pageTotal()) {
							helpPage.sendPage(pageNumber, sender);
						} else {
							sender.sendMessage(ChatColor.RED + "Not a valid page number!");
						}
					} catch (Exception e) {
						sender.sendMessage(ChatColor.RED + "Not a valid page number!");
					}
				} else {
					helpPage.sendPage(1, sender);
				}
			}
			if(args.length >= 1) {
				if(numberCheck(args[0])) {
					helpPage.setFooter(commandLabel  + " NEXTPAGE");
					try {
						int pageNumber = Integer.parseInt(args[0]);
						if(pageNumber <= helpPage.pageTotal()) {
							helpPage.sendPage(pageNumber, sender);
						} else {
							sender.sendMessage(ChatColor.RED + "Not a valid page number!");
						}
					} catch (Exception e) {
						sender.sendMessage(ChatColor.RED + "Not a valid page number!");
					}
					return true;
				}
				if(args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("?")) {
					helpPage.setFooter(commandLabel + " " + args[0] + " NEXTPAGE");
					if(args.length >= 2) {
						try {
							int pageNumber = Integer.parseInt(args[1]);
							if(pageNumber <= helpPage.pageTotal()) {
								helpPage.sendPage(pageNumber, sender);
							} else {
								sender.sendMessage(ChatColor.RED + "Not a valid page number!");
							}
						} catch (Exception e) {
							sender.sendMessage(ChatColor.RED + "Not a valid page number!");
						}
					} else {
						helpPage.sendPage(1, sender);
					}
				} //end of help

				if(args[0].equalsIgnoreCase("current") || args[0].equalsIgnoreCase("c") || args[0].equalsIgnoreCase("currenttarget") || args[0].equalsIgnoreCase("ct")) {

					if(p.getWorld().getEnvironment() == Environment.NETHER) {
						sender.sendMessage(ChatColor.RED + "Compasses don't have targets in the nether!");
						return true;
					}

					/*if(plugin.compassInfoPanels.containsKey(p)) {
						plugin.compassInfoPanels.get(p).cancel();
						plugin.compassInfoPanels.remove(p);
						p.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
						sender.sendMessage(ChatColor.GREEN + "[Compass] Hiding current target info panel.");
					} else {
						plugin.compassInfoPanels.put(p, new CompassInfoPanel(p).runTaskTimerAsynchronously(plugin, 1L, 10L));
						sender.sendMessage(ChatColor.GREEN + "[Compass] Showing current target info panel. Type \"/c current\" to hide.");
					}
					 */
					sender.sendMessage(ChatColor.GREEN + "Compass current info panel has been momentarily disabled. (It's been found to cause lag). Try again soon!");

				} //end of current

				if(args[0].equalsIgnoreCase("targets") || args[0].equalsIgnoreCase("t")) {
					StringBuilder sb = new StringBuilder();
					sb.append(ChatColor.YELLOW + "Presets:\n");
					sb.append(ChatColor.AQUA + "Spawn" + ChatColor.GRAY + " - Spawn.\n");
					sb.append(ChatColor.AQUA + "Home" + ChatColor.GRAY + " - The last bed you slept in.\n");
					sb.append(ChatColor.AQUA + "Death" + ChatColor.GRAY + " - Your last death point.\n");
					sb.append(ChatColor.AQUA + "Online player's name" + ChatColor.GRAY + " - Point to player's current position.\n");
					if(playerData.getConfig().contains(playerName + ".compassTargets")) {
						Set<String> playerTargets = null;
						playerTargets = playerData.getConfig().getConfigurationSection(playerName + ".compassTargets").getKeys(false);
						if(playerTargets != null) {
							if(!playerTargets.isEmpty()) {
								sb.append(ChatColor.YELLOW + "Your Saved Targets: \n");
							}
							for (String targetName : playerTargets) {
								String targetInfo = playerData.getConfig().getString(playerName + ".compassTargets." + targetName);
								String[] points = targetInfo.split(",");
								targetInfo = "x:" + points[0] + ", y:" + points[1] + ", z:" + points[2] + ", world: " + points[3] + "\n";
								sb.append(ChatColor.AQUA + targetName + ": " + ChatColor.GRAY + targetInfo);
							}
						}
					}

					Paginate tarPages = new Paginate(sb.toString());
					tarPages.setHeader("Compass Target Names");
					tarPages.setFooter(commandLabel + " " + args[0]  + " NEXTPAGE");

					if(args.length >= 2) {
						try {
							int pageNumber = Integer.parseInt(args[1]);
							if(pageNumber <= tarPages.pageTotal()) {
								tarPages.sendPage(pageNumber, sender);
							} else {
								sender.sendMessage(ChatColor.RED + "Not a valid page number!");
							}
						} catch (Exception e) {
							sender.sendMessage(ChatColor.RED + "Not a valid page number!");
						}
					} else {
						tarPages.sendPage(1, sender);
					}

				} //end of targets

				if(args[0].equalsIgnoreCase("savetarget") || args[0].equalsIgnoreCase("savet") || args[0].equalsIgnoreCase("savetar")) {
					if(args[1].length() > 16) {
						sender.sendMessage(ChatColor.RED + "Your target name is too long! (16 character max)");
						return true;
					}
					if(p.getWorld().getEnvironment() == Environment.NETHER) {
						sender.sendMessage(ChatColor.RED + "Cannot save custom targets in nether as compasses do not work in the nether!");
						return true;
					}
					if(args.length == 1) {
						sender.sendMessage(ChatColor.RED + "You must give a name for a new target. /compass savetarget [name]");
						return true;
					}
					if(args.length >= 2) {
						Location currentLoc = p.getLocation();
						String worldName = p.getWorld().getName();
						String newTarget = Integer.toString((int) currentLoc.getX()) + "," + Integer.toString((int) currentLoc.getY()) + "," + Integer.toString((int) currentLoc.getZ()) + "," + worldName;
						if(playerData.getConfig().contains(playerName + ".compassTargets." + args[1])) {
							sender.sendMessage(ChatColor.RED + "You already have a target saved with this name!");
							return true;
						}
						playerData.getConfig().set(playerName + ".compassTargets." + args[1], newTarget);
						playerData.saveConfig();
						sender.sendMessage(ChatColor.GREEN + "You have saved the custom target \"" + ChatColor.YELLOW + args[1] + ChatColor.GREEN + "\" based off your location.");
					}
				}// end of addtarget

				if(args[0].equalsIgnoreCase("deletetarget") || args[0].equalsIgnoreCase("dt") || args[0].equalsIgnoreCase("deltar") || args[0].equalsIgnoreCase("delt") || args[0].equalsIgnoreCase("deletet")) {
					if(args.length >= 2) {
						if (playerData.getConfig().contains(playerName + ".compassTargets")) {
							Map<String, Object> targets = null;
							targets = playerData.getConfig().getConfigurationSection(playerName + ".compassTargets").getValues(false);
							if (targets != null) {
								if(targets.containsKey(args[1])) {
									targets.remove(args[1]);
									playerData.getConfig().set(playerName + ".compassTargets", targets);
									playerData.saveConfig();
									sender.sendMessage(ChatColor.GREEN + "Deleted custom target: " + ChatColor.YELLOW + args[1]);
								} else {
									sender.sendMessage(ChatColor.RED + "Not a valid target name!");
								}
								return true;
							}
						}
					}
				}// end of deletetarget

				if(args[0].equalsIgnoreCase("renametarget") || args[0].equalsIgnoreCase("rt") || args[0].equalsIgnoreCase("renamet") || args[0].equalsIgnoreCase("renametar")) {
					if(args.length < 3) {
						sender.sendMessage(ChatColor.RED + "You must specify the target and it's new name. /compass addtarget [oldName] [newName]");
						return true;
					}
					if(args[2].length() > 15) {
						sender.sendMessage(ChatColor.RED + "Your new target name is too long! (15 character max)");
						return true;
					}
					if(args.length == 3) {
						if (playerData.getConfig().contains(playerName + ".compassTargets")) {
							Map<String, Object> targets = null;
							targets = playerData.getConfig().getConfigurationSection(playerName + ".compassTargets").getValues(false);
							if (targets != null) {
								if(targets.containsKey(args[1])) {
									targets.put(args[2], targets.get(args[1]));
									targets.remove(args[1]);
									playerData.getConfig().set(playerName + ".compassTargets", targets);
									playerData.saveConfig();
									sender.sendMessage(ChatColor.GREEN + "rename custom target " + ChatColor.YELLOW + args[0] + ChatColor.GREEN + " to: " + ChatColor.YELLOW + args[2]);
								} else {
									sender.sendMessage(ChatColor.RED + "Not a valid target name!");
								}
								return true;
							}
						}
					}
				}//end of renametarget

				if(args[0].equalsIgnoreCase("sharetarget") || args[0].equalsIgnoreCase("sharet") || args[0].equalsIgnoreCase("sharetar")) {
					if(args.length <= 2) {
						sender.sendMessage(ChatColor.RED + "You must include the custom target name and a players name! /compass sharetarget [targetname] [playername]");
						return true;
					}
					if(args.length == 3) {
						if(playerData.getConfig().contains(playerName + ".compassTargets." + args[1])) {
							String target = playerData.getConfig().getString(playerName + ".compassTargets." + args[1]);
							String reciever = completeName(args[2]);

							if(reciever == null) {
								sender.sendMessage(ChatColor.RED + "Could not find a known player with: " + args[2] + ".");
								return true;
							}

							if(reciever.equals(sender.getName())) {
								sender.sendMessage(ChatColor.RED + "Please enter a player name that isn't you.");
								return true;
							}

							if (playerData.getConfig().contains(reciever + ".compassTargets")) {
								Map<String, Object> targets = null;
								targets = playerData.getConfig().getConfigurationSection(reciever + ".compassTargets").getValues(false);
								if (targets != null) {
									if(targets.containsValue(target)) {
										sender.sendMessage(ChatColor.YELLOW + reciever + ChatColor.RED + " already has a target at " + ChatColor.YELLOW + args[1] + ChatColor.RED + "'s location!");
										return true;
									}
								}
							}

							int counter = 2;
							while(playerData.getConfig().contains(reciever + ".compassTargets." + args[1])) {
								StringBuilder sb = new StringBuilder();
								sb.append(args[1]);
								if(numberCheck(Character.toString(sb.charAt(sb.length() - 1)))) {
									sb.deleteCharAt(sb.length() - 1);
									sb.append(counter);
								} else {
									sb.append("_" + counter);
								}
								args[1] = sb.toString();
								counter++;
							}

							playerData.getConfig().set(reciever + ".compassTargets." + args[1], target);
							playerData.saveConfig();
							sender.sendMessage(ChatColor.GREEN + "Shared " + ChatColor.YELLOW + args[1] + ChatColor.GREEN + " with " + ChatColor.YELLOW + reciever + ChatColor.GREEN + ".");

							Player recieverP = Bukkit.getPlayer(reciever);
							if(recieverP != null && recieverP.isOnline()) {
								recieverP.sendMessage(ChatColor.YELLOW + sender.getName() + ChatColor.GREEN + " just shared custom compass target \"" + ChatColor.YELLOW + args[1] + ChatColor.GREEN + "\" with you! See /compass targets");
							}
							return true;
						} else {
							sender.sendMessage(ChatColor.RED + "You don't have a target with that name!");
							return true;
						}
					}
				}//end of sharetarget

				if(args[0].equalsIgnoreCase("point") || args[0].equalsIgnoreCase("p")) {
					if(args.length == 1) {
						sender.sendMessage(ChatColor.RED + "You need to specify a target. See /compass targets");
						return true;
					}
					if(p.getWorld().getEnvironment() == Environment.NETHER) {
						sender.sendMessage(ChatColor.RED + "Compasses do not work in the nether!");
						return true;
					}
					Mythian mythian = Mythsentials.getMythianManager().getMythian(playerName);
					Location death = mythian.getLastDeathLoc();
					switch(args[1].toLowerCase()) {
					case "spire":
						if(plugin.playerTrackers.containsKey(p)) {
							plugin.playerTrackers.get(p).cancel();
							plugin.playerTrackers.remove(p);
						}
						p.setCompassTarget(SM.getSurvivalSpawn());
						sender.sendMessage(ChatColor.GREEN + "Pointed compass to " + ChatColor.YELLOW + args[1] + ChatColor.GREEN + ".");
						return true;
					case "spawn":
						if(plugin.playerTrackers.containsKey(p)) {
							plugin.playerTrackers.get(p).cancel();
							plugin.playerTrackers.remove(p);
						}
						String worldType = SM.getWorldType(p.getWorld());
						if(worldType.equalsIgnoreCase("survival")) {
							p.setCompassTarget(SM.getSurvivalSpawn());
						}
						else if(worldType.equalsIgnoreCase("pvp")) {
							p.setCompassTarget(SM.getPvpSpawn());
						} else {
							p.setCompassTarget(SM.getInitialSpawn());
						}
						sender.sendMessage(ChatColor.GREEN + "Pointed compass to " + ChatColor.YELLOW + args[1] + ChatColor.GREEN + ".");
						return true;
					case "home":
						if(home != null) {
							p.setCompassTarget(home);
							sender.sendMessage(ChatColor.GREEN + "Pointed compass to " + ChatColor.YELLOW + args[1] + ChatColor.GREEN + ".");
							if(plugin.playerTrackers.containsKey(p)) {
								plugin.playerTrackers.get(p).cancel();
								plugin.playerTrackers.remove(p);
							}
						} else {
							p.sendMessage(ChatColor.RED + "Home/bed not set!");
						}
						return true;
					case "death":
						if(death != null) {
							if(plugin.playerTrackers.containsKey(p)) {
								plugin.playerTrackers.get(p).cancel();
								plugin.playerTrackers.remove(p);
							}
							p.setCompassTarget(death);
							sender.sendMessage(ChatColor.GREEN + "Pointed compass to " + ChatColor.YELLOW + args[1] + ChatColor.GREEN + ".");
						} else {
							sender.sendMessage(ChatColor.RED + "Could not find a previous death.");
						}
						return true;
					case "deathpoint":
						if(death != null) {
							if(plugin.playerTrackers.containsKey(p)) {
								plugin.playerTrackers.get(p).cancel();
								plugin.playerTrackers.remove(p);
							}
							p.setCompassTarget(death);
							sender.sendMessage(ChatColor.GREEN + "Pointed compass to " + ChatColor.YELLOW + args[1] + ChatColor.GREEN + ".");
						} else {
							sender.sendMessage(ChatColor.RED + "Could not find a previous death.");
						}
						return true;
					default:
						//check if arg[1] matches a custom target first
						Set<String> targets = null;
						if (playerData.getConfig().contains(playerName + ".compassTargets")) {
							targets = playerData.getConfig().getConfigurationSection(playerName + ".compassTargets").getKeys(false);
							if (targets != null) {
								for (String targetName : targets) {
									if(targetName.equals(args[1])) {
										if(plugin.playerTrackers.containsKey(p)) {
											plugin.playerTrackers.get(p).cancel();
											plugin.playerTrackers.remove(p);
										}
										String[] points = playerData.getConfig().getString(playerName + ".compassTargets." + targetName).split(",");
										World targetWorld = Bukkit.getWorld(points[3]);
										Location targetLoc = new Location(targetWorld, Double.parseDouble(points[0]), Double.parseDouble(points[1]), Double.parseDouble(points[2]));
										p.setCompassTarget(targetLoc);
										sender.sendMessage(ChatColor.GREEN + "Pointed compass to " + ChatColor.YELLOW + args[1] + ChatColor.GREEN + ".");
										return true;
									}
								}
							}
						}

						//then check for a playername
						String playername = completeName(args[1]);
						if(playername == null) {
							sender.sendMessage(ChatColor.RED + "Could not find a target for: " + args[1] + ", Check /compass targets");
							break;
						}

						if(playername.equals(sender.getName())) {
							sender.sendMessage(ChatColor.RED + "No need to track yourself!");
							return true;
						}

						Player target = Bukkit.getPlayer(playername);
						if(target == null) {
							sender.sendMessage(ChatColor.RED + "Player is not online!");
							return true;
						}

						if(target != null && target.isOnline()) {
							try {
								if(VanishNoPacket.isVanished(target.getDisplayName())) {
									if(!sender.hasPermission("mythica.mod")) {
										sender.sendMessage(ChatColor.RED + "Player target not available!");
										return true;
									}
								}
							} catch (VanishNotLoadedException e) {
								e.printStackTrace();
							}
							if(target.getWorld() != p.getWorld()) {
								sender.sendMessage(ChatColor.RED + "Cannot point compass to player, target is in a different world!");
								return true;
							}
							if(plugin.playerTrackers.containsKey(p)) {
								plugin.playerTrackers.get(p).cancel();
								plugin.playerTrackers.remove(p);
							}
							sender.sendMessage(ChatColor.GREEN + "Tracking online players has been momentarily disabled. (It's been found to cause lag and has been exploted in PvP universe). Try again soon!");
							//sender.sendMessage(ChatColor.GREEN + "Pointed compass to track " + ChatColor.YELLOW + playername + ChatColor.GREEN + ".");
							//plugin.playerTrackers.put(p, new PlayerTracker(plugin, p, target).runTaskTimer(plugin, 1L, 10L));
							//playerData.getConfig().set(playerName + ".playerTrack", playername);
							//playerData.saveConfig();
							return true;
						}
					}
				}//end of point
			}//end of args >= 1
		}//end of compass command
		return true;
	}//end of command boolean

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

	public boolean numberCheck(String args) {
		Pattern checkRegex = Pattern.compile("[\\d]");
		Matcher regexMatcher = checkRegex.matcher(args);
		if(regexMatcher.find()) {
			return true;
		}
		return false;
	}

	String helpMenu() {
		StringBuilder sb = new StringBuilder();
		sb.append(ChatColor.AQUA + "/Compass Current" + ChatColor.GRAY + " - Displays your compass's current target\n");
		sb.append(ChatColor.AQUA + "/Compass Targets" + ChatColor.GRAY + " - Shows saved target names you can point your compass to.\n");
		sb.append(ChatColor.AQUA + "/Compass Point [TargetName]" + ChatColor.GRAY + " - Points your compass to the given target.\n");
		sb.append(ChatColor.AQUA + "/Compass SaveTarget [TargetName]" + ChatColor.GRAY + " - Saves a new custom target at your location.\n");
		sb.append(ChatColor.AQUA + "/Compass RenameTarget [OldName] [NewName]" + ChatColor.GRAY + " - Renames a custom target.\n");
		sb.append(ChatColor.AQUA + "/Compass DeleteTarget [TargetName]" + ChatColor.GRAY + " - Deletes the custom target.\n");
		sb.append(ChatColor.AQUA + "/Compass ShareTarget [TargetName] [PlayerName]" + ChatColor.GRAY + " - Shares target with another player.\n");
		sb.append(ChatColor.YELLOW + "Tip: " + ChatColor.WHITE +" You can use \"/c\" in place of \"/compass\" for all commands.\n" + ChatColor.YELLOW + "Tip: " + ChatColor.WHITE + "\n Subcommands can be shortened as well: t = Targets, p = Point, savet = SaveTarget, rt = RenameTarget, dt = DeleteTarget, sharet = ShareTarget, c = Current\n");
		sb.append(ChatColor.YELLOW + "Examples: \n" + ChatColor.WHITE + "/c point Spire \n/c point ebiggz\n/c savet MyMine");
		return sb.toString();
	}
}
