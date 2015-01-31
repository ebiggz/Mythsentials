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
import org.bukkit.inventory.meta.BookMeta;
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
import com.mythicacraft.plugins.mythsentials.Utilities.ParticleEffect;
import com.mythicacraft.plugins.mythsentials.Utilities.SetExpFix;
import com.mythicacraft.plugins.mythsentials.Utilities.Time;
import com.mythicacraft.plugins.mythsentials.Utilities.Utils;


public class MailCommands implements CommandExecutor {

	private String[] commandData = {
			"&7&o(Hover over a &a&ocommand &7&ofor info, click to run it)",
			"TELLRAW run>>/mail help>>/mail help>>See help info on MPS.",
			"TELLRAW run>>/mail check>>/mail check>>Check for unread mail.",
			"TELLRAW run>>/mail compose>>/mail compose>>Compose mail to send to other players.",
			"TELLRAW run>>/mailbox set>>/mailbox set>>Register a chest as a mailbox.",
			"TELLRAW run>>/mailbox clear>>/mailbox clear>>Unregister a chest as a mailbox.",
			"TELLRAW suggest>>/mailbox clearall>>/mailbox clearall>>Unregister all your mailboxes."
	};

	private String[] helpData = {
			"&b&lWhat is the Mythian Postal Service?",
			" MPS is a way to safely send letters, items, money, or experience to players!",
			"&b&lHow do I use the MPS?",
			" You can send and receive mail at chests that are registered as mailboxes.",
			"&b&lAre there any commands for MPS?",
			" Yes! Type \"/mail\" to see them all!"
	};

	enum Tracking {
		TO, MESSAGE, AMOUNT
	}

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {

		final Player cmdPlayer = (Player) sender;

		if(commandLabel.equalsIgnoreCase("mailbox")) {
			if(args.length == 0) {
				FancyMenu.showClickableCommandList(sender, commandLabel, "Mythian Postal Service", commandData, 1);
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
				else if(args[0].equalsIgnoreCase("find")) {
					Location[] mailboxes = MailboxManager.getInstance().getMailboxLocations();
					boolean found = false;
					for(Location mailbox : mailboxes) {
						if(cmdPlayer.getLocation().distance(mailbox) < 20) {
							ParticleEffect effect = new ParticleEffect(ParticleEffect.ParticleType.VILLAGER_HAPPY, 0, 100, 0, 3, 0);
							mailbox.setX(mailbox.getX() + 0.5);
							mailbox.setZ(mailbox.getZ() + 0.5);
							mailbox.setY(mailbox.getY() + 4);
							effect.sendToLocation(mailbox, cmdPlayer);
							found = true;
						}
					}
					if(found) {
						cmdPlayer.sendMessage(ChatColor.YELLOW + "[MPS]" + ChatColor.DARK_AQUA + " Marking nearby mailboxes!");
					} else {
						cmdPlayer.sendMessage(ChatColor.RED + "[MPS] There are no nearby mailboxes!");
					}
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

		else if(commandLabel.equalsIgnoreCase("mail") || commandLabel.equalsIgnoreCase("mailadmin")) {

			if(commandLabel.equalsIgnoreCase("mailadmin") && !sender.isOp()){
				return true;
			}

			if(args.length == 0) {
				FancyMenu.showClickableCommandList(sender, commandLabel, "Mythian Postal Service", commandData, 1);
			}
			else if(args.length == 1) {
				if(args[0].equalsIgnoreCase("compose")) {

					boolean nearMailbox = MailboxManager.getInstance().mailboxIsNearby(cmdPlayer.getLocation(), 6);
					if(!nearMailbox) {
						sender.sendMessage(ChatColor.RED + "[MPS] You must be near a mailbox to send mail!");
						return true;
					}
					String command = "tellraw {player} {\"text\":\"\",\"extra\":[{\"text\":\"[MPS] Compose mail (Click one): \",\"color\":\"yellow\",\"clickEvent\":{\"action\":\"suggest_command\",\"value\":\"\"}},{\"text\":\"Letter, \",\"color\":\"aqua\",\"clickEvent\":{\"action\":\"suggest_command\",\"value\":\"/mail letter to: message:\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"\",\"extra\":[{\"text\":\"Mail a text-only letter!\",\"color\":\"gold\"}]}}},{\"text\":\"Package, \",\"color\":\"aqua\",\"clickEvent\":{\"action\":\"suggest_command\",\"value\":\"/mail package to: message:\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"\",\"extra\":[{\"text\":\"Mail a package with the items in your drop box!\",\"color\":\"gold\"}]}}},{\"text\":\"Payment, \",\"color\":\"aqua\",\"clickEvent\":{\"action\":\"suggest_command\",\"value\":\"/mail payment to: amount: message:\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"\",\"extra\":[{\"text\":\"Mail a money payment!\",\"color\":\"gold\"}]}}},{\"text\":\"Experience\",\"color\":\"aqua\",\"clickEvent\":{\"action\":\"suggest_command\",\"value\":\"/mail xp to: amount: message:\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"\",\"extra\":[{\"text\":\"Mail xp points (not levels) to a player!!\",\"color\":\"gold\"}]}}}]}";
					Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), command.replace("{player}", sender.getName()));
				}
				else if(args[0].equalsIgnoreCase("help")) {
					FancyMenu.showClickableCommandList(sender, commandLabel, "Mythian Postal Service Help", helpData, 1);
				}
				else if(args[0].equalsIgnoreCase("check")) {
					Mailbox mb = MailboxManager.getInstance().getPlayerMailbox(sender.getName());
					int unread = mb.getUnreadMailCount();
					if(unread == 0) {
						sender.sendMessage(ChatColor.YELLOW + "[MPS] " + ChatColor.DARK_AQUA + "You don't have any unread mail.");
					} else {
						sender.sendMessage(ChatColor.YELLOW + "[MPS] " + ChatColor.DARK_AQUA + "You have " + ChatColor.AQUA + unread + ChatColor.DARK_AQUA + " unread mail message(s)! Visit the a mailbox to read them.");
					}
				}
			}

			else {
				//check if mailbox is near by
				boolean nearMailbox = MailboxManager.getInstance().mailboxIsNearby(cmdPlayer.getLocation(), 6);
				if(!nearMailbox) {
					sender.sendMessage(ChatColor.RED + "[MPS] You must be near a mailbox to send mail!");
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
					sender.sendMessage(ChatColor.RED + "[MPS] \"" + mailTypeStr + "\" is not a recognized mail type. Type /mail for help.");
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
					sender.sendMessage(ChatColor.RED + "[MPS] Could not determine who to send mail to. Type /mail for help.");
					return true;
				}
				String completedName = Utils.completeName(to);
				if(completedName == null) {
					sender.sendMessage(ChatColor.RED + "[MPS] \"" + to + "\" is not a recognized player name.");
					return true;
				}
				if(completedName.equals(sender.getName())) {
					sender.sendMessage(ChatColor.RED + "[MPS] You can't send mail to yourself. Sorry!");
					return true;
				}

				if(message.isEmpty()) {
					if(cmdPlayer.getItemInHand().getType() == Material.BOOK_AND_QUILL) {
						BookMeta bm = (BookMeta) cmdPlayer.getItemInHand().getItemMeta();
						if(bm.hasPages()) {
							if(bm.getPageCount() > 1) {
								sender.sendMessage(ChatColor.RED + "[MPS] Books can only contain 1 page of text to use them to send a message!");
								return true;
							}
							message = bm.getPage(1).trim();
						}
					}
				}

				Mailbox senderMailbox = MailboxManager.getInstance().getPlayerMailbox(sender.getName());
				Mail mail = null;
				if(mailType == MailType.LETTER) {
					if(message.isEmpty()) {
						sender.sendMessage(ChatColor.RED + "[MPS] Your message is empty! Please try again.");
						return true;
					}
					mail = new Mail(completedName, sender.getName(), message, Time.getTime(), MailType.LETTER, MailStatus.UNREAD);
					sender.sendMessage(ChatColor.YELLOW + "[MPS]" + ChatColor.DARK_AQUA + " You have mailed a " + ChatColor.AQUA + "letter" + ChatColor.DARK_AQUA + " to " + ChatColor.AQUA + completedName);
				}
				else if(mailType == MailType.PACKAGE) {
					List<ItemStack> dropbox = senderMailbox.getDropbox();
					if(dropbox.isEmpty()) {
						sender.sendMessage(ChatColor.RED + "[MPS] Your mail drop box is empty! Open up your mailbox and put items in the drop box, then run this command again.");
						return true;
					}
					mail = new PackageObj(completedName, sender.getName(), message, Time.getTime(), MailStatus.UNREAD, dropbox);
					senderMailbox.clearDropbox();
					sender.sendMessage(ChatColor.YELLOW + "[MPS]" + ChatColor.DARK_AQUA + " You have mailed a " + ChatColor.AQUA + "package" + ChatColor.DARK_AQUA + " to " + ChatColor.AQUA + completedName);
				}
				else if(mailType == MailType.PAYMENT) {
					double money = 0;
					try {
						money = Double.parseDouble(amount);
					} catch (Exception e) {
						sender.sendMessage(ChatColor.RED + "[MPS] \"" + amount + "\" is not a recongized amount of money! Please try again.");
						return true;
					}
					if(money <= 0) {
						sender.sendMessage(ChatColor.RED + "[MPS] You can't send zero or negative money! Please try again.");
						return true;
					}

					double balance = Mythsentials.economy.getBalance(sender.getName());

					if(balance >= money) {
						Mythsentials.economy.withdrawPlayer(sender.getName(), money);
						mail = new Payment(completedName, sender.getName(), message, Time.getTime(), MailStatus.UNREAD, money);
						sender.sendMessage(ChatColor.YELLOW + "[MPS]" + ChatColor.DARK_AQUA + " You have mailed a " + ChatColor.AQUA + "payment" + ChatColor.DARK_AQUA + " to " + ChatColor.AQUA + completedName);
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
						sender.sendMessage(ChatColor.RED + "[MPS] \"" + xp + "\" is not a recongized amount of XP! Please try again.");
						return true;
					}

					if(cmdPlayer.getTotalExperience() < xp) {
						sender.sendMessage(ChatColor.RED + "[MPS] You don't have that amount of XP to send!");
						return true;
					}

					mail = new Experience(completedName, sender.getName(), message, Time.getTime(), MailStatus.UNREAD, xp);
					long totalXp = SetExpFix.getTotalExperience(cmdPlayer) - xp;
					if (totalXp < 0L)
					{
						totalXp = 0L;
					}
					SetExpFix.setTotalExperience(cmdPlayer, (int)totalXp);
					sender.sendMessage(ChatColor.YELLOW + "[MPS]" + ChatColor.DARK_AQUA + " You have mailed " + ChatColor.AQUA + "experience" + ChatColor.DARK_AQUA + " to " + ChatColor.AQUA + completedName);
				}
				if(mail != null) {
					senderMailbox.sendMail(completedName, mail);
				}
			}
		}
		return true;
	}
}
