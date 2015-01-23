package com.mythicacraft.plugins.mythsentials.AdminTools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Wool;

import ru.tehkode.permissions.bukkit.PermissionsEx;

import com.bekvon.bukkit.residence.Residence;
import com.bekvon.bukkit.residence.protection.ClaimedResidence;
import com.dthielke.herochat.Herochat;
import com.mythicacraft.plugins.mythsentials.Mythian;
import com.mythicacraft.plugins.mythsentials.Mythsentials;
import com.mythicacraft.plugins.mythsentials.DeathLedger.DeathLog;
import com.mythicacraft.plugins.mythsentials.DeathLedger.LogListGUI;
import com.mythicacraft.plugins.mythsentials.GUIAPI.GUIManager;
import com.mythicacraft.plugins.mythsentials.Utilities.Time;
import com.mythicacraft.plugins.mythsentials.Utilities.Utils;

public class AdminTools implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {

		if(!sender.hasPermission("mythica.mod")) {
			sender.sendMessage(ChatColor.RED + "You don't have permission for this!");
			return true;
		}
		if(commandLabel.equalsIgnoreCase("toolbox")) {

		}
		if(commandLabel.equalsIgnoreCase("chunkcheck")) {
			World world = null;
			if(!(sender instanceof Player)) {
				world = Bukkit.getWorld("survival_main");
			} else {
				Player player = (Player) sender;
				world = player.getWorld();
			}
			System.out.println("Looking at world: " + world.getName());
			System.out.println("Total entities in world: " + world.getLivingEntities().size());
			Chunk[] chunks = world.getLoadedChunks();
			List<Chunk> chunkList = new ArrayList<Chunk>(Arrays.asList(chunks));
			System.out.println("Total loaded chunks: " + chunks.length);
			System.out.println("Chunk info:");
			Collections.sort(chunkList, new Comparator<Chunk>(){
				public int compare(Chunk c1, Chunk c2) {
					return c2.getEntities().length - c1.getEntities().length;
				}
			});
			int count = 1;
			for(Chunk chunk : chunkList) {
				System.out.println("Chunk #" + count + " (" + chunk.getX() + ", " + chunk.getZ()+ ")");
				System.out.println("  Entities Count: " + chunk.getEntities().length);
				System.out.println("  Tiled Entities Count: " + chunk.getTileEntities().length);
				count++;
			}
			sender.sendMessage("Chunk indexing complete! (See console)");
		}

		if(commandLabel.equalsIgnoreCase("mobselect")) {
			Player playerP = (Player) sender;
			if(!Mythsentials.mobCopy.contains(playerP)) {
				Mythsentials.mobCopy.add(playerP);
			}
			sender.sendMessage(ChatColor.AQUA + "Please left-click a mob to select, sah. I'll cancel damage.");
		}
		if(commandLabel.equalsIgnoreCase("mobtp")) {
			Player playerP = (Player) sender;
			if(Mythsentials.copiedMob.containsKey(playerP)) {
				Entity mob = Mythsentials.copiedMob.get(playerP);
				mob.teleport(playerP.getLocation());
				Mythsentials.copiedMob.remove(playerP);
				sender.sendMessage(ChatColor.AQUA + "Mob has been tp'd to you, sah.");
			} else {
				sender.sendMessage(ChatColor.RED + "It doesn't appear that you have a mob selected, sah.");
			}
		}

		if(commandLabel.equalsIgnoreCase("loginlocation")) {

			Player mod = (Player) sender;
			if(args.length == 0) {
				sender.sendMessage(ChatColor.RED + "Please type in a player's name.");
				return true;
			}
			if(args.length == 1) {

				String otherPlayer = completeName(args[0]);
				if(otherPlayer == null) {
					sender.sendMessage(ChatColor.RED + "Not a known player.");
					return true;
				}
				Mythsentials.getMythianManager().getMythian(otherPlayer).setNewLoginLoc(mod.getLocation());
				sender.sendMessage(ChatColor.AQUA + "You have set the login location of " + ChatColor.YELLOW + otherPlayer + ChatColor.AQUA + " to where you're standing.");
			}
		}

		if(commandLabel.equalsIgnoreCase("playerinfo") || commandLabel.equalsIgnoreCase("pi")) {
			if(args.length == 0) {
				sender.sendMessage(ChatColor.RED + "Please type in a player's name.");
				return true;
			}
			if(args.length == 1) {

				String playerName = completeName(args[0]);
				if(playerName == null) {
					sender.sendMessage(ChatColor.RED + "Not a known player.");
					return true;
				}

				Player mod = (Player) sender;
				Inventory i = getPlayerInfoInv(playerName);
				boolean online;
				Player p = Bukkit.getPlayerExact(playerName);
				if(p == null) {
					online = false;
				} else {
					online = true;
				}

				PIMenuData menuData = new PIMenuData(i, mod, playerName, online);
				Mythsentials.playerInfoMenus.put(mod, menuData);

				mod.openInventory(i);

			}
		}
		if(commandLabel.equalsIgnoreCase("deathlogs")) {

			//Paginate playerDeathDrops = new Paginate();

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
				List<DeathLog> logList = Mythsentials.getMythianManager().getMythian(args[0]).getDeathLogs();
				if(logList.isEmpty()) {
					sender.sendMessage(ChatColor.RED + "No prior deathdrops saved for " + args[0] + " yet.");
					return true;
				}
				if(args.length == 1) {
					LogListGUI listGUI = new LogListGUI(args[0],logList);
					GUIManager.getInstance().showGUI(listGUI, (Player) sender);
					return true;
				}
				if(args.length > 1) {
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

							DeathLog playerDD = logList.get(ddNum);

							int openSpots = Utils.getPlayerOpenInvSlots(player);
							if(playerDD.getDropsSanArmor().size() > openSpots) {
								player.sendMessage(ChatColor.RED + "There is not enough empty room in your inventory. Please clear it and try again.");
								return true;
							}

							if(playerDD.hasArmor()) {

								if(Utils.playerHasArmor(player)) {
									player.sendMessage(ChatColor.RED + "Please clear out your armor slots and try again.");
									return true;
								}

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

								//get and set inventory
								List<ItemStack> drops = playerDD.getDropsSanArmor();
								for(ItemStack item : drops) {
									player.getInventory().addItem(item);
								}

								sender.sendMessage(ChatColor.AQUA + "You have set your inventory to " + args[0] + "'s death drop number " + args[2]);
								return true;
							}
							sender.sendMessage(ChatColor.AQUA + "You have set your inventory to " + args[0] + "'s death drop number " + args[2]);
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
								sender.sendMessage(ChatColor.RED + "Player is not online! You can set the invetory to yourself and use /inv " + args[0]);
								return true;
							}
							Player player = Bukkit.getPlayerExact(args[0]);
							try {
								int ddNum = Integer.parseInt(args[2])-1;
								DeathLog playerDD = logList.get(ddNum);
								int openSpots = Utils.getPlayerOpenInvSlots(player);
								if(playerDD.getDropsSanArmor().size() > openSpots) {
									player.sendMessage(ChatColor.RED + "There is not enough empty room in the players inventory. Please have them clear it and then try again.");
									return true;
								}

								if(playerDD.hasArmor()) {

									if(Utils.playerHasArmor(player)) {
										player.sendMessage(ChatColor.RED + "Please have the other player clear out their armor slots and try again.");
										return true;
									}

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
								}

								//get and set inventory
								List<ItemStack> drops = playerDD.getDropsSanArmor();
								for(ItemStack item : drops) {
									player.getInventory().addItem(item);
								}

								sender.sendMessage(ChatColor.AQUA + "You have restored " + args[0] + "'s inventory from death log number " + args[2]);
								player.sendMessage(ChatColor.AQUA + "A moderator has restored the inventory from one of your previous deaths.");
								return true;

							} catch (Exception e) {
								sender.sendMessage(ChatColor.RED + "Thats not a valid number!");
							}
						} else {
							sender.sendMessage(ChatColor.RED + "Please include a death log number from the list of " + args[0] + "'s death logs. Type \"/deathlogs " + args[0] + "\" to see that list.");
						}
						return true;
					}

					/* try {
						int ddNum = Integer.parseInt(args[1])-1;
						if(ddNum <= logList.size()) {
							DeathLog dd = logList.get(ddNum);
							sender.sendMessage(ChatColor.GREEN + "-----" + ChatColor.YELLOW + args[0] + ChatColor.GREEN + " Death Drop #" + ChatColor.YELLOW + args[1] + ChatColor.GREEN + "-----");
							sender.sendMessage(ChatColor.GOLD + "Time: " + ChatColor.GRAY + dd.getDeathTime());
							sender.sendMessage(ChatColor.GOLD + "Location: " + ChatColor.GRAY + dd.getDeathLoc());
							sender.sendMessage(ChatColor.GOLD + "Reason: " + ChatColor.GRAY + dd.getReason());
							sender.sendMessage(ChatColor.GOLD + "Items: "  + ChatColor.DARK_AQUA + Utils.getDeathDropItemList(dd.getDrops()));
						} else {
							sender.sendMessage(ChatColor.RED + "Not a valid deathdrop number!");
						}
					} catch(Exception e) {
						sender.sendMessage(ChatColor.RED + "Not a valid deathdrop number!");
					} */

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

	@SuppressWarnings("deprecation")
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

	@SuppressWarnings("deprecation")
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


	public String getDeathDropsMenu(List<DeathLog> dropsList) {
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < dropsList.size(); i++) {
			int dropNum = i + 1;
			DeathLog dd = dropsList.get(i);
			sb.append(dropNum + ": " + dd.getDeathTime() + "  Drops: " + dd.getDrops().size() + "\n");
		}
		return sb.toString();
	}
	public Inventory getPlayerInfoInv(String playerName) {
		Inventory i = Bukkit.createInventory(null, 9*2, ChatColor.YELLOW + playerName + ChatColor.BLACK + " Info");

		Player p = Bukkit.getPlayerExact(playerName);
		if(p == null) {
			//offline menu
			OfflinePlayer offp = Bukkit.getOfflinePlayer(playerName);

			ItemStack details = new ItemStack(Material.NAME_TAG);
			ItemMeta detailsItemMeta = details.getItemMeta();
			detailsItemMeta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.UNDERLINE + "Details");
			List<String> detailsLore = new ArrayList<String>();
			detailsLore.add(ChatColor.GOLD + "Online: " + ChatColor.WHITE + offp.isOnline());
			detailsLore.add(ChatColor.GOLD + "Perm Group: " + ChatColor.WHITE + getPermGroupStr(offp.getName()));
			detailsItemMeta.setLore(detailsLore);
			details.setItemMeta(detailsItemMeta);
			i.setItem(3, details);

			ItemStack clock = new ItemStack(Material.WATCH);
			ItemMeta clockItemMeta = clock.getItemMeta();
			clockItemMeta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.UNDERLINE + "Time");
			List<String> clockLore = new ArrayList<String>();
			clockLore.add(ChatColor.GOLD + "First Joined: " + ChatColor.WHITE + Time.dateFromMills(offp.getFirstPlayed()));
			clockLore.add(ChatColor.GOLD + "Last Played: " + ChatColor.WHITE + Time.dateFromMills(offp.getLastPlayed()));
			clockItemMeta.setLore(clockLore);
			clock.setItemMeta(clockItemMeta);
			i.setItem(4, clock);

			ItemStack command = new ItemStack(Material.COMMAND);
			ItemMeta commandItemMeta = command.getItemMeta();
			commandItemMeta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.UNDERLINE + "Recent Commands");
			Mythian mythian = Mythsentials.getMythianManager().getMythian(offp.getName());
			List<String> commands = mythian.getRecentCommands();
			for(int j = 0; j < commands.size(); j++) {
				commands.set(j, ChatColor.RESET + commands.get(j));
			}
			commandItemMeta.setLore(commands);
			command.setItemMeta(commandItemMeta);
			i.setItem(5, command);

			ItemStack chest = new ItemStack(Material.CHEST);
			ItemMeta chestItemMeta = chest.getItemMeta();
			chestItemMeta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.ITALIC + "Player Inventory");
			List<String> chestLore = new ArrayList<String>();
			chestLore.add("*Click To See*");
			chestItemMeta.setLore(chestLore);
			chest.setItemMeta(chestItemMeta);
			i.setItem(12, chest);

			ItemStack enderchest = new ItemStack(Material.ENDER_CHEST);
			ItemMeta enderchestItemMeta = enderchest.getItemMeta();
			enderchestItemMeta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.ITALIC + "Player Ender Chest");
			List<String> enderchestLore = new ArrayList<String>();
			enderchestLore.add("*Click To See*");
			enderchestItemMeta.setLore(enderchestLore);
			enderchest.setItemMeta(enderchestItemMeta);
			i.setItem(13, enderchest);

			Wool wool3 = new Wool(DyeColor.RED);
			String banPlayer = "Ban Player";
			String clickToBan = "*Click To Ban*";
			if(offp.isBanned()) {
				wool3 = new Wool(DyeColor.LIME);
				banPlayer = "Unban Player";
				clickToBan = "*Click To Unban*";
			}
			ItemStack ban = wool3.toItemStack(1);
			ItemMeta banItemMeta = ban.getItemMeta();
			ban.setData(wool3);
			banItemMeta.setDisplayName(ChatColor.YELLOW + banPlayer);
			List<String> banLore = new ArrayList<String>();
			banLore.add(clickToBan);
			banItemMeta.setLore(banLore);
			ban.setItemMeta(banItemMeta);
			i.setItem(14, ban);

			return i;
		}

		//player online menu

		Mythian mythian = Mythsentials.getMythianManager().getMythian(p.getName());

		ItemStack details = new ItemStack(Material.NAME_TAG);
		ItemMeta detailsItemMeta = details.getItemMeta();
		detailsItemMeta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.UNDERLINE + "Details");
		List<String> detailsLore = new ArrayList<String>();
		detailsLore.add(ChatColor.GOLD + "Online: " + ChatColor.WHITE + p.isOnline());
		String ip = p.getAddress().toString().replace("/", "");
		if(ip.contains(":")) {
			int index = ip.indexOf(":");
			ip = ip.substring(0, index - 1);
		}
		detailsLore.add(ChatColor.GOLD + "IP: " + ChatColor.WHITE + ip);
		detailsLore.add(ChatColor.GOLD + "Chat Channel: " + ChatColor.WHITE + Herochat.getChatterManager().getChatter(p).getActiveChannel().getName().replace(p.getName(), " with "));
		detailsLore.add(ChatColor.GOLD + "Perm Group: " + ChatColor.WHITE + getPermGroupStr(p.getName()));
		detailsItemMeta.setLore(detailsLore);
		details.setItemMeta(detailsItemMeta);
		i.setItem(2, details);

		ItemStack clock = new ItemStack(Material.WATCH);
		ItemMeta clockItemMeta = clock.getItemMeta();
		clockItemMeta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.UNDERLINE + "Time");
		List<String> clockLore = new ArrayList<String>();
		clockLore.add(ChatColor.GOLD + "First Joined: " + ChatColor.WHITE + Time.dateFromMills(p.getFirstPlayed()));
		clockLore.add(ChatColor.GOLD + "Last Played: " + ChatColor.WHITE + Time.dateFromMills(p.getLastPlayed()));
		clockLore.add(ChatColor.GOLD + "Playtime: " + ChatColor.WHITE + Time.timeString(mythian.getPlayTime()));
		clockItemMeta.setLore(clockLore);
		clock.setItemMeta(clockItemMeta);
		i.setItem(3, clock);

		ItemStack compass = new ItemStack(Material.COMPASS);
		ItemMeta compassItemMeta = compass.getItemMeta();
		compassItemMeta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.UNDERLINE + "Location");
		List<String> compassLore = new ArrayList<String>();
		Location loc = p.getLocation();
		ClaimedResidence res = Residence.getResidenceManager().getByLoc(loc);
		compassLore.add(ChatColor.GOLD + "Coords: " + ChatColor.WHITE + "X:" + (int)(p.getLocation().getX()) + " Y:" + (int)(p.getLocation().getY()) + " Z:" + (int)(p.getLocation().getZ()));
		compassLore.add(ChatColor.GOLD + "World: " + ChatColor.WHITE + p.getWorld().getName());
		if(res != null) {
			compassLore.add(ChatColor.GOLD + "Res: " + ChatColor.WHITE + res.getName());
		}
		compassItemMeta.setLore(compassLore);
		compass.setItemMeta(compassItemMeta);
		i.setItem(4, compass);

		ItemStack apple = new ItemStack(Material.APPLE);
		ItemMeta appleItemMeta = apple.getItemMeta();
		appleItemMeta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.UNDERLINE + "Stats");
		List<String> appleLore = new ArrayList<String>();
		appleLore.add(ChatColor.GOLD + "Health: " + ChatColor.WHITE + p.getHealth()/2 + "/10");
		appleLore.add(ChatColor.GOLD + "Food: " + ChatColor.WHITE + p.getFoodLevel()/2 + "/10");
		appleLore.add(ChatColor.GOLD + "XP Levels: " + ChatColor.WHITE + p.getLevel());
		appleLore.add(ChatColor.GOLD + "Balance: " + ChatColor.WHITE + "$" + Mythsentials.economy.getBalance(p.getName()));
		appleItemMeta.setLore(appleLore);
		apple.setItemMeta(appleItemMeta);
		i.setItem(5, apple);

		ItemStack command = new ItemStack(Material.COMMAND);
		ItemMeta commandItemMeta = command.getItemMeta();
		commandItemMeta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.UNDERLINE + "Recent Commands");
		List<String> commands = mythian.getRecentCommands();
		for(int j = 0; j < commands.size(); j++) {
			commands.set(j, ChatColor.RESET + commands.get(j));
		}
		commandItemMeta.setLore(commands);
		command.setItemMeta(commandItemMeta);
		i.setItem(6, command);

		ItemStack pearl = new ItemStack(Material.ENDER_PEARL);
		ItemMeta pearlItemMeta = pearl.getItemMeta();
		pearlItemMeta.setDisplayName(ChatColor.YELLOW + "Teleport To Player");
		List<String> pearlLore = new ArrayList<String>();
		pearlLore.add("*Click To TP*");
		pearlLore.add(ChatColor.GRAY + "(and auto vanish)");
		pearlItemMeta.setLore(pearlLore);
		pearl.setItemMeta(pearlItemMeta);
		i.setItem(10, pearl);

		ItemStack eye = new ItemStack(Material.EYE_OF_ENDER);
		ItemMeta eyeItemMeta = eye.getItemMeta();
		eyeItemMeta.setDisplayName(ChatColor.YELLOW + "Teleport Player Here");
		List<String> eyeLore = new ArrayList<String>();
		eyeLore.add("*Click To TP*");
		eyeItemMeta.setLore(eyeLore);
		eye.setItemMeta(eyeItemMeta);
		i.setItem(11, eye);

		ItemStack chest = new ItemStack(Material.CHEST);
		ItemMeta chestItemMeta = chest.getItemMeta();
		chestItemMeta.setDisplayName(ChatColor.YELLOW + "Player Inventory");
		List<String> chestLore = new ArrayList<String>();
		chestLore.add("*Click To See*");
		chestItemMeta.setLore(chestLore);
		chest.setItemMeta(chestItemMeta);
		i.setItem(12, chest);

		ItemStack enderchest = new ItemStack(Material.ENDER_CHEST);
		ItemMeta enderchestItemMeta = enderchest.getItemMeta();
		enderchestItemMeta.setDisplayName(ChatColor.YELLOW + "Player Ender Chest");
		List<String> enderchestLore = new ArrayList<String>();
		enderchestLore.add("*Click To See*");
		enderchestItemMeta.setLore(enderchestLore);
		enderchest.setItemMeta(enderchestItemMeta);
		i.setItem(13, enderchest);

		Wool wool = new Wool(DyeColor.GRAY);
		String mutePlayer = "Mute Player";
		String clickToMute = "*Click To Mute*";
		if(Herochat.getChatterManager().getChatter(p).isMuted()) {
			wool = new Wool(DyeColor.SILVER);
			mutePlayer = "Unmute Player";
			clickToMute = "*Click To Unmute*";
		}
		ItemStack mute = wool.toItemStack(1);
		ItemMeta muteItemMeta = mute.getItemMeta();
		mute.setData(wool);
		muteItemMeta.setDisplayName(ChatColor.YELLOW + mutePlayer);
		List<String> muteLore = new ArrayList<String>();
		muteLore.add(clickToMute);
		muteItemMeta.setLore(muteLore);
		mute.setItemMeta(muteItemMeta);
		i.setItem(14, mute);

		Wool wool2 = new Wool(DyeColor.YELLOW);
		ItemStack kick = wool2.toItemStack(1);
		ItemMeta kickItemMeta = kick.getItemMeta();
		kickItemMeta.setDisplayName(ChatColor.YELLOW + "Kick Player");
		List<String> kickLore = new ArrayList<String>();
		kickLore.add("*Click To Mute*");
		kickItemMeta.setLore(kickLore);
		kick.setItemMeta(kickItemMeta);
		i.setItem(15, kick);



		Wool wool3 = new Wool(DyeColor.RED);
		String banPlayer = "Ban Player";
		String clickToBan = "*Click To Ban*";
		if(p.isBanned()) {
			wool3 = new Wool(DyeColor.LIME);
			banPlayer = "Unban Player";
			clickToBan = "*Click To Unban*";
		}
		ItemStack ban = wool3.toItemStack(1);
		ItemMeta banItemMeta = ban.getItemMeta();
		ban.setData(wool3);
		banItemMeta.setDisplayName(ChatColor.YELLOW + banPlayer);
		List<String> banLore = new ArrayList<String>();
		banLore.add(clickToBan);
		banItemMeta.setLore(banLore);
		ban.setItemMeta(banItemMeta);
		i.setItem(16, ban);

		return i;
	}
}
