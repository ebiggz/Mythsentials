package com.mythicacraft.plugins.mythsentials.MythianPostalService;

import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

import com.mythicacraft.plugins.mythsentials.Mythian;
import com.mythicacraft.plugins.mythsentials.Mythsentials;
import com.mythicacraft.plugins.mythsentials.MythianPostalService.exceptions.MailException;
import com.mythicacraft.plugins.mythsentials.MythianPostalService.exceptions.MailException.Reason;
import com.mythicacraft.plugins.mythsentials.Utilities.ConfigAccessor;



public class MailboxManager {

	public HashMap<Player,MailboxSelect> mailboxSelectors = new HashMap<Player,MailboxSelect>();

	protected MailboxManager() { /*exists to block instantiation*/ }
	private static MailboxManager instance = null;
	public static MailboxManager getInstance() {
		if(instance == null) {
			instance = new MailboxManager();
		}
		return instance;
	}

	public enum MailboxSelect {
		ADD, REMOVE
	}


	public Mailbox getPlayerMailbox(String player) {
		return new Mailbox(player);
	}

	public boolean locationHasMailbox(Location location) {
		ConfigAccessor mailboxData = new ConfigAccessor("mailbox-locations.yml");
		return mailboxData.getConfig().contains(this.locationToString(location));
	}


	public String getMailboxOwner(Location loc) {
		ConfigAccessor mailboxData = new ConfigAccessor("mailbox-locations.yml");
		return mailboxData.getConfig().getString(locationToString(loc));
	}

	public void addMailboxAtLoc(Location location, Player player) throws MailException {
		if(location.getBlock() != null && location.getBlock().getType() != Material.CHEST) {
			throw new MailException(Reason.NOT_CHEST);
		}
		else if(!canBreakAndPlaceAtLoc(location, player)) {
			throw new MailException(Reason.NO_PERMISSION);
		}
		else if(this.locationHasMailbox(location)) {
			throw new MailException(Reason.MAILBOX_ALREADY_EXISTS);
		}
		else {
			Mythian mythian = Mythsentials.getMythianManager().getMythian(player.getName());
			ConfigAccessor mailboxData = new ConfigAccessor("mailbox-locations.yml");
			mythian.saveMailboxLoc(this.locationToString(location));
			mailboxData.getConfig().set(this.locationToString(location), player.getName());
			mailboxData.saveConfig();
		}
	}

	public void removeMailboxAtLoc(Location location, Player player) throws MailException {
		/*if(mythian.getMailboxLocs().size() >= getMaxMailboxCount(owner)) {
			throw new MailException(Reason.MAX_MAILBOXES_REACHED);
		}*/
		if(location.getBlock() != null && location.getBlock().getType() != Material.CHEST) {
			throw new MailException(Reason.NOT_CHEST);
		}
		else if(!getMailboxOwner(location).equals(player.getName())) {
			throw new MailException(Reason.NOT_OWNER);
		}
		else if(!this.locationHasMailbox(location)) {
			throw new MailException(Reason.MAILBOX_DOESNT_EXIST);
		}
		else {
			Mythian mythian = Mythsentials.getMythianManager().getMythian(player.getName());
			ConfigAccessor mailboxData = new ConfigAccessor("mailbox-locations.yml");
			mythian.deleteMailboxLoc(this.locationToString(location));
			mailboxData.getConfig().set(this.locationToString(location), null);
			mailboxData.saveConfig();
		}
	}

	public void removeAllMailboxes(String owner) {
		Mythian mythian = Mythsentials.getMythianManager().getMythian(owner);
		ConfigAccessor mailboxData = new ConfigAccessor("mailbox-locations.yml");
		List<String> locs = mythian.getMailboxLocs();
		for(String loc : locs) {
			mailboxData.getConfig().set(loc, null);
		}
		mailboxData.saveConfig();
		mythian.clearMailboxLocs();
	}

	private boolean canBreakAndPlaceAtLoc(Location loc, Player player) {
		Block block = loc.getBlock();
		int spawnRadius = Bukkit.getServer().getSpawnRadius();
		Location spawn = loc.getWorld().getSpawnLocation();
		boolean canBuild = (spawnRadius <= 0) || (player.isOp()) || (Math.max(Math.abs(block.getX() - spawn.getBlockX()), Math.abs(block.getZ() - spawn.getBlockZ())) > spawnRadius);
		BlockPlaceEvent placeEvent = new BlockPlaceEvent(block, block.getState(), null, new ItemStack(Material.CHEST), player, canBuild);
		BlockBreakEvent breakEvent = new BlockBreakEvent(block, player);
		Bukkit.getPluginManager().callEvent(placeEvent);
		Bukkit.getPluginManager().callEvent(breakEvent);
		return (!placeEvent.isCancelled() && !breakEvent.isCancelled());
	}

	public enum MailboxType {
		INBOX, SENT
	}

	public int getMaxMailboxCount(String playerName) {
		String primaryGroup = Mythsentials.permission.getPrimaryGroup("", playerName);
		if(primaryGroup != null) {
			return Mythsentials.getMaxMailboxesForGroup(primaryGroup);
		}
		return 1;
	}

	private String locationToString(Location location) {
		return location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ() + "," + location.getWorld();
	}

	/*private Location stringToLocation(String string) {
		String[] split = string.split(",");
		return new Location(Bukkit.getWorld(split[3]), Double.parseDouble(split[0]), Double.parseDouble(split[1]), Double.parseDouble(split[2]));
	}*/
}
