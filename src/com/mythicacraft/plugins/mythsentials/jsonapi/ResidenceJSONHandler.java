package com.mythicacraft.plugins.mythsentials.JsonAPI;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.alecgorge.minecraft.jsonapi.api.APIMethodName;
import com.alecgorge.minecraft.jsonapi.api.JSONAPICallHandler;
import com.bekvon.bukkit.residence.Residence;
import com.bekvon.bukkit.residence.protection.ClaimedResidence;
import com.bekvon.bukkit.residence.protection.FlagPermissions.FlagState;


public class ResidenceJSONHandler implements JSONAPICallHandler {
	// if you return true here, you WILL handle the API call in the handle method.
	// will be called twice for every API call, once to test if the method exists and once on the actuall call
	public boolean willHandle(APIMethodName methodName) {
		if(methodName.matches("getPlayerResidences")) {
			return true;
		}
		if(methodName.matches("getResidenceInfo")) {
			return true;
		}
		if(methodName.matches("setResidenceFlag")) {
			return true;
		}
		if(methodName.matches("setPlayerFlag")) {
			return true;
		}
		if(methodName.matches("setResidenceMessage")) {
			return true;
		}
		if(methodName.matches("setResidenceName")) {
			return true;
		}
		return false;
	}

	// the result of this method will be treated as the response to the API request, even if null is returned (some methods do return null)
	public Object handle(APIMethodName methodName, Object[] args) {
		if(methodName.matches("getPlayerResidences")) {
			String player = (String) args[0];
			ArrayList<String> playerResidences = Residence.getResidenceManager().getResidenceList(player, true, false);
			String[] residenceNames = new String [playerResidences.size()];
			playerResidences.toArray(residenceNames);
			return residenceNames;
		}
		if(methodName.matches("getResidenceInfo")) {
			String resName = (String) args[0];
			ClaimedResidence res = Residence.getResidenceManager().getByName(resName);
			HashMap<Object,Object> resInfo = new HashMap<Object, Object>();
			HashMap<Object, Object> flags = new HashMap<Object, Object>();
			flags.put("global", res.getPermissions().listFlags().split(" "));
			HashMap<String, String[]> playerFlags = new HashMap<String, String[]>();
			String[] allPlayersWithFlags = ChatColor.stripColor(res.getPermissions().listOtherPlayersFlags(res.getOwner())).split("] ");
			ArrayList<String> flagPlayers = new ArrayList<String>();
			if(allPlayersWithFlags.length > 0) {
				for(int i = 0; i < allPlayersWithFlags.length; i++) {
					String[] playerAndFlags = allPlayersWithFlags[i].split("\\[");
					if(playerAndFlags.length > 1) {
						flagPlayers.add(playerAndFlags[0]);
						playerFlags.put(playerAndFlags[0], playerAndFlags[1].split(" "));
					}
				}
				String[] flagPlayerArray = new String[flagPlayers.size()];
				flagPlayers.toArray(flagPlayerArray);
				playerFlags.put("players", flagPlayerArray);
			}
			flags.put("player", playerFlags);
			resInfo.put("name", res.getName());
			resInfo.put("owner", res.getOwner());
			resInfo.put("world", res.getWorld());
			resInfo.put("subzones", res.getSubzoneList());
			resInfo.put("flags", flags);
			resInfo.put("enter_message", res.getEnterMessage());
			resInfo.put("leave_message", res.getLeaveMessage());
			if(res.getParent() != null) {
				resInfo.put("immediate_parent", res.getParent().getName());
			} else {
				resInfo.put("immediate_parent", "None");
			}
			if(res.getTopParent() != null) {
				if(res.getTopParent().getName().equals(res.getName())) {
					resInfo.put("top_parent","None");
				} else {
					resInfo.put("top_parent", res.getTopParent().getName());
				}
			}
			String[] playersInRes = new String[res.getPlayersInResidence().size()];
			ArrayList<Player> pir = res.getPlayersInResidence();
			for(int i = 0; i < playersInRes.length; i++) {
				playersInRes[i] = pir.get(i).getName();
			}
			resInfo.put("players_in_res", playersInRes);
			return resInfo;
		}
		if(methodName.matches("setResidenceFlag")) {

			String residence = (String) args[0];
			String flag = (String) args[1];

			FlagState fs = null;
			if(args[2] != null) {
				boolean state = (boolean) args[2];
				if(state) fs = FlagState.TRUE;
				else fs = FlagState.FALSE;
			} else {
				fs = FlagState.NEITHER;
			}
			Residence.getResidenceManager().getByName(residence).getPermissions().setFlag(flag, fs);
			return true;
		}
		if(methodName.matches("setPlayerFlag")) {

			String residence = (String) args[0];
			String player = (String) args[1];
			String flag = (String) args[2];

			FlagState fs = null;
			if(args[3] != null) {
				boolean state = (boolean) args[3];
				if(state) fs = FlagState.TRUE;
				else fs = FlagState.FALSE;
			} else {
				fs = FlagState.NEITHER;
			}
			Residence.getResidenceManager().getByName(residence).getPermissions().setPlayerFlag(player, flag, fs);
			return true;
		}
		if(methodName.matches("setResidenceMessage")) {
			String residence = (String) args[0];
			String message = (String) args[1];
			String type = (String) args[2];
			if(type.equalsIgnoreCase("enter")) {
				Residence.getResidenceManager().getByName(residence).setEnterMessage(message);
			}
			else if(type.equalsIgnoreCase("leave")) {
				Residence.getResidenceManager().getByName(residence).setLeaveMessage(message);
			}
			return true;
		}
		if(methodName.matches("setResidenceName")) {
			String residence = (String) args[0];
			String newName = (String) args[1];
			Residence.getResidenceManager().renameResidence(residence, newName);
			return true;
		}
		return false;
	}
}
