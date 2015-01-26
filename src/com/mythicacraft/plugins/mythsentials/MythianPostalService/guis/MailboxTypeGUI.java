package com.mythicacraft.plugins.mythsentials.MythianPostalService.guis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.mythicacraft.plugins.mythsentials.Mythsentials;
import com.mythicacraft.plugins.mythsentials.GUIAPI.GUI;
import com.mythicacraft.plugins.mythsentials.GUIAPI.GUIManager;
import com.mythicacraft.plugins.mythsentials.GUIAPI.GUIUtils;
import com.mythicacraft.plugins.mythsentials.MythianPostalService.Mailbox;
import com.mythicacraft.plugins.mythsentials.MythianPostalService.MailboxManager;
import com.mythicacraft.plugins.mythsentials.MythianPostalService.MailboxManager.MailboxType;
import com.mythicacraft.plugins.mythsentials.MythianPostalService.mailtypes.Experience;
import com.mythicacraft.plugins.mythsentials.MythianPostalService.mailtypes.Mail;
import com.mythicacraft.plugins.mythsentials.MythianPostalService.mailtypes.Mail.MailStatus;
import com.mythicacraft.plugins.mythsentials.MythianPostalService.mailtypes.Mail.MailType;
import com.mythicacraft.plugins.mythsentials.MythianPostalService.mailtypes.PackageObj;
import com.mythicacraft.plugins.mythsentials.MythianPostalService.mailtypes.Payment;
import com.mythicacraft.plugins.mythsentials.Utilities.SetExpFix;
import com.mythicacraft.plugins.mythsentials.Utilities.Utils;


public class MailboxTypeGUI implements GUI {

	private MailboxType boxType;
	private String playerName;
	private int pageNumber;
	private int totalPages;
	private Mailbox mailbox;

	public MailboxTypeGUI(String playerName, MailboxType type, int pageNumber) {
		this.boxType = type;
		this.playerName = playerName;
		this.pageNumber = pageNumber;
		this.mailbox = MailboxManager.getInstance().getPlayerMailbox(playerName);
	}

	public MailboxType getType() {
		return boxType;
	}

	@Override
	public Inventory createInventory(Player player) {
		Inventory inventory = Bukkit.createInventory(null, 9*5, player.getName() + "'s " + Utils.capitalize(boxType.toString().toLowerCase(), null));
		List<Mail> mails = mailbox.getBoxFromType(boxType);
		int boxSize = mails.size();
		totalPages = (int) Math.ceil(boxSize/27.0);
		if(totalPages == 0) {
			totalPages = 1;
		}
		if(pageNumber > totalPages) {
			pageNumber = totalPages;
		}
		int mailIndex = 0 + (27*(pageNumber-1));

		while(mailIndex < 27 * pageNumber && mailIndex < boxSize) {
			inventory.setItem(mailIndex,createMailButton(mails.get(mailIndex)));
			mailIndex++;
		}

		ItemStack seperator = GUIUtils.createButton(Material.STONE_BUTTON, ChatColor.STRIKETHROUGH + "---", null);
		for(int i = 27; i < 36; i++) {
			inventory.setItem(i, seperator);
		}

		ItemStack mainMenu = GUIUtils.createButton(
				Material.BOOK_AND_QUILL,
				ChatColor.YELLOW +""+ ChatColor.BOLD + "Main Menu",
				Arrays.asList(
						ChatColor.RED + "Left-Click to " + ChatColor.BOLD + "Return"));
		inventory.setItem(40, mainMenu);
		totalPages = (int) Math.ceil(boxSize/27.0);
		if(totalPages > 1) {
			if(pageNumber + 1 <= totalPages) {
				ItemStack next = GUIUtils.createButton(
						Material.IRON_PLATE,
						ChatColor.GOLD + "Next " + ChatColor.STRIKETHROUGH + "->",
						Arrays.asList(
								ChatColor.RED + "Click for" + ChatColor.BOLD + "Next Page"));
				inventory.setItem(41, next);
				//next button
			}
			if(pageNumber - 1 >= 1) {
				ItemStack previous = GUIUtils.createButton(
						Material.IRON_PLATE,
						ChatColor.GOLD +""+ ChatColor.STRIKETHROUGH + "<- " + ChatColor.GOLD + "Previous",
						Arrays.asList(
								ChatColor.RED + "Click for" + ChatColor.BOLD + "Previous Page"));
				inventory.setItem(39, previous);
				//previous button
			}
		}
		return inventory;
	}

	@Override
	public void onInventoryClick(Player whoClicked, InventoryClickEvent clickedEvent) {

		ItemStack clickedItem = clickedEvent.getCurrentItem();
		int clickedSlot = clickedEvent.getSlot();
		switch(clickedSlot) {
			case 39:
				if(clickedItem != null && clickedItem.getType() != Material.AIR) {
					GUIManager.getInstance().showGUI(new MailboxTypeGUI(playerName, boxType, pageNumber-1), whoClicked);
				}
				break;
			case 40:
				if(clickedItem != null && clickedItem.getType() != Material.AIR) {
					GUIManager.getInstance().showGUI(new MailboxGUI(), whoClicked);
				}
				break;
			case 41:
				if(clickedItem != null && clickedItem.getType() != Material.AIR) {
					GUIManager.getInstance().showGUI(new MailboxTypeGUI(playerName, boxType, pageNumber+1), whoClicked);
				}
				break;
			default:
				if(clickedSlot <= 27 && clickedItem != null && clickedItem.getType() != Material.AIR) {
					int mailIndex = clickedSlot + (27*(pageNumber-1));
					Mail mail = mailbox.getBoxFromType(boxType).get(mailIndex);
					if(clickedEvent.getClick() == ClickType.LEFT) {
						if(boxType == MailboxType.INBOX) {

						}
					}
					else if(clickedEvent.getClick() == ClickType.RIGHT) {
						if(boxType == MailboxType.INBOX) {
							if(mail.getType() != MailType.LETTER && mail.getStatus() != MailStatus.CLAIMED) {
								if(mail.getType() == MailType.EXPERIENCE) {
									Experience exp = (Experience) mail;

									long amount = exp.getExperience() + SetExpFix.getTotalExperience(whoClicked);
									if (amount > 2147483647L)
									{
										amount = 2147483647L;
									}
									if (amount < 0L)
									{
										amount = 0L;
									}
									SetExpFix.setTotalExperience(whoClicked, (int)amount);

									exp.setStatus(MailStatus.CLAIMED);
									mailbox.updateMailItem(exp, mailIndex, boxType);
									whoClicked.closeInventory();
									whoClicked.sendMessage(ChatColor.YELLOW +"[Mythica] " + ChatColor.DARK_AQUA + "You have successfuly claimed the experience!");
								}
								else if(mail.getType() == MailType.PAYMENT) {
									Payment payment = (Payment) mail;
									Mythsentials.economy.depositPlayer(whoClicked.getName(), payment.getPayment());
									payment.setStatus(MailStatus.CLAIMED);
									mailbox.updateMailItem(payment, mailIndex, boxType);
									whoClicked.closeInventory();
									whoClicked.sendMessage(ChatColor.YELLOW +"[Mythica] " + ChatColor.DARK_AQUA + "You have successfuly claimed the payment!");
								}
								else if(mail.getType() == MailType.PACKAGE) {
									GUIManager.getInstance().showGUI(new PackageGUI((PackageObj) mail, mailIndex, boxType, pageNumber), whoClicked);
								}
							}
						}
					}
					else if(clickedEvent.getClick() == ClickType.SHIFT_RIGHT) {
						mailbox.deleteMailItem(mailIndex, boxType);
						clickedEvent.getInventory().setContents(createInventory(whoClicked).getContents());
					}
				}
		}
	}

	@Override
	public void onInventoryClose(Player whoClosed, InventoryCloseEvent closeEvent) {}

	private ItemStack createMailButton(Mail mail) {

		ItemStack button = new ItemStack(Material.AIR);
		List<String> lore = new ArrayList<String>();

		switch(mail.getType()) {
			case LETTER:
				button = new ItemStack(Material.PAPER);
				break;
			case EXPERIENCE:
				button = new ItemStack(Material.EXP_BOTTLE);
				Experience exp = (Experience) mail;
				lore.add(ChatColor.WHITE + "" + exp.getExperience() + " XP Points");

				break;
			case PACKAGE:
				PackageObj po = (PackageObj) mail;
				lore.add(ChatColor.WHITE + "" + po.getItems().size() + " Required Slots");
				button = new ItemStack(Material.CHEST);
				break;
			case PAYMENT:
				Payment payment = (Payment) mail;
				lore.add(ChatColor.WHITE + "$" + payment.getPayment());
				button = new ItemStack(Material.GOLD_INGOT);
				break;
		}

		if(mail.getStatus() == MailStatus.CLAIMED) {
			lore.add(ChatColor.GRAY + "(Already claimed)");
		}

		ItemMeta im = button.getItemMeta();
		im.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + ChatColor.UNDERLINE + Utils.capitalize(mail.getType().toString().toLowerCase(), null));
		if(!mail.getMessage().trim().isEmpty()) {
			String[] wrappedMessage = Utils.wrap("\"" +mail.getMessage() +"\"", 30, "\n", true).split("\n");
			for(String line : wrappedMessage) {
				lore.add(ChatColor.YELLOW + line);
			}
		}

		lore.add(boxType.equals(MailboxType.INBOX) ? ChatColor.GRAY +""+ChatColor.ITALIC+ "from " + mail.getFrom() : ChatColor.GRAY +""+ChatColor.ITALIC+ "to " + mail.getTo());
		lore.add(ChatColor.GRAY + mail.getTimeStamp());
		if(boxType.equals(MailboxType.INBOX)) {
			lore.add(ChatColor.RED + "Left-Click to " + ChatColor.BOLD + "Respond");
			if(mail.getType() != MailType.LETTER) {
				if(mail.getStatus() != MailStatus.CLAIMED) {
					lore.add(ChatColor.DARK_RED + "Right-Click to " + ChatColor.BOLD + "Claim");
				}
			}
		}
		lore.add(ChatColor.RED + "Shift+Right-Click to " + ChatColor.BOLD + "Delete");

		im.setLore(lore);
		button.setItemMeta(im);
		return button;

	}
}
