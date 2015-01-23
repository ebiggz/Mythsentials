package com.mythicacraft.plugins.mythsentials.MythianPostalService.mailtypes;

import java.util.List;

import org.bukkit.inventory.ItemStack;


public class PackageObj extends Mail {

	private List<ItemStack> items;

	public PackageObj(String to, String from, String message, String timeStamp, MailStatus status, List<ItemStack> items) {
		super(to, from, message, timeStamp, MailType.PACKAGE, status);
		this.items = items;
	}

	public List<ItemStack> getItems() {
		return items;
	}
}