package com.mythicacraft.plugins.mythsentials.MiscListeners;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scoreboard.DisplaySlot;
import org.kitteh.vanish.event.VanishStatusChangeEvent;

import com.gmail.mythicacraft.mythicaspawn.MythicaSpawn;
import com.mythicacraft.plugins.mythsentials.Mythian;
import com.mythicacraft.plugins.mythsentials.Mythsentials;
import com.mythicacraft.plugins.mythsentials.AdminTools.PlayerDeathDrop;
import com.mythicacraft.plugins.mythsentials.JsonAPI.NotificationStreamMessage;
import com.mythicacraft.plugins.mythsentials.Utilities.Time;
import com.mythicacraft.plugins.mythsentials.Utilities.Utils;

public class PlayerListener implements Listener {

	private Mythsentials plugin;

	public PlayerListener(Mythsentials plugin) {
		this.plugin = plugin;
	}

	@EventHandler (priority = EventPriority.MONITOR)
	public void onPlayerInteract(final PlayerInteractEvent event) {
		if(!event.hasBlock()) return;
		//Check if player has right clicked a door
		if (event.getAction() == Action.LEFT_CLICK_BLOCK && event.getClickedBlock().getType() == Material.WOODEN_DOOR) {
			event.getClickedBlock().getWorld().playSound(event.getClickedBlock().getLocation(), Sound.WOOD_CLICK, 4, -1);
			event.getClickedBlock().getWorld().playSound(event.getClickedBlock().getLocation(), Sound.ZOMBIE_WOOD, 1, -1);
			event.getClickedBlock().getWorld().playSound(event.getClickedBlock().getLocation(), Sound.NOTE_BASS_DRUM, 1, 0);
		}
	}

	@EventHandler (priority = EventPriority.MONITOR)
	public void onGoingToPvP(PlayerChangedWorldEvent event) {
		Player player = event.getPlayer();
		String worldType = MythicaSpawn.getSpawnManager().getWorldType(player.getWorld());
		if(worldType.equalsIgnoreCase("pvp") || worldType.equalsIgnoreCase("minigames")) {
			Utils.reloadPexSmartly(player);
		}

		if(plugin.playerTargets.containsKey(player)) {
			List<Player> trackers = plugin.playerTargets.get(player).getTrackers();
			for(Player tracker: trackers) {
				if(tracker.getWorld() != player.getWorld()) {
					tracker.sendMessage(ChatColor.RED + "Player changed worlds! Resetting compass...");
					tracker.setCompassTarget(tracker.getWorld().getSpawnLocation());
					if(plugin.compassInfoPanels.containsKey(tracker)) {
						plugin.compassInfoPanels.get(tracker).setTargetName("Spawn");
						plugin.compassInfoPanels.get(tracker).run();
					}
					plugin.playerTargets.get(player).removeTracker(tracker);
				}
			}
		}

		Player trackingPlayer = Utils.getTrackingPlayer(player);
		if(trackingPlayer != null) {
			if(trackingPlayer.getWorld() != player.getWorld()) {
				plugin.playerTargets.get(trackingPlayer).removeTracker(player);
				player.setCompassTarget(player.getWorld().getSpawnLocation());
				if(plugin.compassInfoPanels.containsKey(player)) {
					plugin.compassInfoPanels.get(player).setTargetName("Spawn");
					plugin.compassInfoPanels.get(player).run();
				}
			}
		}
	}

	@EventHandler (priority = EventPriority.MONITOR)
	public void onVanish(VanishStatusChangeEvent event) {

		if(event.isVanishing()) {
			Player player = event.getPlayer();
			if(plugin.playerTargets.containsKey(player)) {
				List<Player> trackers = plugin.playerTargets.get(player).getTrackers();
				for(Player tracker: trackers) {
					if(!tracker.hasPermission("mythica.mod")) {
						tracker.sendMessage(ChatColor.RED + "Player is no longer available! Resetting compass...");
						tracker.setCompassTarget(tracker.getWorld().getSpawnLocation());
						if(plugin.compassInfoPanels.containsKey(tracker)) {
							plugin.compassInfoPanels.get(tracker).setTargetName("Spawn");
							plugin.compassInfoPanels.get(tracker).run();
						}
						plugin.playerTargets.get(player).removeTracker(tracker);
					}
				}
			}
		}
	}




	@EventHandler (priority = EventPriority.MONITOR)
	public void onJoin(PlayerJoinEvent event) {

		final Player p = event.getPlayer();

		if(!p.hasPlayedBefore()) {
			Mythsentials.notificationStream.addMessage(new NotificationStreamMessage("newplayer", p.getName(), null));
			Utils.playerNotify("mythica.helpreceive", ChatColor.RED + "[ModMessage] " + ChatColor.GOLD + p.getDisplayName() + ChatColor.YELLOW + " is new!");
		}

		String playerName = p.getName();
		Mythian mythian = Mythsentials.getMythianManager().getMythian(playerName);

		mythian.setJoinTime(Time.getTime());

		Location newLoc = mythian.getNewLoginLoc();
		if(newLoc != null) {
			p.teleport(newLoc);
			mythian.setNewLoginLoc(null);
		}

		BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
		scheduler.runTaskLaterAsynchronously(Mythsentials.getPlugin(), new Runnable() {
			@Override
			public void run() {
				p.sendMessage(ChatColor.YELLOW + "[Mythica] "+ ChatColor.GREEN + "Last tweet from @MythicaCraft: " + ChatColor.WHITE + Utils.getLastTweet() + "\n" + ChatColor.GRAY + " Type /twitter to see more tweets.");
				/*Utils.offlineBalanceChangeCheck(p);
				Utils.modMessage(p);*/
			}
		}, 15L);

		String worldType = MythicaSpawn.getSpawnManager().getWorldType(p.getWorld());
		if(worldType.equalsIgnoreCase("pvp") || worldType.equalsIgnoreCase("minigames")) {
			Utils.reloadPexSmartly(p);
		}
	}

	@EventHandler (priority = EventPriority.MONITOR)
	public void onQuit(PlayerQuitEvent event) {

		Player p = event.getPlayer();
		String playerName = p.getDisplayName();

		Double balance = Mythsentials.economy.getBalance(playerName);
		Mythsentials.getMythianManager().getMythian(playerName).setLogoffBalance(balance);

		if(Mythsentials.permissionsReloaded.contains(p)) {
			Mythsentials.permissionsReloaded.remove(p);
		}

		if(plugin.compassInfoPanels.containsKey(p)) {
			plugin.compassInfoPanels.remove(p);
			p.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
		}

		if(plugin.playerTrackers.containsKey(p)) {
			plugin.playerTrackers.get(p).cancel();
			plugin.playerTrackers.remove(p);
			p.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
		}

		if(plugin.playerTargets.containsKey(event.getPlayer())) {
			List<Player> trackers = plugin.playerTargets.get(event.getPlayer()).getTrackers();
			for(Player tracker : trackers) {
				tracker.setCompassTarget(tracker.getWorld().getSpawnLocation());
				tracker.sendMessage(ChatColor.RED + event.getPlayer().getName() + " has gone offline and is no longer available to track! Resetting compass...");
				if(plugin.compassInfoPanels.containsKey(tracker)) {
					plugin.compassInfoPanels.get(tracker).setTargetName("Spawn");
					plugin.compassInfoPanels.get(tracker).run();
				}
			}
			plugin.playerTargets.remove(event.getPlayer());
		}

		Player trackingPlayer = Utils.getTrackingPlayer(p);
		if(trackingPlayer != null) {
			plugin.playerTargets.get(trackingPlayer).removeTracker(p);
		}
	}


	@EventHandler (priority = EventPriority.MONITOR)
	public void onDeath(PlayerDeathEvent event) {
		String playerName = event.getEntity().getName();
		Mythian mythian = Mythsentials.getMythianManager().getMythian(playerName);

		mythian.setLastDeathLoc(event.getEntity().getLocation());

		List<ItemStack> drops = event.getDrops();

		if(drops.size() > 3) {

			Location death = event.getEntity().getLocation();
			String deathWorld = event.getEntity().getWorld().getName();
			String deathLoc = Integer.toString((int) death.getX()) + "," + Integer.toString((int) death.getY()) + "," + Integer.toString((int) death.getZ()) + "," + death.getWorld().getName();
			List<ItemStack> armor = Arrays.asList(event.getEntity().getInventory().getArmorContents());

			String reason = event.getDeathMessage().replace(playerName, "Player").trim();
			String time = Time.dateAndTimeFromMills(Time.timeInMillis());

			PlayerDeathDrop thisDeath = new PlayerDeathDrop(playerName, drops, armor, deathLoc, deathWorld, reason, time);
			mythian.addNewDeathDrop(thisDeath);
		}
	}

	@EventHandler (priority = EventPriority.HIGHEST)
	public void onChatEvent(final AsyncPlayerChatEvent event) {
		event.getRecipients().clear();
	}

	@EventHandler (priority = EventPriority.HIGHEST)
	public void onPlayerMove(PlayerMoveEvent event) {
		Location to = event.getTo();
		Location from = event.getFrom();
		if(((to.getBlockX() == from.getBlockX()) && (to.getBlockY() == to.getBlockY()) && (to.getBlockZ() == to.getBlockZ())) && ((to.getYaw() != from.getYaw()) || (to.getPitch() != from.getPitch()))) return;
		if(plugin.compassInfoPanels.containsKey(event.getPlayer())) {
			if(!event.getPlayer().getInventory().contains(Material.COMPASS)) {
				event.getPlayer().sendMessage(ChatColor.RED + "A compass is required to be in your inventory to use the info panel.");
				plugin.compassInfoPanels.remove(event.getPlayer());
				event.getPlayer().getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
			} else {
				plugin.compassInfoPanels.get(event.getPlayer()).run();
			}
		}
		if(plugin.playerTargets.containsKey(event.getPlayer())) {
			List<Player> trackers = plugin.playerTargets.get(event.getPlayer()).getTrackers();
			for(Player tracker : trackers) {
				tracker.setCompassTarget(event.getPlayer().getLocation());
				if(plugin.compassInfoPanels.containsKey(tracker)) {
					if(!tracker.getInventory().contains(Material.COMPASS)) {
						tracker.sendMessage(ChatColor.RED + "A compass is required to be in your inventory to use the info panel.");
						plugin.compassInfoPanels.remove(tracker);
						tracker.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
					} else {
						plugin.compassInfoPanels.get(tracker).run();
					}
				}
			}
		}
	}






	/*public static void giveStarterKit(Player p) {
		PlayerInventory inv = p.getInventory();
		inv.setHelmet(new ItemStack(Material.LEATHER_HELMET, 1));
		inv.setChestplate(new ItemStack(Material.LEATHER_CHESTPLATE, 1));
		inv.setLeggings(new ItemStack(Material.LEATHER_LEGGINGS, 1));
		inv.setBoots(new ItemStack(Material.LEATHER_BOOTS, 1));
		inv.setItemInHand(ruleBook());
		inv.addItem(guideBook());
		inv.addItem(new ItemStack(Material.STONE_SWORD, 1));
		inv.addItem(new ItemStack(Material.STONE_PICKAXE, 1));
		inv.addItem(new ItemStack(Material.STONE_AXE, 1));
		inv.addItem(new ItemStack(Material.STONE_SPADE, 1));
		inv.addItem(new ItemStack(Material.BREAD, 16));
	}

	static ItemStack ruleBook() {
		ItemStack rules = new ItemStack(Material.WRITTEN_BOOK);
		BookMeta rulesMeta = (BookMeta) rules.getItemMeta();
		rulesMeta.setTitle("Rule Book");
		rulesMeta.setAuthor("Mythica");
		List<String> lore = new ArrayList<String>();
		lore.add("Mythica's Offical Rule Book");
		rulesMeta.setLore(lore);
		rulesMeta.addPage(ChatColor.BOLD + "Unforgivables: " + ChatColor.RESET + ChatColor.RED + "\n1. No Cheating \n2. No Griefing \n3. No Stealing" + ChatColor.BLACK + ChatColor.BOLD + "\nGeneral: " + ChatColor.RESET + ChatColor.BLACK + "\n- Be respectful \n- Don't ask for OP or moderator \n- No spamming \n- No Teleportation! We use portals \n- Don't promote other servers " + ChatColor.ITALIC +  "\nDetails on the next page");
		rulesMeta.addPage(ChatColor.BOLD + "Definitions: " + ChatColor.RESET + ChatColor.UNDERLINE + "\nCheating" + ChatColor.RESET + ChatColor.BLACK + "\nThe use of any mod to gain an advantage over other players." + ChatColor.UNDERLINE + "\nGriefing" + ChatColor.RESET + ChatColor.BLACK + "\nAny deliberate destruction of another player's property." + ChatColor.UNDERLINE + "\nStealing" + ChatColor.RESET + ChatColor.BLACK + "\nObtaining any item/block that you have not gathered or worked for yourself (Including 'abandoned' places).\nThese offenses will get you banned from the server.");
		rules.setItemMeta(rulesMeta);
		return rules;
	}

	static ItemStack guideBook() {
		ItemStack guide = new ItemStack(Material.WRITTEN_BOOK);
		BookMeta guideMeta = (BookMeta) guide.getItemMeta();
		guideMeta.setTitle("Guide Book");
		guideMeta.setAuthor("Mythica");
		List<String> lore = new ArrayList<String>();
		lore.add("Getting started");
		lore.add("in Mythica");
		guideMeta.setLore(lore);
		guideMeta.addPage(ChatColor.BOLD + "" + ChatColor.UNDERLINE + "Table of Contents" + ChatColor.RESET + "\nPage 2: Getting Started\nPage 4: Portals\nPage 5: Grief Protection\nPage 6: Residence/LWC\nPage 7: Live Map\nPage 8: Chat\nPage 9: Economy\nPage 10: Donator & Subscriber\nPage 11: Temp & Creative Worlds\nPage 12: More Info");
		guideMeta.addPage(ChatColor.BOLD + "" + ChatColor.UNDERLINE +"Getting Started" + ChatColor.RESET + "\nAll new players must type \"/register [email] [password]\" which will register you with our forums and allow us to automatically promote you in game.");
		guideMeta.addPage(ChatColor.BOLD + "" + ChatColor.UNDERLINE + "Beginning Tips" + ChatColor.RESET + "\n\n- Visit the spawn garden\n- Visit the spawn/server mall\n- Vist player towns and cities\n- Use the free build portals on the southern wall of the Spire\n- Use the map");
		guideMeta.addPage(ChatColor.BOLD + "" + ChatColor.UNDERLINE + "Portals " + ChatColor.RESET + "\nInstead of teleportation, we use portals. They are located in the Spire.\n\n" + ChatColor.BOLD + "Portals Types:" + ChatColor.RESET + "\n- 4 Free build\n- 2 Temp World\n- Nether/End Portals\n- Creative Portal\n- Player made city and town portals");
		guideMeta.addPage(ChatColor.BOLD + "" + ChatColor.UNDERLINE + "Grief Protection" + ChatColor.RESET + "\nWe take grief very seriously and take steps to prevent it, using the plugins Residence and LWC as preemptive measures. We also use LogBlock to reverse any grief that does manage to happen. LWC and Residence are detailed on page 6.");
		guideMeta.addPage(ChatColor.BOLD + "" + ChatColor.UNDERLINE + "Grief Protection(cont)" + ChatColor.RESET + ChatColor.BOLD + "\n\nLWC" + ChatColor.RESET + ": Lock individual chests and containers to prevent unauthorized access!" + ChatColor.BOLD + "\n\nResidence" + ChatColor.RESET + ": Claim areas of land to protect your buildings adn everything inside!\n\nTutorials on the site!");
		guideMeta.addPage(ChatColor.BOLD + "" + ChatColor.UNDERLINE + "Live Map" + ChatColor.RESET + "\n\nYou may view our live map at mythicacraft.com/map\n\nThe red boxes indicate claimed land, anything else is available for claim!");
		guideMeta.addPage(ChatColor.BOLD + "" + ChatColor.UNDERLINE + "Chat" + ChatColor.RESET + "\n\nThere are two main chat channels, local and global." + ChatColor.BOLD + "\n\nLocal (/ch L)" + ChatColor.RESET + ": Only players within 100 blocks can hear you." + ChatColor.BOLD + "\n\nGlobal (/ch G)" + ChatColor.RESET + ": Anyone on the server can hear you.");
		guideMeta.addPage(ChatColor.BOLD + "" + ChatColor.UNDERLINE + "Economy" + ChatColor.RESET + "\n\nThere are many ways to earn/spend money:\n\nYou may earn money by selling items at the server mall or by killing mobs in the temporary worlds\n\nYou may spend money by purchasing items from other players.");
		guideMeta.addPage(ChatColor.BOLD + "" + ChatColor.UNDERLINE + "Donator/Subscriber" + ChatColor.RESET + "\n\nWe offer perks and abilities to our donors and subscribers as a thank you for supporting the server.\n\nA full list of perks can be viewed at\n www.mythicacraft.com/donate");
		guideMeta.addPage(ChatColor.BOLD + "Temp Worlds" + ChatColor.RESET + ":\nThere is a temporary nether and overworld portal located in the spire for up-to-date material gathering." + ChatColor.BOLD + "\n\nCreative" + ChatColor.RESET + ":\nWe offer a creative planning world for our donators to hammer out ideas with your friends.");
		guideMeta.addPage(ChatColor.BOLD + "" + ChatColor.UNDERLINE +"More Info" + ChatColor.RESET + "\n\nMore info and details can be found on our website at\n\n www.mythicacraft.com/tutorials\n\nand\n\nwww.mythicacraft.com/wiki");
		guide.setItemMeta(guideMeta);
		return guide;
	}
	 */
}