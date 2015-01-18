package com.mythicacraft.plugins.mythsentials.Censor;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.milkbowl.vault.chat.Chat;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.dthielke.herochat.Channel;
import com.dthielke.herochat.ChannelChatEvent;
import com.dthielke.herochat.Chatter;
import com.dthielke.herochat.Chatter.Result;
import com.dthielke.herochat.ConversationChannel;
import com.dthielke.herochat.Herochat;
import com.mythicacraft.plugins.mythsentials.Mythian;
import com.mythicacraft.plugins.mythsentials.Mythsentials;
import com.mythicacraft.plugins.mythsentials.Utilities.Utils;

public class CensorListener implements Listener {

	@EventHandler(priority= EventPriority.MONITOR)
	public void onChannelChat(ChannelChatEvent event) {
		if(Mythsentials.shouldCensor) {
			if(event.getResult() == Result.ALLOWED) {
				event.setResult(Result.FAIL);
				if(event.getChannel() instanceof ConversationChannel) {

					Player player = event.getSender().getPlayer();
					String senderName = player.getName();
					Chatter sender = Herochat.getChatterManager().getChatter(player);
					Channel channel = event.getChannel();

					Mythian mythianSender = Mythsentials.getMythianManager().getMythian(player.getName());

					String format = event.getFormat();
					for (Chatter member : channel.getMembers()) {
						Player memberPlayer = member.getPlayer();
						Mythian mythian = Mythsentials.getMythianManager().getMythian(memberPlayer.getName());
						String message = event.getMessage();
						if(mythian.getCensorChat()) {
							message = Utils.censorString(message);
						}
						if(mythianSender.getCensorChatGlobal()) {
							message = Utils.censorStringFunny(message);
						}

						if ((!member.isIgnoring(senderName)) || (member.canIgnore(Herochat.getChatterManager().getChatter(senderName)) == Chatter.Result.NO_PERMISSION)) {
							String appliedFormat = applyFormatConvo(format, player, memberPlayer, channel);
							memberPlayer.sendMessage(appliedFormat.replace("{msg}", ChatColor.translateAlternateColorCodes('&', message)));
						}
						else {
							return;
						}

						if ((!sender.equals(member)) && (member.isAFK())) {
							String afkMsg = member.getAFKMessage();
							afkMsg = "<AFK> " + afkMsg;
							player.sendMessage(applyFormatConvo(format, memberPlayer, player, channel).replace("{msg}", afkMsg));
						}

						if (!sender.equals(member)) {
							member.setLastPrivateMessageSource(sender);
							Herochat.logChat(senderName + " -> " + member.getName() + ": " + event.getMessage());

						}
					}
				} else {
					Player player = event.getSender().getPlayer();

					String format = applyFormatStandard(event.getFormat(), event.getBukkitFormat(), player, event.getChannel());

					Chatter sender = Herochat.getChatterManager().getChatter(player);
					Set<Player> recipients = new HashSet<Player>(Arrays.asList(Bukkit.getOnlinePlayers()));

					trimRecipients(recipients, sender, event.getChannel());
					Mythian mythian = Mythsentials.getMythianManager().getMythian(player.getName());
					String msg = String.format(format, new Object[] { player.getDisplayName(), event.getMessage() });
					if(mythian.getCensorChatGlobal()) {
						msg = Utils.censorStringFunny(msg);
					}
					String censoredMsg = Utils.censorString(msg);
					for (Player pl : recipients) {
						if(Mythsentials.getMythianManager().getMythian(pl.getName()).getCensorChat()) {
							pl.sendMessage(ChatColor.translateAlternateColorCodes('&',censoredMsg));
						} else {
							pl.sendMessage(ChatColor.translateAlternateColorCodes('&',msg));
						}
					}
					//Bukkit.getPluginManager().callEvent(new Herochat.ChatCompleteEvent(sender, this, msg));
					Herochat.logChat(msg);
				}
			}
		}
	}

	private void trimRecipients(Set<Player> recipients, Chatter sender, Channel c) {
		World world = sender.getPlayer().getWorld();
		for (Iterator<Player> iterator = recipients.iterator(); iterator.hasNext(); ) {
			Chatter recipient = Herochat.getChatterManager().getChatter((Player)iterator.next());
			if (recipient != null)
			{
				World recipientWorld = recipient.getPlayer().getWorld();
				if (!c.getMembers().contains(recipient))
					iterator.remove();
				else if ((c.isLocal()) && (!sender.isInRange(recipient, c.getDistance())))
					iterator.remove();
				else if (!c.hasWorld(recipientWorld))
					iterator.remove();
				else if (recipient.isIgnoring(sender))
					iterator.remove();
				else if ((!c.isCrossWorld()) && (!world.equals(recipientWorld)))
					iterator.remove();
			}
		}
	}

	public String applyFormatStandard(String format, String originalFormat, Player sender, Channel c)
	{
		format = applyFormatStandard(format, originalFormat, c);
		format = format.replace("{plainsender}", sender.getName());
		format = format.replace("{world}", sender.getWorld().getName());
		Chat chat = Herochat.getChatService();
		if (chat != null) {
			try {
				String prefix = chat.getPlayerPrefix(sender);
				if ((prefix == null) || (prefix == "")) {
					prefix = chat.getPlayerPrefix((String)null, sender.getName());
				}
				String suffix = chat.getPlayerSuffix(sender);
				if ((suffix == null) || (suffix == "")) {
					suffix = chat.getPlayerSuffix((String)null, sender.getName());
				}
				String group = chat.getPrimaryGroup(sender);
				String groupPrefix = group == null ? "" : chat.getGroupPrefix(sender.getWorld(), group);
				if ((group != null) && ((groupPrefix == null) || (groupPrefix == ""))) {
					groupPrefix = chat.getGroupPrefix((String)null, group);
				}
				String groupSuffix = group == null ? "" : chat.getGroupSuffix(sender.getWorld(), group);
				if ((group != null) && ((groupSuffix == null) || (groupSuffix == ""))) {
					groupSuffix = chat.getGroupSuffix((String)null, group);
				}
				format = format.replace("{prefix}", prefix == null ? "" : prefix.replace("%", "%%"));
				format = format.replace("{suffix}", suffix == null ? "" : suffix.replace("%", "%%"));
				format = format.replace("{group}", group == null ? "" : group.replace("%", "%%"));
				format = format.replace("{groupprefix}", groupPrefix == null ? "" : groupPrefix.replace("%", "%%"));
				format = format.replace("{groupsuffix}", groupSuffix == null ? "" : groupSuffix.replace("%", "%%"));
			} catch (UnsupportedOperationException ignored) {
			}
		} else {
			format = format.replace("{prefix}", "");
			format = format.replace("{suffix}", "");
			format = format.replace("{group}", "");
			format = format.replace("{groupprefix}", "");
			format = format.replace("{groupsuffix}", "");
		}
		format = ChatColor.translateAlternateColorCodes('&', format);
		return format;
	}

	public String applyFormatStandard(String format, String originalFormat, Channel c)
	{
		Pattern msgPattern = Pattern.compile("(.*)<(.*)%1\\$s(.*)> %2\\$s");
		format = format.replace("{default}",  c.getFormatSupplier().getStandardFormat());
		format = format.replace("{name}", c.getName());
		format = format.replace("{nick}", c.getNick());
		format = format.replace("{color}", c.getColor().toString());
		format = format.replace("{msg}", "%2$s");

		Matcher matcher = msgPattern.matcher(originalFormat);
		if ((matcher.matches()) && (matcher.groupCount() == 3))
			format = format.replace("{sender}", matcher.group(1) + matcher.group(2) + "%1$s" + matcher.group(3));
		else {
			format = format.replace("{sender}", "%1$s");
		}

		format = ChatColor.translateAlternateColorCodes('&', format);
		return format;
	}
	public String applyFormatConvo(String format, Player sender, Player recipient, Channel channel) {
		if (sender.equals(recipient)) {
			Player target = null;
			for (Chatter chatter : channel.getMembers()) {
				if (!chatter.getPlayer().equals(sender)) {
					target = chatter.getPlayer();
					break;
				}
			}
			if (target != null) {
				format = format.replace("{convoaddress}", "To");
				format = format.replace("{convopartner}", target.getDisplayName());
			}
		} else {
			format = format.replace("{convoaddress}", "From");
			format = format.replace("{convopartner}", sender.getDisplayName());
		}
		format = format.replaceAll("(?i)&([0-9a-fk-or])", "§$1");
		return format;
	}

}