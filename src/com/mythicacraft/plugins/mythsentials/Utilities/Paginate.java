package com.mythicacraft.plugins.mythsentials.Utilities;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.util.ChatPaginator;
import org.bukkit.util.ChatPaginator.ChatPage;

public class Paginate {

	private String paginateString;
	private int totalPages;
	private String header = "Pages";
	private String footer = "command";

	public Paginate() {}

	public Paginate(String paginateString) {
		this.paginateString = paginateString;
	}

	public Paginate(String paginateString, String header) {
		this.paginateString = paginateString;
		this.header = header;
	}

	public Paginate(String paginateString, String header, String footer) {
		this.paginateString = paginateString;
		this.header = header;
		this.footer = footer;
	}

	public void sendPage(int pageNumber, CommandSender sender) {
		ChatPage message = ChatPaginator.paginate(paginateString, pageNumber, 53, 8); //paginate string, pulling the page number the player provided. It creates the page with the lines 53 characters long and 8 lines per page
		String[] pages = message.getLines(); //puts the lines from the page into a string array

		sender.sendMessage(ChatColor.GREEN + "-----" + ChatColor.YELLOW + header + ChatColor.GREEN + " | " + ChatColor.YELLOW + "Page " + pageNumber + "/" + pageTotal() + ChatColor.GREEN + "-----"); //header of page with current and total pages
		sender.sendMessage(pages); //send page string array

		if(pageNumber < pageTotal()) { //if page number is less than total, include this footer
			int nextPage = pageNumber + 1;
			footer = footer.replaceAll("NEXTPAGE", Integer.toString(nextPage));
			sender.sendMessage(ChatColor.GOLD + "Type \"/" + footer + "\" for next page.");
		}
	}

	public void setPaginateString(String paginateString) {
		this.paginateString = paginateString;
	}

	public int pageTotal() { //returns an Int of total pages
		this.totalPages = ChatPaginator.paginate(paginateString, 1, 53, 8).getTotalPages();
		return totalPages;
	}

	public void setHeader(String header) {
		this.header = header;
	}

	public void setFooter(String footer) {
		this.footer = footer;
	}
}

