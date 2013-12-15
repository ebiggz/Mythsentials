package com.mythicacraft.plugins.mythsentials.CmdExecutors;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ru.tehkode.permissions.bukkit.PermissionsEx;

import com.bekvon.bukkit.residence.Residence;


public class ResMax implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if(commandLabel.equalsIgnoreCase("resmax")) {
			//get some vars
			Player player = (Player) sender;
			Location loc = player.getLocation();
			int max = getMaxSize(player);

			//make selection
			Residence.getSelectionManager().placeLoc1(player, new Location(player.getWorld(), loc.getX()+max+1, 256.0, loc.getZ()+max+1));
			Residence.getSelectionManager().placeLoc2(player, new Location(player.getWorld(), loc.getX()-max, 1.0, loc.getZ()-max));
			Residence.getSelectionManager().showSelectionInfo(player);

		}
		return true;
	}

	private int getMaxSize(Player player) {
		String[] groups = PermissionsEx.getUser(player).getGroupsNames();
		String group = groups[0];
		switch(group) {
		case "Member":
			return 31;
		case "Donator":
			return 127;
		case "Subscriber":
			return 255;
		case "Moderator":
			return 255;
		case "Admin":
			return 255;
		case "Owner":
			return 255;
		default:
			return 31;
		}
	}
}
