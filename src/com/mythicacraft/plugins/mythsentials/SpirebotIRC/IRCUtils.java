package com.mythicacraft.plugins.mythsentials.SpirebotIRC;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jibble.pircbot.User;
import org.kitteh.vanish.staticaccess.VanishNoPacket;
import org.kitteh.vanish.staticaccess.VanishNotLoadedException;

public class IRCUtils {

	public IRCUtils() {

	}

	public static String getIrcUserList() {
		StringBuilder sbIrcUserList = new StringBuilder();
		String channel = "#MythicaCraft";
		IRCBot bot = IRCBot.getBot();
		try {
			ArrayList<User> users = new ArrayList<User>(Arrays.asList(bot.getUsers(channel)));
			for (int i = 0; i < users.size(); i++) {
				if(users.get(i).getNick().equals("[Global]")) continue;
				sbIrcUserList.append(users.get(i).getNick());
				if(i != users.size()) {
					sbIrcUserList.append(", ");
				}
			}
			return sbIrcUserList.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	public static String completeIRCNick(String user) {
		IRCBot bot = IRCBot.getBot();
		try {
			String channel = "#MythicaCraft";
			ArrayList<User> users = new ArrayList<User>(Arrays.asList(bot.getUsers(channel)));
			for(int i = 0; i < users.size(); i++) {
				if(users.get(i).getNick().toLowerCase().startsWith(user.toLowerCase())) {
					return users.get(i).getNick();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String getGameUserList() {
		Player[] onlinePlayers = Bukkit.getOnlinePlayers();
		StringBuilder gameUserList = new StringBuilder();
		for (int i = 0; i < onlinePlayers.length; i++) {
			try {
				if(VanishNoPacket.isVanished(onlinePlayers[i].getName())) continue;
			} catch (VanishNotLoadedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			gameUserList.append(onlinePlayers[i].getName());
			if(i != onlinePlayers.length-1) {
				gameUserList.append(", ");
			}
		}
		return gameUserList.toString();
	}

	public static int getPlayerCount(){
		int count = 0;
		Player[] onlinePlayers = Bukkit.getOnlinePlayers();
		for (int i = 0; i < onlinePlayers.length; i++) {
			try {
				if(VanishNoPacket.isVanished(onlinePlayers[i].getName())) continue;
				count++;
			} catch (VanishNotLoadedException e) {
				e.printStackTrace();
			}
		}
		return count;
	}

	public static boolean playerIsInGame(String playername) {
		Player[] onlinePlayers = Bukkit.getOnlinePlayers();
		for(int i = 0; i < onlinePlayers.length; i++) {
			String player = onlinePlayers[i].getName();
			if(player.equals(playername)) {
				try {
					if(VanishNoPacket.isVanished(player)) return false;
					return true;
				} catch (VanishNotLoadedException e) {
					e.printStackTrace();
				}
			}
		}
		return false;
	}

	public static boolean userIsInIRC(String username) {
		IRCBot bot = IRCBot.getBot();
		ArrayList<User> usersO = new ArrayList<User>(Arrays.asList(bot.getUsers("#MythicaCraft")));
		ArrayList<User> users1 = new ArrayList<User>(Arrays.asList(bot.getUsers("#MythicaStaff")));
		ArrayList<String> users = new ArrayList<String>();
		for(User user: usersO) {
			users.add(user.getNick());
		}
		for(User user: users1) {
			users.add(user.getNick());
		}
		return users.contains(username);
	}

	public static User getUser(String nick, String channel) {
		IRCBot bot = IRCBot.getBot();
		ArrayList<User> usersO = new ArrayList<User>(Arrays.asList(bot.getUsers(channel)));
		for(User user: usersO) {
			if(user.getNick().equals(nick)) return user;
		}
		return null;
	}

	public static String completePlayerName(String playername) {
		Player[] onlinePlayers = Bukkit.getOnlinePlayers();
		for(int i = 0; i < onlinePlayers.length; i++) {
			if(onlinePlayers[i].getName().toLowerCase().startsWith(playername.toLowerCase())) {
				try {
					if(!VanishNoPacket.isVanished(onlinePlayers[i].getName())) {
						return onlinePlayers[i].getName();
					}
				} catch (VanishNotLoadedException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}
}
