package com.mythicacraft.plugins.mythsentials.MythiboardEntries;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.mythicacraft.plugins.mythsentials.MythianPostalService.MailboxManager;
import com.mythicacraft.plugins.mythsentials.MythiboardAPI.ScoreboardEntry;


public class UnreadMailSBEntry implements ScoreboardEntry {

	@Override
	public String getKey() {
		return ChatColor.GOLD + "Unread Mail";
	}

	@Override
	public String getValue(Player player) {
		return Integer.toString(MailboxManager.getInstance().getPlayerMailbox(player.getName()).getUnreadMailCount());
	}

	@Override
	public String getCommand() {
		// TODO Auto-generated method stub
		return "/mail";
	}
}
