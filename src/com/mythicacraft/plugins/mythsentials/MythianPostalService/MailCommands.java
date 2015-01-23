package com.mythicacraft.plugins.mythsentials.MythianPostalService;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.mythicacraft.plugins.mythsentials.Mythsentials;
import com.mythicacraft.plugins.mythsentials.MythianPostalService.mailtypes.Experience;
import com.mythicacraft.plugins.mythsentials.MythianPostalService.mailtypes.Mail;
import com.mythicacraft.plugins.mythsentials.MythianPostalService.mailtypes.Mail.MailStatus;
import com.mythicacraft.plugins.mythsentials.MythianPostalService.mailtypes.Mail.MailType;
import com.mythicacraft.plugins.mythsentials.MythianPostalService.mailtypes.PackageObj;
import com.mythicacraft.plugins.mythsentials.MythianPostalService.mailtypes.Payment;
import com.mythicacraft.plugins.mythsentials.Utilities.Time;
import com.mythicacraft.plugins.mythsentials.Utilities.Utils;


public class MailCommands implements CommandExecutor {

	enum Tracking {
		TO, MESSAGE, AMOUNT
	}

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {

		if(commandLabel.equalsIgnoreCase("mail")) {
			if(args.length == 0) {
				//send fancy clickable menu
			}
			else if(args.length == 1) {
				//too short
			}
			else {
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
								to += args[i] + " ";
								break;
							case MESSAGE:
								message += args[i] + " ";
								break;
							case AMOUNT:
								amount += args[i] + " ";
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

				if(mailType == MailType.LETTER) {
					if(message.isEmpty()) {
						sender.sendMessage(ChatColor.RED + "[Mythica] Your message is empty! Please try again.");
						return true;
					}
					Mail mail = new Mail(completedName, sender.getName(), message, Time.getTime(), MailType.LETTER, MailStatus.UNREAD);
					senderMailbox.sendMail(completedName, mail);
					sender.sendMessage(ChatColor.YELLOW + "[Mythica]" + ChatColor.DARK_AQUA + " You have sent a " + ChatColor.AQUA + "letter" + ChatColor.DARK_AQUA + " to " + ChatColor.AQUA + completedName);
				}
				else if(mailType == MailType.PACKAGE) {
					List<ItemStack> dropbox = senderMailbox.getDropbox();
					if(dropbox.isEmpty()) {
						sender.sendMessage(ChatColor.RED + "[Mythica] Your mail drop box is empty! Open up your mailbox and put items in the drop box, then run this command again.");
						return true;
					}
					PackageObj packageItem = new PackageObj(completedName, sender.getName(), message, Time.getTime(), MailStatus.UNREAD, dropbox);
					senderMailbox.sendMail(completedName, packageItem);
					senderMailbox.clearDropbox();
					sender.sendMessage(ChatColor.YELLOW + "[Mythica]" + ChatColor.DARK_AQUA + " You have sent a " + ChatColor.AQUA + "package" + ChatColor.DARK_AQUA + " to " + ChatColor.AQUA + completedName);
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
						Payment payment = new Payment(completedName, sender.getName(), message, Time.getTime(), MailStatus.UNREAD, money);
						senderMailbox.sendMail(completedName, payment);
						sender.sendMessage(ChatColor.YELLOW + "[Mythica]" + ChatColor.DARK_AQUA + " You have sent a " + ChatColor.AQUA + "payment" + ChatColor.DARK_AQUA + " to " + ChatColor.AQUA + completedName);
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
					Player player = (Player) sender;
					if(player.getExp() < xp) {
						sender.sendMessage(ChatColor.RED + "[Mythica] You don't have that amount of XP to send!");
						return true;
					}
					Experience experience = new Experience(completedName, sender.getName(), message, Time.getTime(), MailStatus.UNREAD, xp);
					senderMailbox.sendMail(completedName, experience);
					player.setExp(player.getExp()-xp);
					sender.sendMessage(ChatColor.YELLOW + "[Mythica]" + ChatColor.DARK_AQUA + " You have sent " + ChatColor.AQUA + "experience" + ChatColor.DARK_AQUA + " to " + ChatColor.AQUA + completedName);
				}
			}
		}
		return true;
	}
}
