package com.mythicacraft.plugins.mythsentials.MythiboardEntries;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.mythicacraft.plugins.mythsentials.Mythsentials;
import com.mythicacraft.plugins.mythsentials.MythiboardAPI.ScoreboardEntry;
import com.mythicacraft.plugins.mythsentials.Utilities.Utils;


public class BankSBEntry implements ScoreboardEntry {

	@Override
	public String getKey() {
		return ChatColor.GOLD + "Bank";
	}

	@Override
	public String getValue(Player player) {

		String playerName = player.getName();

		Double balance = Mythsentials.economy.getBalance(playerName);
		Double previousBal = Mythsentials.getMythianManager().getMythian(playerName).getLogoffBalance();

		String value = "";

		if(balance < 0) {
			value = ChatColor.RED + "$" + Double.toString(balance);
		} else {
			value = ChatColor.GREEN + "$" + Double.toString(balance);
		}

		if(balance < previousBal) {
			double difference = Utils.roundTwoDecimals(previousBal - balance);
			value += ChatColor.WHITE + " (-" + difference + ")";
		}
		else if(balance >= previousBal) {
			double difference = Utils.roundTwoDecimals(balance - previousBal);
			value += ChatColor.WHITE + " (+" + difference + ")";
		}
		return value;
	}

	@Override
	public String getCommand() {
		return "/money";
	}

	@Override
	public ItemStack getButton(Player player) {
		// TODO Auto-generated method stub
		return null;
	}
}
