package com.mythicacraft.plugins.mythsentials.Listeners;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
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
import com.mythicacraft.plugins.mythsentials.Tools.Time;
import com.mythicacraft.plugins.mythsentials.Tools.Utils;

public class PlayerListener implements Listener {

	ConfigAccessor playerData = new ConfigAccessor("players.yml");
	private Mythsentials plugin;

	public PlayerListener(Mythsentials plugin) {
		this.plugin = plugin;
	}

	@EventHandler (priority = EventPriority.MONITOR)
	public void onJoin(PlayerJoinEvent event) {
		Player p = event.getPlayer();
		if(!p.hasPlayedBefore()) {
			giveStarterKit(p);
		}
		String playerName = p.getDisplayName();
		Utils.offlineBalanceChange(p);
		Utils.modMessage(p);
		String time = Time.getTime();
		playerData.getConfig().set(playerName + ".joinTime", time);
		playerData.saveConfig();
	}

	@EventHandler (priority = EventPriority.MONITOR)
	public void onQuit(PlayerQuitEvent event) {
		Player p = event.getPlayer();
		String playerName = p.getDisplayName();
		plugin.economy.getBalance(playerName);
		Double balance = plugin.economy.getBalance(playerName);
		playerData.getConfig().set(playerName + ".logoffBalance", balance);
		playerData.saveConfig();
	}


	@EventHandler (priority = EventPriority.MONITOR)
	public void onDeath(PlayerDeathEvent event) {
		String playerName = event.getEntity().getName();
		Location death = event.getEntity().getLocation();
		String deathWorld = event.getEntity().getWorld().getName();
		String deathLoc = Integer.toString((int) death.getX()) + "," + Integer.toString((int) death.getY()) + "," + Integer.toString((int) death.getZ()) + "," + deathWorld;
		playerData.getConfig().set(playerName + ".lastDeathLoc", deathLoc);
		playerData.saveConfig();

		/* This is for saving drops in an event of a glitched/lag death
		 * List<ItemStack> drops = event.getDrops();
		 *if(drops.size() > 1) {
		 *	playerData.getConfig().set(playerName + ".lastDeathDrops", drops);
		 *    playerData.saveConfig();
		 *}
		 */
	}

	public static void giveStarterKit(Player p) {
		PlayerInventory inv = p.getInventory();
		inv.setHelmet(new ItemStack(Material.LEATHER_HELMET, 1));
		inv.setChestplate(new ItemStack(Material.LEATHER_CHESTPLATE, 1));
		inv.setLeggings(new ItemStack(Material.LEATHER_LEGGINGS, 1));
		inv.setBoots(new ItemStack(Material.LEATHER_BOOTS, 1));
		inv.setItemInHand(ruleBook());
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
		//addPages
		guide.setItemMeta(guideMeta);
		return guide;
	}
}