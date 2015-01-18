package com.mythicacraft.plugins.mythsentials.Compass;

import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scoreboard.DisplaySlot;
import org.kitteh.vanish.staticaccess.VanishNoPacket;
import org.kitteh.vanish.staticaccess.VanishNotLoadedException;

import com.gmail.mythicacraft.mythicaspawn.MythicaSpawn;
import com.gmail.mythicacraft.mythicaspawn.SpawnManager;
import com.gmail.mythicacraft.mythicaspawn.SpawnManager.Universe;
import com.mythicacraft.plugins.mythsentials.Mythian;
import com.mythicacraft.plugins.mythsentials.Mythsentials;
import com.mythicacraft.plugins.mythsentials.Utilities.Paginate;
import com.mythicacraft.plugins.mythsentials.Utilities.Utils;


public class CompassTarget implements CommandExecutor {

	public Mythsentials plugin;

	public CompassTarget(Mythsentials plugin) {
		this.plugin = plugin;
	}

	int taskId;

	World realm = Bukkit.getWorld("The_Realm");

	public boolean onCommand(final CommandSender sender, Command cmd, String commandLabel, String[] args) {

		//ConfigAccessor playerData = new ConfigAccessor("players.yml");

		final Player p = (Player) sender;
		String playerName = sender.getName();
		Location home = p.getBedSpawnLocation();
		Paginate helpPage = new Paginate(helpMenu(),"Compass Commands");
		SpawnManager SM = MythicaSpawn.getSpawnManager();

		Mythian mythian = Mythsentials.getMythianManager().getMythian(playerName);

		if(commandLabel.equalsIgnoreCase("sure")) {
			if(plugin.trackRequests.containsKey(p)) {
				Player tracker = plugin.trackRequests.get(p);
				plugin.trackRequests.remove(p);
				if(tracker.isOnline()) {
					Player currentTrack = Utils.getTrackingPlayer(tracker);
					if(currentTrack != null) {
						plugin.playerTargets.get(currentTrack).removeTracker(tracker);
					}
					if(plugin.playerTargets.containsKey(p)) {
						plugin.playerTargets.get(p).addTracker(tracker);
					} else {
						plugin.playerTargets.put(p, new PlayerTarget(tracker, p, plugin));
					}
					sender.sendMessage(ChatColor.GREEN + tracker.getName() + " is now tracking you with their compass. Type " + ChatColor.YELLOW + "/stoptrack" + ChatColor.GREEN + " to stop them from tracking you. Type " + ChatColor.YELLOW + "/trackers" + ChatColor.GREEN +" to see all players currently compass tracking you.");
					tracker.sendMessage(ChatColor.GREEN + sender.getName() + " has accepted your request. Pointing compass...");
					tracker.setCompassTarget(p.getLocation());
					if(plugin.compassInfoPanels.containsKey(tracker)) {
						plugin.compassInfoPanels.get(tracker).setTargetName(p.getName());
						plugin.compassInfoPanels.get(tracker).run();
					}

				} else {
					sender.sendMessage(ChatColor.RED + "Player no longer online!");
				}
			} else {
				sender.sendMessage(ChatColor.RED + "No one has requested to compass track you!");
			}
		}
		if(commandLabel.equalsIgnoreCase("nah")) {
			if(plugin.trackRequests.containsKey(p)) {
				Player tracker = plugin.trackRequests.get(p);
				plugin.trackRequests.remove(p);
				if(tracker.isOnline()) {
					sender.sendMessage(ChatColor.GREEN + "You have denied the compass track request.");
					tracker.sendMessage(ChatColor.RED + sender.getName() + " denied the compass track request.");
				} else {
					sender.sendMessage(ChatColor.RED + "Player no longer online!");
				}
			} else {
				sender.sendMessage(ChatColor.RED + "No one has requested to compass track you!");
			}
		}

		if(commandLabel.equalsIgnoreCase("creep")) {
			if(plugin.trackRequests.containsKey(p)) {
				Player tracker = plugin.trackRequests.get(p);
				plugin.trackRequests.remove(p);
				if(tracker.isOnline()) {
					sender.sendMessage(ChatColor.GREEN + "You have denied the compass track request and they have been told they are a creep.");
					tracker.sendMessage(ChatColor.RED + sender.getName() + " thinks you are a creep. Request denied.");
				} else {
					sender.sendMessage(ChatColor.RED + "Player no longer online!");
				}
			} else {
				sender.sendMessage(ChatColor.RED + "No one has requested to compass track you!");
			}
		}

		if(commandLabel.equalsIgnoreCase("trackers")) {
			if(plugin.playerTargets.containsKey(p)) {
				List<Player> trackers = plugin.playerTargets.get(p).getTrackers();
				StringBuilder sb = new StringBuilder();
				for(Player tracker : trackers) {
					sb.append(tracker.getName());
					sb.append(", ");
				}
				sb.delete(sb.length()-2, sb.length()-1);
				sender.sendMessage(ChatColor.GREEN + "People compass tracking you: " + ChatColor.YELLOW + sb.toString());
			} else {
				sender.sendMessage(ChatColor.GREEN + "No one is currently tracking you.");
			}
		}

		if(commandLabel.equalsIgnoreCase("stoptrack")) {
			if(args.length == 0) {
				sender.sendMessage(ChatColor.GREEN +"Type " + ChatColor.YELLOW + "/stoptrack all" + ChatColor.GREEN + " or " + ChatColor.YELLOW + "/stoptrack [player]" + ChatColor.GREEN + " to stop people from compass tracking you.");
				if(plugin.playerTargets.containsKey(p)) {
					List<Player> trackers = plugin.playerTargets.get(p).getTrackers();
					StringBuilder sb = new StringBuilder();
					for(Player tracker : trackers) {
						sb.append(tracker.getName());
						sb.append(", ");
					}
					sb.delete(sb.length()-2, sb.length()-1);
					sender.sendMessage(ChatColor.GREEN + "People compass tracking you: " + ChatColor.YELLOW + sb.toString());
				} else {
					sender.sendMessage(ChatColor.GRAY + "No one is currently tracking you");
				}
			} else {
				if(args[0].equalsIgnoreCase("all")) {
					if(plugin.playerTargets.containsKey(p)) {
						List<Player> trackers = plugin.playerTargets.get(p).getTrackers();
						for(int i = 0; i < trackers.size(); i++) {
							Player tracker = trackers.get(i);
							plugin.playerTargets.get(p).removeTracker(tracker);
							tracker.setCompassTarget(tracker.getWorld().getSpawnLocation());
							tracker.sendMessage(ChatColor.RED + "Player is no longer available to track! Resetting compass...");
							if(plugin.compassInfoPanels.containsKey(tracker)) {
								plugin.compassInfoPanels.get(tracker).setTargetName("Spawn");
								plugin.compassInfoPanels.get(tracker).run();
							}
						}
						return true;
					} else {
						sender.sendMessage(ChatColor.RED + "No one is tracking you!");
						return true;
					}
				}
				String playername = completeName(args[1]);
				Player tracker = Bukkit.getPlayer(playername);
				if(tracker == null) {
					sender.sendMessage(ChatColor.RED + "Not a valid player!");
					return true;
				}
				if(plugin.playerTargets.containsKey(p)) {
					if(plugin.playerTargets.get(p).hasTracker(tracker)) {
						plugin.playerTargets.get(p).removeTracker(tracker);
						tracker.sendMessage(ChatColor.RED + "Player no longer available to track! Resetting compass...");
						tracker.setCompassTarget(tracker.getWorld().getSpawnLocation());
						if(plugin.compassInfoPanels.containsKey(tracker)) {
							plugin.compassInfoPanels.get(tracker).setTargetName("Spawn");
							plugin.compassInfoPanels.get(tracker).run();
						}
					} else {
						sender.sendMessage(ChatColor.RED + "This player is not tracking you!");
					}
				} else {
					sender.sendMessage(ChatColor.RED + "No one is tracking you!");
				}
			}
		}


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

					if(plugin.compassInfoPanels.containsKey(p)) {
						plugin.compassInfoPanels.remove(p);
						p.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
						sender.sendMessage(ChatColor.GREEN + "[Compass] Hiding current target info panel.");
					} else {
						if(!p.getInventory().contains(Material.COMPASS)) {
							p.sendMessage(ChatColor.RED + "A compass is required to be in your inventory to use the info panel.");
							return true;
						}
						CompassInfoPanel cip = new CompassInfoPanel(p);
						cip.run();
						plugin.compassInfoPanels.put(p, cip);
						sender.sendMessage(ChatColor.GREEN + "[Compass] Showing current target info panel. Type \"/c current\" to hide.");
					}

					//sender.sendMessage(ChatColor.GREEN + "Compass current info panel has been momentarily disabled. (It's been found to cause lag). Try again soon!");

				} //end of current

				if(args[0].equalsIgnoreCase("targets") || args[0].equalsIgnoreCase("t")) {
					StringBuilder sb = new StringBuilder();
					sb.append(ChatColor.YELLOW + "Presets:\n");
					sb.append(ChatColor.AQUA + "Spawn" + ChatColor.GRAY + " - Spawn.\n");
					sb.append(ChatColor.AQUA + "Home" + ChatColor.GRAY + " - The last bed you slept in.\n");
					sb.append(ChatColor.AQUA + "Death" + ChatColor.GRAY + " - Your last death point.\n");
					sb.append(ChatColor.AQUA + "Online player's name" + ChatColor.GRAY + " - Point to player's current position.\n");
					Set<String> playerTargets = null;
					playerTargets = mythian.getCompassTargetNames();
					if(playerTargets != null) {

						if(!playerTargets.isEmpty()) {
							sb.append(ChatColor.YELLOW + "Your Saved Targets: \n");
						}
						for (String targetName : playerTargets) {
							Location target = mythian.getCompassTarget(targetName);
							String targetInfo = "x:" + target.getBlockX() + ", y:" + target.getBlockY() + ", z:" + target.getBlockZ() + ", world: " + getReadableName(target.getWorld().getName()) + "\n";
							sb.append(ChatColor.AQUA + targetName + ": " + ChatColor.GRAY + targetInfo);
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
					if(args.length == 1) {
						sender.sendMessage(ChatColor.RED + "You must give a name for a new target. \"/compass savetarget [name]\" or \"/compass savetarget [name] [x] [y] [z] [world]");
						return true;
					}
					if(args[1].length() > 16) {
						sender.sendMessage(ChatColor.RED + "Your target name is too long! (16 character max)");
						return true;
					}
					if(mythian.hasCompassTarget(args[1])) {
						sender.sendMessage(ChatColor.RED + "You already have a target saved with this name!");
						return true;
					}
					if(args.length == 2) {
						if(p.getWorld().getEnvironment() == Environment.NETHER) {
							sender.sendMessage(ChatColor.RED + "Cannot save custom targets in nether as compasses do not work in the nether!");
							return true;
						}
						mythian.saveCompassTarget(args[1], p.getLocation());
						sender.sendMessage(ChatColor.GREEN + "You have saved the custom target \"" + ChatColor.YELLOW + args[1] + ChatColor.GREEN + "\" based off your location.");
					}
					if(args.length == 5) {
						if(p.getWorld().getEnvironment() == Environment.NETHER) {
							sender.sendMessage(ChatColor.RED + "Cannot save custom targets in nether as compasses do not work in the nether!");
							return true;
						}
						try {
							Location customLoc = new Location(p.getWorld(), Double.parseDouble(args[2]), Double.parseDouble(args[3]), Double.parseDouble(args[4]));
							mythian.saveCompassTarget(args[1], customLoc);
							sender.sendMessage(ChatColor.GREEN + "You have saved the custom target \"" + ChatColor.YELLOW + args[1] + ChatColor.GREEN + "\" at the coordinates " + ChatColor.YELLOW + args[2] + ", " + args[3] + ", " + args[4] + ChatColor.GREEN + " in your current world.");
						} catch (Exception e) {
							sender.sendMessage(ChatColor.RED + "There was an error converting the coordinates into numbers. Did you type them correctly?");
						}
					}
					if(args.length == 6) {
						String worldName = this.getWorldName(args[5]);
						if(worldName.equalsIgnoreCase("unknown")) {
							sender.sendMessage(ChatColor.RED + "Not a recognized world. Available worlds: Survival, SurvivalTemp, SurvivalEnd, Pvp, PvpTemp, PvpEnd");
							return true;
						}
						World customWorld = Bukkit.getWorld(worldName);
						try {
							Location customLoc = new Location(customWorld, Double.parseDouble(args[2]), Double.parseDouble(args[3]), Double.parseDouble(args[4]));
							mythian.saveCompassTarget(args[1], customLoc);
							sender.sendMessage(ChatColor.GREEN + "You have saved the custom target \"" + ChatColor.YELLOW + args[1] + ChatColor.GREEN + "\" at the coordinates " + ChatColor.YELLOW + args[2] + ", " + args[3] + ", " + args[4] + ChatColor.GREEN + " in the world " + ChatColor.YELLOW + getReadableName(worldName) + ChatColor.GREEN + ".");
						} catch (Exception e) {
							sender.sendMessage(ChatColor.RED + "There was an error converting the coordinates into numbers. Did you type them correctly?");
						}
					}
				}// end of addtarget

				if(args[0].equalsIgnoreCase("deletetarget") || args[0].equalsIgnoreCase("dt") || args[0].equalsIgnoreCase("deltar") || args[0].equalsIgnoreCase("delt") || args[0].equalsIgnoreCase("deletet")) {
					if(args.length >= 2) {
						if(!mythian.hasCompassTarget(args[1])) {
							sender.sendMessage(ChatColor.RED + "Not a valid target name!");
							return true;
						}
						mythian.removeCompassTarget(args[1]);
						sender.sendMessage(ChatColor.GREEN + "Deleted custom target: " + ChatColor.YELLOW + args[1]);
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
						if(!mythian.hasCompassTarget(args[1])) {
							sender.sendMessage(ChatColor.RED + "Not a valid target name!");
							return true;
						}
						mythian.renameCompassTarget(args[1], args[2]);
						sender.sendMessage(ChatColor.GREEN + "Renamed custom target " + ChatColor.YELLOW + args[0] + ChatColor.GREEN + " to: " + ChatColor.YELLOW + args[2]);
					}
				}//end of renametarget

				if(args[0].equalsIgnoreCase("sharetarget") || args[0].equalsIgnoreCase("sharet") || args[0].equalsIgnoreCase("sharetar")) {
					if(args.length <= 2) {
						sender.sendMessage(ChatColor.RED + "You must include the custom target name and a players name! /compass sharetarget [targetname] [playername]");
						return true;
					}
					if(args.length == 3) {
						if(mythian.hasCompassTarget(args[1])) {
							String reciever = completeName(args[2]);

							if(reciever == null) {
								sender.sendMessage(ChatColor.RED + "Could not find a known player with: " + args[2] + ".");
								return true;
							}

							if(reciever.equals(sender.getName())) {
								sender.sendMessage(ChatColor.RED + "Please enter a player name that isn't you.");
								return true;
							}
							Mythian otherMythian = Mythsentials.getMythianManager().getMythian(reciever);
							if(otherMythian.hasCompassTarget(args[1])) {
								sender.sendMessage(ChatColor.YELLOW + reciever + ChatColor.RED + " already has a target with that name!");
								return true;
							}

							otherMythian.saveCompassTarget(args[1], mythian.getCompassTarget(args[1]));
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
					if(!p.getInventory().contains(Material.COMPASS)) {
						sender.sendMessage(ChatColor.RED + "You must have a compass in your inventory to use this command!");
						return true;
					}
					Location death = mythian.getLastDeathLoc();
					Player currentTrack = Utils.getTrackingPlayer(p);
					switch(args[1].toLowerCase()) {
					case "spire":
						if(currentTrack != null) {
							plugin.playerTargets.get(currentTrack).removeTracker(p);
						}
						p.setCompassTarget(SM.getSurvivalSpawn());
						sender.sendMessage(ChatColor.GREEN + "Pointed compass to " + ChatColor.YELLOW + args[1] + ChatColor.GREEN + ".");
						if(plugin.compassInfoPanels.containsKey(p)) {
							plugin.compassInfoPanels.get(p).setTargetName(args[1]);
							plugin.compassInfoPanels.get(p).run();
						}
						return true;
					case "spawn":
						if(currentTrack != null) {
							plugin.playerTargets.get(currentTrack).removeTracker(p);
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
						if(plugin.compassInfoPanels.containsKey(p)) {
							plugin.compassInfoPanels.get(p).setTargetName(args[1]);
							plugin.compassInfoPanels.get(p).run();
						}
						return true;
					case "bed":
					case "home":
						Universe playerUniverse = SM.getWorldsUniverse(p.getWorld());
						if(playerUniverse == Universe.SURVIVAL) {
							home = SM.getPlayerSurvivalBed(p);
						} else {
							home = SM.getPlayerPvpBed(p);
						}
						if(home != null) {
							p.setCompassTarget(home);
							sender.sendMessage(ChatColor.GREEN + "Pointed compass to " + ChatColor.YELLOW + args[1] + ChatColor.GREEN + ".");
							if(currentTrack != null) {
								plugin.playerTargets.get(currentTrack).removeTracker(p);
							}
							if(plugin.compassInfoPanels.containsKey(p)) {
								Universe universe = MythicaSpawn.getSpawnManager().getWorldsUniverse(home.getWorld());
								if(universe == Universe.SURVIVAL) {
									plugin.compassInfoPanels.get(p).setTargetName("Survival Bed");
								} else {
									plugin.compassInfoPanels.get(p).setTargetName("PvP Bed");
								}
								plugin.compassInfoPanels.get(p).run();
							}
						} else {
							p.sendMessage(ChatColor.RED + "Home/bed not set!");
						}
						return true;
					case "death":
						if(death != null) {
							if(currentTrack != null) {
								plugin.playerTargets.get(currentTrack).removeTracker(p);
							}
							p.setCompassTarget(death);
							sender.sendMessage(ChatColor.GREEN + "Pointed compass to " + ChatColor.YELLOW + args[1] + ChatColor.GREEN + ".");
							if(plugin.compassInfoPanels.containsKey(p)) {
								plugin.compassInfoPanels.get(p).setTargetName("Last Death");
								plugin.compassInfoPanels.get(p).run();
							}
						} else {
							sender.sendMessage(ChatColor.RED + "Could not find a previous death.");
						}
						return true;
					case "deathpoint":
						if(death != null) {
							if(currentTrack != null) {
								plugin.playerTargets.get(currentTrack).removeTracker(p);
							}
							p.setCompassTarget(death);
							sender.sendMessage(ChatColor.GREEN + "Pointed compass to " + ChatColor.YELLOW + args[1] + ChatColor.GREEN + ".");
							if(plugin.compassInfoPanels.containsKey(p)) {
								plugin.compassInfoPanels.get(p).setTargetName("Last Death");
								plugin.compassInfoPanels.get(p).run();
							}
						} else {
							sender.sendMessage(ChatColor.RED + "Could not find a previous death.");
						}
						return true;
					default:
						//check if arg[1] matches a custom target first
						Set<String> targets = null;
						targets = mythian.getCompassTargetNames();

						if (targets != null) {
							for (String targetName : targets) {
								if(targetName.equals(args[1])) {
									if(currentTrack != null) {
										plugin.playerTargets.get(currentTrack).removeTracker(p);
									}
									Location targetLoc = mythian.getCompassTarget(targetName);
									p.setCompassTarget(targetLoc);
									sender.sendMessage(ChatColor.GREEN + "Pointed compass to " + ChatColor.YELLOW + args[1] + ChatColor.GREEN + ".");
									if(plugin.compassInfoPanels.containsKey(p)) {
										plugin.compassInfoPanels.get(p).setTargetName(args[1]);
										plugin.compassInfoPanels.get(p).run();
									}
									return true;

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

						final Player target = Bukkit.getPlayer(playername);
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

							Player trackingPlayer = Utils.getTrackingPlayer(p);
							if(trackingPlayer != null) {
								if(trackingPlayer == target) {
									sender.sendMessage(ChatColor.RED + "You are already tracking that player!");
								}
							}

							sender.sendMessage(ChatColor.GREEN + "Sending a request to compass track " + ChatColor.YELLOW + target.getName() + ChatColor.GREEN + "...");
							if(plugin.trackRequests.containsKey(target)) {
								plugin.trackRequests.remove(target);
							}
							plugin.trackRequests.put(target, p);
							target.sendMessage(ChatColor.YELLOW + sender.getName() + ChatColor.GREEN + " is requesting to track you with their compass. Reply with " + ChatColor.YELLOW +  "/sure" + ChatColor.GREEN +", " + ChatColor.YELLOW + "/nah" + ChatColor.GREEN + ", or " + ChatColor.YELLOW + "/creep");
							BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
							scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
								@Override
								public void run() {
									if(plugin.trackRequests.containsKey(target)) {
										plugin.trackRequests.remove(target);
										p.sendMessage(ChatColor.GREEN + "No reply. Player track request canceled.");
									}
								}
							}, 300L);
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
		sb.append(ChatColor.AQUA + "/Compass Current" + ChatColor.GRAY + " - Displays the compass's current target panel\n");
		sb.append(ChatColor.AQUA + "/Compass Targets" + ChatColor.GRAY + " - Shows saved target names you can point your compass to.\n");
		sb.append(ChatColor.AQUA + "/Compass Point [TargetName]" + ChatColor.GRAY + " - Points your compass to the given target.\n");
		sb.append(ChatColor.AQUA + "/Compass SaveTarget [TargetName]" + ChatColor.GRAY + " - Saves a new custom target at your location.\n");
		sb.append(ChatColor.AQUA + "/Compass SaveTarget [TargetName] [x] [y] [z]" + ChatColor.GRAY + " - Saves a new custom target at the given coords in your world.\n");
		sb.append(ChatColor.AQUA + "/Compass SaveTarget [TargetName] [x] [y] [z] [world]" + ChatColor.GRAY + " - Saves a new custom target at the given coords in the given world.\n");
		sb.append(ChatColor.AQUA + "/Compass RenameTarget [OldName] [NewName]" + ChatColor.GRAY + " - Renames a custom target.\n");
		sb.append(ChatColor.AQUA + "/Compass DeleteTarget [TargetName]" + ChatColor.GRAY + " - Deletes the custom target.\n");
		sb.append(ChatColor.AQUA + "/Compass ShareTarget [TargetName] [PlayerName]" + ChatColor.GRAY + " - Shares target with another player.\n");
		sb.append(ChatColor.YELLOW + "Tip: " + ChatColor.GRAY +" You can use \"/c\" in place of \"/compass\" for all commands.\n" + ChatColor.YELLOW + "Tip: " + ChatColor.GRAY + "\n Subcommands can be shortened as well: t = Targets, p = Point, savet = SaveTarget, rt = RenameTarget, dt = DeleteTarget, sharet = ShareTarget, c = Current\n");
		sb.append(ChatColor.YELLOW + "Examples: \n" + ChatColor.GRAY + "/c point Spire \n/c point ebiggz\n/c savet MyMine");
		return sb.toString();
	}

	public String getWorldName(String input) {
		switch(input.toLowerCase()) {
		case "survival":
		case "survival_main":
		case "survivalmain":
		case "therealm":
		case "the_realm":
		case "realm":
			return "survival_main";
		case "survivaltemp":
		case "survival_temp":
			return "survival_temp";
		case "survival_the_end":
		case "survival_end":
		case "survivalend":
		case "survival_main_the_end":
			return "survival_main_the_end";
		case "pvp":
		case "pvp_main":
		case "pvpmain":
		case "thedominion":
		case "the_dominion":
		case "dominion":
			return "pvp_main";
		case "pvptemp":
		case "pvp_Temp":
			return "pvp_temp";
		case "pvp_the_end":
		case "pvpend":
		case "pvp_end":
		case "pvp_main_the_end":
			return "pvp_main_the_end";
		default:
			return "unknown";
		}
	}

	public String getReadableName(String input) {
		switch(input.toLowerCase()) {
		case "survival_main":
			return "SurvivalMain";
		case "survival_temp":
			return "SurvivalTemp";
		case "survival_main_the_end":
			return "SurvivalEnd";
		case "pvp_main":
			return "PvpMain";
		case "pvp_temp":
			return "PvpTemp";
		case "pvp_main_the_end":
			return "PvpEnd";
		default:
			return "Unknown";
		}
	}
}
