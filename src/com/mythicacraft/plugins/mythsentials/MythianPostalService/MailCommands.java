package com.mythicacraft.plugins.mythsentials.MythianPostalService;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitScheduler;

import com.mythicacraft.plugins.mythsentials.Mythian;
import com.mythicacraft.plugins.mythsentials.Mythsentials;
import com.mythicacraft.plugins.mythsentials.MythianPostalService.MailboxManager.MailboxSelect;
import com.mythicacraft.plugins.mythsentials.MythianPostalService.mailtypes.Experience;
import com.mythicacraft.plugins.mythsentials.MythianPostalService.mailtypes.Mail;
import com.mythicacraft.plugins.mythsentials.MythianPostalService.mailtypes.Mail.MailStatus;
import com.mythicacraft.plugins.mythsentials.MythianPostalService.mailtypes.Mail.MailType;
import com.mythicacraft.plugins.mythsentials.MythianPostalService.mailtypes.PackageObj;
import com.mythicacraft.plugins.mythsentials.MythianPostalService.mailtypes.Payment;
import com.mythicacraft.plugins.mythsentials.Utilities.FancyMenu;
import com.mythicacraft.plugins.mythsentials.Utilities.SetExpFix;
import com.mythicacraft.plugins.mythsentials.Utilities.Time;
import com.mythicacraft.plugins.mythsentials.Utilities.Utils;


public class MailCommands implements CommandExecutor {

	enum Tracking {
		TO, MESSAGE, AMOUNT
	}

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {

		final Player cmdPlayer = (Player) sender;

		String[] commandData = {
				"&7&o(Hover over a &a&ocommand &7&ofor info, click to run it)",
				"TELLRAW run>>/mail compose>>/mail compose>>Compose mail to send to other players.",
				"TELLRAW run>>/mailbox set>>/mailbox set>>Register a chest as a mailbox.",
				"TELLRAW run>>/mailbox clear>>/mailbox clear>>Unregister a chest as a mailbox.",
				"TELLRAW suggest>>/mailbox clearall>>/mailbox clearall>>Unregister all your mailboxes."
		};

		if(commandLabel.equalsIgnoreCase("mailbox")) {
			if(args.length == 0) {
				FancyMenu.showClickableCommandList(sender, commandLabel, "Mail Commands", commandData, 1);
			}
			else if(args.length == 1) {
				if(args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("set")) {
					Mythian mythian = Mythsentials.getMythianManager().getMythian(sender.getName());
					if(mythian.getMailboxLocs().size() >= MailboxManager.getInstance().getMaxMailboxCount(sender.getName())) {
						sender.sendMessage(ChatColor.RED + "[Mythica] Can't add another mailbox! You have reached your max allotment of mailboxes.");
						return true;
					}
					MailboxManager.getInstance().mailboxSelectors.put((Player) sender, MailboxSelect.ADD);
					sender.sendMessage(ChatColor.YELLOW + "[Mythica] " + ChatColor.DARK_AQUA + "Please click a chest to register it as a mailbox...");
				}
				else if(args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("clear")) {
					MailboxManager.getInstance().mailboxSelectors.put((Player) sender, MailboxSelect.REMOVE);
					sender.sendMessage(ChatColor.YELLOW + "[Mythica] " + ChatColor.DARK_AQUA + "Please click a chest to unregister it as a mailbox...");

				}
				else if(args[0].equalsIgnoreCase("removeall") || args[0].equalsIgnoreCase("clearall")) {
					MailboxManager.getInstance().removeAllMailboxes(sender.getName());
					sender.sendMessage(ChatColor.YELLOW + "[Mythica] " + ChatColor.DARK_AQUA + "Unregistered all your mailboxes.");
				}

				if(args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("set") || args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("clear")) {

					BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
					scheduler.scheduleSyncDelayedTask(Mythsentials.getPlugin(), new Runnable() {
						@Override
						public void run() {
							if(MailboxManager.getInstance().mailboxSelectors.containsKey(cmdPlayer)) {
								MailboxManager.getInstance().mailboxSelectors.remove(cmdPlayer);
								cmdPlayer.sendMessage(ChatColor.YELLOW + "[Mythica] " + ChatColor.AQUA + "Mailbox selection timed out.");
							}
						}
					}, 100L);
				}
			}
		}

		if(commandLabel.equalsIgnoreCase("mail") || commandLabel.equalsIgnoreCase("mailadmin")) {

			if(commandLabel.equalsIgnoreCase("mailadmin") && !sender.isOp()){
				return true;
			}

			if(args.length == 0) {
				FancyMenu.showClickableCommandList(sender, commandLabel, "Mail Commands", commandData, 1);
			}
			else if(args.length == 1) {
				if(args[0].equalsIgnoreCase("compose")) {
					String command = "tellraw {player} {\"text\":\"\",\"extra\":[{\"text\":\"[Mythica] Compose mail (Click one): \",\"color\":\"yellow\",\"clickEvent\":{\"action\":\"suggest_command\",\"value\":\"\"}},{\"text\":\"Letter, \",\"color\":\"aqua\",\"clickEvent\":{\"action\":\"suggest_command\",\"value\":\"/mail letter to: message:\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"\",\"extra\":[{\"text\":\"Mail a text-only letter!\",\"color\":\"gold\"}]}}},{\"text\":\"Package, \",\"color\":\"aqua\",\"clickEvent\":{\"action\":\"suggest_command\",\"value\":\"/mail package to: message:\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"\",\"extra\":[{\"text\":\"Mail a package with the items in your drop box!\",\"color\":\"gold\"}]}}},{\"text\":\"Payment, \",\"color\":\"aqua\",\"clickEvent\":{\"action\":\"suggest_command\",\"value\":\"/mail payment to: amount: message:\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"\",\"extra\":[{\"text\":\"Mail a money payment!\",\"color\":\"gold\"}]}}},{\"text\":\"Experience\",\"color\":\"aqua\",\"clickEvent\":{\"action\":\"suggest_command\",\"value\":\"/mail xp to: amount: message:\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"\",\"extra\":[{\"text\":\"Mail xp points (not levels) to a player!!\",\"color\":\"gold\"}]}}}]}";
					Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), command.replace("{player}", sender.getName()));
				}
				//too short
			}

			else {
				//check if mailbox is near by
				if(!locationIsNearMailbox(cmdPlayer.getLocation(), 6)) {
					sender.sendMessage(ChatColor.RED + "[Mythica] You must be near a mailbox to send mail!");
					return true;
				}
				MailType mailType;
				String mailTypeStr = args[0];
				if(mailTypeStr.equalsIgnoreCase("letter")) {
					mailType = MailType.LETTER;
				}
				else if(mailTypeStr.equalsIgnoreCase("package")) {
					mailType = MailType.PACKAGE;
				}
				else if(mailTypeStr.equalsIgnoreCase("payment")) {
					mailType = MailType.PAYMENT;
				}
				else if(mailTypeStr.equalsIgnoreCase("experience") || mailTypeStr.equalsIgnoreCase("xp") || mailTypeStr.equalsIgnoreCase("exp")) {
					mailType = MailType.EXPERIENCE;
				}
				else {
					sender.sendMessage(ChatColor.RED + "[Mythica] \"" + mailTypeStr + "\" is not a recognized mail type. Type /mail for help.");
					return true;
				}
				String to = "", message = "", amount = "";
				Tracking tracking = null;
				for(int i = 1; i < args.length; i++) {
					if(args[i].startsWith("to:")) {
						tracking = Tracking.TO;
						to += args[i].replace("to:", "");

					}
					else if(args[i].startsWith("message:")) {
						tracking = Tracking.MESSAGE;
						message += args[i].replace("message:", "");
					}
					else if(args[i].startsWith("amount:")) {
						tracking = Tracking.AMOUNT;
						amount += args[i].replace("amount:", "");
					}
					else {
						if(tracking == null) continue;
						switch(tracking) {
							case TO:
								to += " " + args[i];
								break;
							case MESSAGE:
								message += " "+ args[i];
								break;
							case AMOUNT:
								amount += " " + args[i];
								break;
							default:
								break;
						}
					}
				}
				to = to.trim();
				message = message.trim();
				amount = amount.trim();
				if(to.isEmpty()) {
					sender.sendMessage(ChatColor.RED + "[Mythica] Could not determine who to send mail to. Type /mail for help.");
					return true;
				}
				String completedName = Utils.completeName(to);
				if(completedName == null) {
					sender.sendMessage(ChatColor.RED + "[Mythica] \"" + to + "\" is not a recognized player name.");
					return true;
				}
				if(completedName.equals(sender.getName())) {
					sender.sendMessage(ChatColor.RED + "[Mythica] You can't send mail to yourself. Sorry!");
					return true;
				}

				Mailbox senderMailbox = MailboxManager.getInstance().getPlayerMailbox(sender.getName());
				Mail mail = null;
				if(mailType == MailType.LETTER) {
					if(message.isEmpty()) {
						sender.sendMessage(ChatColor.RED + "[Mythica] Your message is empty! Please try again.");
						return true;
					}
					mail = new Mail(completedName, sender.getName(), message, Time.getTime(), MailType.LETTER, MailStatus.UNREAD);
					sender.sendMessage(ChatColor.YELLOW + "[Mythica]" + ChatColor.DARK_AQUA + " You have mailed a " + ChatColor.AQUA + "letter" + ChatColor.DARK_AQUA + " to " + ChatColor.AQUA + completedName);
				}
				else if(mailType == MailType.PACKAGE) {
					List<ItemStack> dropbox = senderMailbox.getDropbox();
					if(dropbox.isEmpty()) {
						sender.sendMessage(ChatColor.RED + "[Mythica] Your mail drop box is empty! Open up your mailbox and put items in the drop box, then run this command again.");
						return true;
					}
					mail = new PackageObj(completedName, sender.getName(), message, Time.getTime(), MailStatus.UNREAD, dropbox);
					senderMailbox.clearDropbox();
					sender.sendMessage(ChatColor.YELLOW + "[Mythica]" + ChatColor.DARK_AQUA + " You have mailed a " + ChatColor.AQUA + "package" + ChatColor.DARK_AQUA + " to " + ChatColor.AQUA + completedName);
				}
				else if(mailType == MailType.PAYMENT) {
					double money = 0;
					try {
						money = Double.parseDouble(amount);
					} catch (Exception e) {
						sender.sendMessage(ChatColor.RED + "[Mythica] \"" + amount + "\" is not a recongized amount of money! Please try again.");
						return true;
					}
					if(money <= 0) {
						sender.sendMessage(ChatColor.RED + "[Mythica] You can't send zero or negative money! Please try again.");
						return true;
					}

					double balance = Mythsentials.economy.getBalance(sender.getName());

					if(balance >= money) {
						Mythsentials.economy.withdrawPlayer(sender.getName(), money);
						mail = new Payment(completedName, sender.getName(), message, Time.getTime(), MailStatus.UNREAD, money);
						sender.sendMessage(ChatColor.YELLOW + "[Mythica]" + ChatColor.DARK_AQUA + " You have mailed a " + ChatColor.AQUA + "payment" + ChatColor.DARK_AQUA + " to " + ChatColor.AQUA + completedName);
					} else {
						sender.sendMessage(ChatColor.RED + "You do not have enough in your bank to send this amount!");
						return true;
					}
				}
				else if(mailType == MailType.EXPERIENCE) {
					int xp = 0;
					try {
						xp = Integer.parseInt(amount);
					} catch (Exception e) {
						sender.sendMessage(ChatColor.RED + "[Mythica] \"" + xp + "\" is not a recongized amount of XP! Please try again.");
						return true;
					}

					if(cmdPlayer.getTotalExperience() < xp) {
						sender.sendMessage(ChatColor.RED + "[Mythica] You don't have that amount of XP to send!");
						return true;
					}

					mail = new Experience(completedName, sender.getName(), message, Time.getTime(), MailStatus.UNREAD, xp);
					long totalXp = SetExpFix.getTotalExperience(cmdPlayer) - xp;
					if (totalXp < 0L)
					{
						totalXp = 0L;
					}
					SetExpFix.setTotalExperience(cmdPlayer, (int)totalXp);
					sender.sendMessage(ChatColor.YELLOW + "[Mythica]" + ChatColor.DARK_AQUA + " You have mailed " + ChatColor.AQUA + "experience" + ChatColor.DARK_AQUA + " to " + ChatColor.AQUA + completedName);
				}
				if(mail != null) {
					senderMailbox.sendMail(completedName, mail);
				}
			}
		}
		return true;
	}

	private boolean locationIsNearMailbox(Location location, int radius) {
		for (int x = -(radius); x <= radius; x++)
		{
			for (int y = -(radius); y <= radius; y++)
			{
				for (int z = -(radius); z <= radius; z++)
				{
					Location loc = location.getBlock().getRelative(x, y, z).getLocation();

					if(loc.getBlock().getType() == Material.CHEST) {

						if(MailboxManager.getInstance().locationHasMailbox(loc)) return true;
					}
				}

			}
		}
		return false;
	}
}
