package com.mythicacraft.plugins.mythsentials.MythianPostalService;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import com.mythicacraft.plugins.mythsentials.GUIAPI.GUIManager;
import com.mythicacraft.plugins.mythsentials.MythianPostalService.MailboxManager.MailboxSelect;
import com.mythicacraft.plugins.mythsentials.MythianPostalService.exceptions.MailException;
import com.mythicacraft.plugins.mythsentials.MythianPostalService.guis.MailboxGUI;



public class MailboxListener implements Listener {

	@EventHandler (priority = EventPriority.MONITOR)
	public void onBlockBreak(final BlockBreakEvent event) {
		if(event.getBlock().getType() == Material.CHEST) {
			if(MailboxManager.getInstance().locationHasMailbox(event.getBlock().getLocation())) {
				event.setCancelled(true);
				event.getPlayer().sendMessage(ChatColor.RED + "[Mythica] This chest is registered as a mailbox. The owner must deregister the chest before it can be removed.");
			}
		}
	}

	@EventHandler (priority = EventPriority.MONITOR)
	public void onPlayerInteract(PlayerInteractEvent event) {
		if(!event.hasBlock()) return;
		if(event.getClickedBlock().getType() != Material.CHEST) {
			if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
				if(MailboxManager.getInstance().mailboxSelectors.containsKey(event.getPlayer())) {
					MailboxManager.getInstance().mailboxSelectors.remove(event.getPlayer());
					event.setCancelled(true);
					event.getPlayer().sendMessage(ChatColor.RED + "[Mythica] Selected block is not a chest! Mailbox selection canceled.");
				}
			}
			return;
		}
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if(MailboxManager.getInstance().locationHasMailbox(event.getClickedBlock().getLocation())) {
				event.setCancelled(true);
				GUIManager.getInstance().showGUI(new MailboxGUI(), event.getPlayer());
			}
		}
		else if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
			if(MailboxManager.getInstance().mailboxSelectors.containsKey(event.getPlayer())) {
				MailboxSelect selectType = MailboxManager.getInstance().mailboxSelectors.get(event.getPlayer());
				MailboxManager.getInstance().mailboxSelectors.remove(event.getPlayer());
				event.setCancelled(true);
				if(selectType == MailboxSelect.ADD) {
					try {
						MailboxManager.getInstance().addMailboxAtLoc(event.getClickedBlock().getLocation(), event.getPlayer());
						event.getPlayer().sendMessage(ChatColor.YELLOW + "[Mythica]" + ChatColor.AQUA + " Mailbox registered!");
					} catch (MailException me) {
						switch(me.getReason()) {
							case MAILBOX_ALREADY_EXISTS:
								event.getPlayer().sendMessage(ChatColor.RED + "[Mythica] A mailbox already exsists here! Selection canceled.");
								break;
							case NO_PERMISSION:
								event.getPlayer().sendMessage(ChatColor.RED + "[Mythica] Unable to set mailbox, you don't have permission to build or place here! Selection canceled.");
								break;
							default:
								event.getPlayer().sendMessage(ChatColor.RED + "[Mythica] There was an error setting your mailbox. Please try again.");
								break;
						}
					}
				} else {
					try {
						MailboxManager.getInstance().removeMailboxAtLoc(event.getClickedBlock().getLocation(), event.getPlayer());
						event.getPlayer().sendMessage(ChatColor.YELLOW + "[Mythica]" + ChatColor.AQUA + " Mailbox unregistered!");
					} catch (MailException me) {
						switch(me.getReason()) {
							case MAILBOX_DOESNT_EXIST:
								event.getPlayer().sendMessage(ChatColor.RED + "[Mythica] A mailbox doesn't exsist here! Selection canceled.");
								break;
							case NOT_OWNER:
								event.getPlayer().sendMessage(ChatColor.RED + "[Mythica] Unable to remove mailbox, you don't own this mailbox! Selection canceled.");
								break;
							default:
								event.getPlayer().sendMessage(ChatColor.RED + "[Mythica] There was an error setting your mailbox. Please try again.");
								break;
						}
					}
				}
			}
		}
	}
}
