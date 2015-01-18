package com.mythicacraft.plugins.mythsentials.SpirebotIRC;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jibble.pircbot.Colors;
import org.jibble.pircbot.IrcException;
import org.jibble.pircbot.PircBot;
import org.jibble.pircbot.User;

import com.bekvon.bukkit.residence.Residence;
import com.bekvon.bukkit.residence.protection.ClaimedResidence;
import com.dthielke.herochat.Channel;
import com.dthielke.herochat.Chatter;
import com.dthielke.herochat.Herochat;
import com.mythicacraft.plugins.mythsentials.Mythian;
import com.mythicacraft.plugins.mythsentials.Mythsentials;

public class IRCBot extends PircBot {

	Channel global = Herochat.getChannelManager().getChannel("Global");

	private static final Logger log = Logger.getLogger("Minecraft");

	private static IRCBot bot;

	public IRCBot() {
		this.setName("[Spirebot]");
	}

	public static void makeBot() {

		bot = new IRCBot();

		try {

			bot.setAutoNickChange(true);
			bot.connect("chat.freenode.net");
			if (bot.isConnected()) {
				log.info("Spirebot - Connected!");
			} else {
				log.info("Spirebot - Connection failed!");
			}

			authenticateBot(bot);

		} catch (IOException e) {
			e.printStackTrace();
		} catch (IrcException e) {
			e.printStackTrace();
		}

		bot.joinChannel("#MythicaCraft");
		bot.joinChannel("##MythicaStaff", "dirko123");
	}

	static void authenticateBot(IRCBot bot) {
		bot.sendMessage("nickserv", "GHOST " + "[Spirebot] " + "sundwall");
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		bot.changeNick("[Spirebot]");
		bot.identify("sundwall");
	}

	//Bot methods!
	public void onMessage(String channel, String sender, String login, String hostname, String message) {

		String hcChannel = "Global";
		if(channel.equals("##MythicaStaff")) {
			hcChannel = "ModChat";
		}
		if(!(sender.equals("[Global]") || sender.equals("[ModChat]") || sender.equals("[Spirebot]"))) {
			Event ircToChannel = new IRCToChannelEvent(hcChannel, sender, message);
			Bukkit.getServer().getPluginManager().callEvent(ircToChannel);
		}

		if(sender.equals("[Spirebot]")) return;

		/*User user = IRCUtils.getUser(sender, channel);

		if(!user.hasVoice()) {
			if(!user.isOp()) {
				sendMessage(sender, Colors.YELLOW + "Reminder! You are currently muted. Message me \"?help\" for help with commands.");
				return;
			}
		}*/

		if(channel.equals("#MythicaCraft")) {
			if(!sender.equals("[Global]")) {
				Mythsentials.writeLog("(" + sender + ") " + message);
			}
		}

		if(message.equalsIgnoreCase("?map")) {
			sendMessage(channel, "You can view our live map at www.mythicacraft.com/map");
		}
	}
	public static IRCBot getBot() {
		return bot;
	}

	public void onJoin(String channel, String sender, String login, String hostname) {

		String hcChannel = "Global";
		if(channel.equals("##MythicaStaff")) {
			hcChannel = "ModChat";
		}
		if(!(sender.equals("[Global]") || sender.equals("[ModChat]") || sender.equals("[Spirebot]"))) {
			Event ircToChannel = new IRCToChannelEvent(hcChannel, sender, "connect");
			Bukkit.getServer().getPluginManager().callEvent(ircToChannel);
		}

		if(channel.equals("#MythicaCraft")) {
			if(!(sender.equals("[Global]") || sender.equals("[Spirebot]"))) {
				Mythsentials.writeLog("[" + sender + " joined channel]");
				sendMessage(sender, Colors.BLUE + "Welcome to the " + Colors.NORMAL + "#MythicaCraft" + Colors.BLUE + " IRC channel!");
				sendMessage(sender, Colors.BLUE + "To be able to speak, you must be a member of the Mythica community. Message me \"" + Colors.NORMAL +  "?login MinecraftName ForumPassword" + Colors.BLUE + "\" to verify yourself! Type \"" + Colors.NORMAL + "?help" + Colors.BLUE + "\" for more info.");
				sendMessage(sender, Colors.BLUE + "Anything you send to me is completely private.");
				sendMessage(channel, Colors.BLUE + "Welcome " + sender + "! I've private messaged you with details on how to speak in this channel.");
			}
		}
		if(sender.equalsIgnoreCase("rockjolt") || sender.equalsIgnoreCase("rockjolt375")) {
			sendMessage(channel, "Rock! My hero!");
			return;
		}
	}

	public void onPart(String channel, String sender, String login, String hostname) {

		String hcChannel = "Global";
		if(channel.equals("##MythicaStaff")) {
			hcChannel = "ModChat";
		}
		if(!(sender.equals("[Global]") || sender.equals("[ModChat]") || sender.equals("[Spirebot]"))) {
			Event ircToChannel = new IRCToChannelEvent(hcChannel, sender, "disconnect");
			Bukkit.getServer().getPluginManager().callEvent(ircToChannel);
		}

		if(channel.equals("#MythicaCraft")) {
			if(!sender.equals("[Global]") || !sender.equals("[Spirebot]")) {
				Mythsentials.writeLog("[" + sender + " left channel]");
			}
		}
	}



	//private commands
	public void onPrivateMessage(String sender, String login, String hostname, String message) {

		if(sender.equals("[Spirebot]")) return;

		if(!IRCUtils.userIsInIRC(sender)) {
			sendMessage(sender, Colors.YELLOW + "You must be in the #MythicaCraft channel to use my commands.");
			return;
		}

		Pattern checkRegex = Pattern.compile("^@(.*?)\\s");
		Matcher regexMatcher = checkRegex.matcher(message);

		if(message.toLowerCase().startsWith("hello")) {
			sendMessage(sender, "Hi, " + sender + "!");
		}
		else if(message.equalsIgnoreCase("?players")) {
			String players = IRCUtils.getGameUserList();
			int playerCount = IRCUtils.getPlayerCount();
			if(playerCount == 0) {
				sendMessage(sender, Colors.YELLOW + "There's nobody minecrafting right now.");
			} else {
				sendMessage(sender, Colors.YELLOW + "Online (" + playerCount + "/" + Bukkit.getServer().getMaxPlayers() + "): " + players);
			}
		}
		else if(message.equalsIgnoreCase("?help")) {
			sendMessage(sender, Colors.YELLOW+ "-----Player Help Menu-----");
			sendMessage(sender, Colors.GREEN + "?login [username] [password]" + Colors.NORMAL + " - Verifiy your Mythica Membership.");
			sendMessage(sender, Colors.NORMAL+ "   Example: " + Colors.LIGHT_GRAY + "?login Notch minecraft123");
			sendMessage(sender, Colors.DARK_GREEN + "   The username and password are the same ones you used when you first registered for Mythica! (AKA the ones you use to log into our forums)");
			sendMessage(sender, Colors.DARK_GREEN + "   If your IRC nickname is the same as your Mythica username, you can just type your password, IE: " + Colors.BLUE + "?login minecraft123");
			sendMessage(sender, Colors.GREEN + "?players" + Colors.NORMAL + " - List of in-game players");
			sendMessage(sender, Colors.GREEN + "@PlayerName [Message]" + Colors.NORMAL + " - Send a private message to the given player");
			sendMessage(sender, Colors.DARK_GREEN + "   Example: " + Colors.BLUE + "@ebiggz hello!");
			sendMessage(sender, Colors.GREEN + "?help" + Colors.NORMAL + " - This help menu");
			sendMessage(sender, Colors.YELLOW+ "-------------------");
		}

		else if(message.equalsIgnoreCase("?help mod")) {
			User user = IRCUtils.getUser(sender, "#MythicaCraft");
			if(user.isOp()) {
				sendMessage(sender, Colors.YELLOW+ "-----Mod Help Menu-----");
				sendMessage(sender, Colors.GREEN + "?sc [message]" + Colors.NORMAL + " - Send a message to SpawnChat.");
				sendMessage(sender, Colors.GREEN + "?kick [player]" + Colors.NORMAL + " - Kick the given in-game player.");
				sendMessage(sender, Colors.GREEN + "?ban [player]" + Colors.NORMAL + " - Bans the given in-game player.");
				sendMessage(sender, Colors.GREEN + "?unban [player]" + Colors.NORMAL + " - Unbans the given player.");
				sendMessage(sender, Colors.GREEN + "?commands [player]" + Colors.NORMAL + " - See the recent commmands by a player.");
				sendMessage(sender, Colors.GREEN + "?help mod" + Colors.BLUE + " - This help menu");
				sendMessage(sender, "Mods will get a PM from Spirebot when a new player joins, when they successfully register, and when someone chats in Spawn.");
				sendMessage(sender, Colors.YELLOW+ "-------------------");
			}
		}

		else if(message.toLowerCase().startsWith("?sc ")) {
			User user = IRCUtils.getUser(sender, "#MythicaCraft");
			if(user.isOp()) {
				ClaimedResidence spire = Residence.getResidenceManager().getByName("Spawn");
				ArrayList<Player> spawnPlayers = spire.getPlayersInResidence();
				if(spawnPlayers.size() == 0) {
					sendMessage(sender, Colors.RED + "There is no one in Spawn.");
				}

				String chatMessage = ChatColor.YELLOW + "[L] " + ChatColor.WHITE + user.getNick() + ChatColor.YELLOW + ": " + message.replace("?sc ", "").trim();
				for(int i = 0; i < spawnPlayers.size(); i++) {
					spawnPlayers.get(i).sendMessage(chatMessage);
				}

				String chatMessage2 = ChatColor.YELLOW + "[SpawnChat](IRC) " + ChatColor.WHITE + user.getNick() + ChatColor.YELLOW + ": " + message.replace("?sc ", "").trim();
				Channel spawnChat = Herochat.getChannelManager().getChannel("SpawnChat");
				Set<Chatter> members = spawnChat.getMembers();
				Iterator<Chatter> it = members.iterator();
				while(it.hasNext()) {
					Player p = it.next().getPlayer();
					if(Residence.getResidenceManager().getByLoc(p.getLocation()) == spire) continue;
					p.sendMessage(chatMessage2);
				}
			}
		}

		else if(message.toLowerCase().startsWith("?ban ")) {
			User user = IRCUtils.getUser(sender, "#MythicaCraft");
			if(user.isOp()) {
				String rawName = message.replace("?ban ", "").trim();
				String player = IRCUtils.completePlayerName(rawName);
				if(player != null) {
					sendMessage(sender, "Attempting to ban: " + player);
					Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "ban " + player + " Make an issue to appeal ban");
				} else {
					sendMessage(sender, Colors.RED + "Sorry, " + rawName + " isn't recognized!");
				}
			}
		}

		else if(message.toLowerCase().startsWith("?unban ")) {
			User user = IRCUtils.getUser(sender, "#MythicaCraft");
			if(user.isOp()) {
				String rawName = message.replace("?unban ", "").trim();
				String player = IRCUtils.completePlayerName(rawName);
				if(player != null) {
					sendMessage(sender, "Attempting to unban: " + player);
					Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "unban " + player);
				} else {
					sendMessage(sender, Colors.RED + "Sorry, " + rawName + " isn't recognized!");
				}
			}
		}

		else if(message.toLowerCase().startsWith("?kick ")) {
			User user = IRCUtils.getUser(sender, "#MythicaCraft");
			if(user.isOp()) {
				String rawName = message.replace("?kick ", "").trim();
				String player = IRCUtils.completePlayerName(rawName);
				if(player != null) {
					sendMessage(sender, "Attempting to kick: " + player);
					Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "kick " + player);
				} else {
					sendMessage(sender, Colors.RED + "Sorry, " + rawName + " isn't recognized!");
				}
			}
		}

		else if(message.toLowerCase().startsWith("?commands ")) {
			User user = IRCUtils.getUser(sender, "#MythicaCraft");
			if(user.isOp()) {
				String rawName = message.replace("?commands ", "").trim();
				String player = IRCUtils.completePlayerName(rawName);
				if(player != null) {
					Mythian mythian = Mythsentials.getMythianManager().getMythian(player);
					List<String> commands = mythian.getRecentCommands();
					StringBuilder sb = new StringBuilder();
					for(String command : commands) {
						sb.append(command + ", ");
					}
					sb.delete(sb.length()-2, sb.length()-1);
					sendMessage(sender, "Recent commands by " + player + ":");
					sendMessage(sender, sb.toString());
				} else {
					sendMessage(sender, Colors.RED + "Sorry, " + rawName + " isn't recognized!");
				}
			}
		}

		else if(message.toLowerCase().startsWith("?login")) {


			User user = IRCUtils.getUser(sender, "#MythicaCraft");
			if(user.hasVoice() || user.isOp()) {
				sendMessage(sender, Colors.RED + "You are already verified!");
				return;
			}

			if(message.trim().toLowerCase().equals("?login")) {
				sendMessage(sender, Colors.RED + "You must include a username and password! Type \"?help\" for more.");
				return;
			}
			String creds = message.trim().substring(7, message.length());
			String[] splits = creds.split(" ");
			String username = null, password = null;
			if(splits.length == 1) {
				password = splits[0];
				username = sender;
			}
			if(splits.length == 2) {
				username = splits[0];
				password = splits[1];
			}
			if(splits.length < 1 || splits.length > 2) {
				sendMessage(sender, Colors.RED + "You've entered your login incorrectly! Type ?help");
				return;
			}
			String reply = checkLogin(username, password);
			if(reply.startsWith("1")) {
				String mcName = reply.substring(reply.indexOf(",")+1, reply.length()).trim();
				OfflinePlayer offlineP = Bukkit.getOfflinePlayer(mcName);
				if(offlineP != null) {
					if(offlineP.isBanned()) {
						sendMessage(sender, Colors.RED + "Sorry, " + sender + ", but you have been banned from Mythica.");
						return;
					}
				}
				authorizeUser(sender, mcName);
			} else {
				sendMessage(sender, Colors.RED + "Sorry, " + sender + ", that username/password combination was not recognized.");
			}
		}
		else if(regexMatcher.find()) {
			String playername = regexMatcher.group(1);
			message = message.replaceFirst("@"+playername+" ", "");
			String player = IRCUtils.completePlayerName(playername);
			if(player != null) {
				Player recipitent = Bukkit.getPlayerExact(player);
				recipitent.sendMessage(ChatColor.LIGHT_PURPLE + "From " + sender + "(IRC): " + message);
			} else {
				sendMessage(sender, Colors.RED + "Sorry, " + playername + " isn't online right now!");
			}
		} else {
			sendMessage(sender, Colors.RED + "I wasn't able to find an action to do with what you said. Type ?help to see the things you can say to me!");
		}
	}

	void authorizeUser(String nick, String mcName) {
		if(!IRCUtils.userIsInIRC(nick)) {
			sendMessage(nick, Colors.RED + "There was an issue authorizing you. Make sure you are in the #MythicaCraft channel and try again.");
			return;
		}
		String world = null;
		if(Mythsentials.permission.playerInGroup(world, mcName, "Moderator") || Mythsentials.permission.playerInGroup(world, mcName, "Admin") || Mythsentials.permission.playerInGroup(world, mcName, "Owner")) {
			op("#MythicaCraft", nick);
			this.sendInvite(nick, "##MythicaStaff");
		}
		voice("#MythicaCraft", nick);
		sendMessage(nick, Colors.BLUE + "You've been verified! You may now speak in #MythicaCraft :)");
		sendMessage(nick, Colors.NORMAL + "And remember, you can type " + Colors.YELLOW + "/nick [name]" + Colors.NORMAL + " to change your nickname.");

	}
	public String checkLogin(String username, String password) {
		String url = "http://www.mythicacraft.com/regcheck.php?username=" + username + "&password=" + password;
		String reply = "";
		try {
			URL u = new URL(url);
			URLConnection c = u.openConnection();
			BufferedReader in = new BufferedReader(
					new InputStreamReader(
							c.getInputStream()));
			reply = in.readLine();
			in.close();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return reply;
	}
}

