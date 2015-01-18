package com.mythicacraft.plugins.mythsentials.Store;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.mythicacraft.plugins.mythsentials.MythianManager;
import com.mythicacraft.plugins.mythsentials.Mythsentials;


public class PurchaseCmds implements CommandExecutor {

	StoreManager sm = Mythsentials.getStoreManager();
	MythianManager mm = Mythsentials.getMythianManager();

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {

		if(commandLabel.equalsIgnoreCase("store")) {

			if(!sender.isOp()) return true;

			if(args.length == 3) {
				if(args[0].equalsIgnoreCase("credit")) {

					String player = (String) args[1];
					String itemName = (String) args[2];

					StoreManager sm = Mythsentials.getStoreManager();

					if(!sm.hasItem(itemName)) return true;

					sm.administerItemContents(sm.getItem(itemName), player);

				}
			}
		}
		return true;
	}
}
