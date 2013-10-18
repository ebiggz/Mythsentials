package com.mythicacraft.plugins.mythsentials.Listeners;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.BookMeta;

import com.mythicacraft.plugins.mythsentials.Mythsentials;
import com.mythicacraft.plugins.mythsentials.Tools.ConfigAccessor;
import com.mythicacraft.plugins.mythsentials.Tools.DeathDrops;
import com.mythicacraft.plugins.mythsentials.Tools.NotificationStreamMessage;
import com.mythicacraft.plugins.mythsentials.Tools.Time;
import com.mythicacraft.plugins.mythsentials.Tools.Utils;

public class PlayerListener implements Listener {

	private Mythsentials plugin;

	public PlayerListener(Mythsentials plugin) {
		this.plugin = plugin;
	}

	@EventHandler (priority = EventPriority.MONITOR)
	public void onJoin(PlayerJoinEvent event) {
		ConfigAccessor playerData = new ConfigAccessor("players.yml");
		Player p = event.getPlayer();
		if(!p.hasPlayedBefore()) {
			Mythsentials.notificationStream.addMessage(new NotificationStreamMessage("newplayer", p.getName(), null));
			Utils.playerNotify("mythica.helpreceive", ChatColor.RED + "[ModMessage] " + ChatColor.GOLD + p.getDisplayName() + ChatColor.YELLOW + " is new!");
			giveStarterKit(p);
		}
		checkForNewLoc(p);
		String playerName = p.getDisplayName();
		Utils.offlineBalanceChange(p);
		Utils.modMessage(p);
		String time = Time.getTime();
		playerData.getConfig().set(playerName + ".joinTime", time);
		playerData.saveConfig();
	}

	@EventHandler (priority = EventPriority.MONITOR)
	public void onQuit(PlayerQuitEvent event) {
		ConfigAccessor playerData = new ConfigAccessor("players.yml");
		Player p = event.getPlayer();
		String playerName = p.getDisplayName();
		plugin.economy.getBalance(playerName);
		Double balance = plugin.economy.getBalance(playerName);
		playerData.getConfig().set(playerName + ".logoffBalance", balance);
		playerData.saveConfig();
	}


	@EventHandler (priority = EventPriority.MONITOR)
	public void onDeath(PlayerDeathEvent event) {
		ConfigAccessor playerData = new ConfigAccessor("players.yml");
		String playerName = event.getEntity().getName();
		Location death = event.getEntity().getLocation();
		String deathWorld = event.getEntity().getWorld().getName();
		String deathLoc = Integer.toString((int) death.getX()) + "," + Integer.toString((int) death.getY()) + "," + Integer.toString((int) death.getZ()) + "," + deathWorld;
		playerData.getConfig().set(playerName + ".lastDeathLoc", deathLoc);
		playerData.saveConfig();

		List<ItemStack> armor = Arrays.asList(event.getEntity().getInventory().getArmorContents());
		List<ItemStack> drops = event.getDrops();
		event.getEntity().getInventory().getContents();
		if(drops.size() > 3) {
			if(!playerData.getConfig().contains(playerName + ".lastDeathDrops")) {
				playerData.getConfig().createSection(playerName + ".lastDeathDrops");
				playerData.saveConfig();
			}
			List<DeathDrops> dropsList = Utils.getPlayerDeathDrops(playerName);
			playerData.getConfig().set(playerName + ".lastDeathDrops.1.Drops", drops);
			if(armor != null) {
				if(armor.size() > 0 ) {
					playerData.getConfig().set(playerName + ".lastDeathDrops.1.Armor", armor);
				}
			}
			playerData.getConfig().set(playerName + ".lastDeathDrops.1.Time", Time.dateAndTimeFromMills(Time.timeInMillis()));
			playerData.getConfig().set(playerName + ".lastDeathDrops.1.Location", deathLoc);
			playerData.getConfig().set(playerName + ".lastDeathDrops.1.World", deathWorld);
			playerData.getConfig().set(playerName + ".lastDeathDrops.1.Reason", event.getDeathMessage().replace(playerName, ""));
			for(int i = 0; i < dropsList.size(); i++) {
				int dropNum = i + 2;
				playerData.getConfig().set(playerName + ".lastDeathDrops." + dropNum + ".Drops", dropsList.get(i).getDrops());
				if(dropsList.get(i).hasArmor()) {
					playerData.getConfig().set(playerName + ".lastDeathDrops." + dropNum + ".Armor", dropsList.get(i).getArmor());
				}
				playerData.getConfig().set(playerName + ".lastDeathDrops."  + dropNum + ".Time", dropsList.get(i).getDeathTime());
				playerData.getConfig().set(playerName + ".lastDeathDrops." + dropNum + ".Location", dropsList.get(i).getDeathLoc());
				playerData.getConfig().set(playerName + ".lastDeathDrops."  + dropNum + ".World", dropsList.get(i).getWorld());
			}
			playerData.saveConfig();
		}
	}

	public static void giveStarterKit(Player p) {
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

	void checkForNewLoc(Player player) {
		ConfigAccessor playerData = new ConfigAccessor("players.yml");
		if(playerData.getConfig().contains(player.getName() + ".newLoginLoc")) {
			String[] points = playerData.getConfig().getString(player.getName() + ".newLoginLoc").split(",");
			World world = Bukkit.getWorld(points[3]);
			Location newLoc = new Location(world, Double.parseDouble(points[0]), Double.parseDouble(points[1]), Double.parseDouble(points[2]));
			player.teleport(newLoc);
			playerData.getConfig().set(player.getName() + ".newLoginLoc", null);
			playerData.saveConfig();
		}
	}
}