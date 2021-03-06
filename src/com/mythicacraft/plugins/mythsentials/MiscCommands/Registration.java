package com.mythicacraft.plugins.mythsentials.MiscCommands;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mythicacraft.plugins.mythsentials.Mythsentials;

/*
 *  Written by Rockjolt
 */

public class Registration implements CommandExecutor {

	public Mythsentials plugin;

	private String check;

	public Registration(Mythsentials plugin) {
		this.plugin = plugin;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		final Player player = (Player) sender;

		if(commandLabel.equalsIgnoreCase("register") || commandLabel.equalsIgnoreCase("reg")){
			if(!sender.hasPermission("mythica.registered")){

				if(args.length == 0 || args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("?")){
					sender.sendMessage(ChatColor.GREEN + "-----" + ChatColor.GOLD + "Register Help" + ChatColor.GREEN + "-----");
					sender.sendMessage(ChatColor.GOLD + "The /register command conveniently adds you to our forums from in-game! Our forums tie into our permissions system, so you can't get build rights without registering!");
					sender.sendMessage(ChatColor.GOLD + "To get started, type '" + ChatColor.AQUA + "/register [email] [password] " + ChatColor.GOLD + "'.");
					return true;
				}

				if(args.length != 2){ //If player did not enter /register [email] [password] command format
					sender.sendMessage(ChatColor.RED + "Please enter a valid email and password by typing '/register [email] [password]'");
					return true;
				}

				if(!validate(args[0])){ //Checks validity of email address
					sender.sendMessage(ChatColor.RED + "The email you've entered appears to be invalid. If you believe this is an error, please use '/helpme' to contact a mod or you may manually register at our website: www.mythicacraft.com");
					return true;
				}

				String [] regInfo = {args[0], args[1]};

				confirmInfo(regInfo, sender);

			} //End perm check hasPermission
			else{
				sender.sendMessage(ChatColor.GREEN + "You are already registered!");
			}
		} //End commandLabel = reg or register

		if(commandLabel.equalsIgnoreCase("confirm")){ //If player confirms information, execute to PHP script
			if(plugin.emailHash.containsKey(player)){
				try { //Checks database for duplicate email/username
					if(!duplicateCheck(sender.getName(), plugin.emailHash.get(player))){
						if(check.equalsIgnoreCase("email")){
							sender.sendMessage(ChatColor.RED + "Your email already exists on the forum! Please try again with another email or log in to the forums to modify your Minecraft username in your profile.");
						}
						else{
							sender.sendMessage(ChatColor.RED + "Your username is already registered on the forum! If you believe this is an error - please contact a mod using '/helpme'.");
						}
						Bukkit.getServer().getScheduler().cancelTask(Integer.parseInt(plugin.taskIDHash.get(player))); //Cancels timeout task
						return true;
					}
				} catch (SQLException e1) {
					e1.printStackTrace();
				}

				//Connect to external PHP script to add user to forum
				new ForumConnector(sender.getName(), plugin.emailHash.get(player), plugin.passHash.get(player)).run();

				sender.sendMessage(ChatColor.GREEN + "You have successfully registered! You will be automatically promoted within a few seconds.");
				Bukkit.getServer().getScheduler().cancelTask(Integer.parseInt(plugin.taskIDHash.get(player))); //Cancels timeout task

				Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "pex promote " + player.getName());

				//Clears hash data to remove access to confirm and cancel commands
				plugin.emailHash.remove(player);
				plugin.passHash.remove(player);
				plugin.taskIDHash.remove(player);

			}
			else{
				sender.sendMessage(ChatColor.RED + "You do not have permissions to use this command");
			}
		}

		if(commandLabel.equalsIgnoreCase("cancel")){ //If player cancels process, restart.
			if(plugin.emailHash.containsKey(player)){
				sender.sendMessage(ChatColor.GOLD + "You have cancelled your registration.");

				//Cancels timeout and removes playerdata from hash
				Bukkit.getServer().getScheduler().cancelTask(Integer.parseInt(plugin.taskIDHash.get(player)));
				plugin.emailHash.remove(player);
				plugin.passHash.remove(player);
				plugin.taskIDHash.remove(player);
				return true;
			}
			else{
				sender.sendMessage(ChatColor.RED + "You do not have permissions to use this command");
			}
		}

		return true;
	}//End onCommand

	public static boolean validate(String emailAdress) { //Does basic check for email structure
		Pattern checkRegex = Pattern.compile("[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})");
		Matcher regexMatcher = checkRegex.matcher(emailAdress);
		if(regexMatcher.find()) return true;
		return false;
	}

	public boolean confirmInfo(String[] info, final CommandSender sender){ //Sends confirmation message and initiates timeout
		sender.sendMessage(ChatColor.GREEN + "You have entered " + ChatColor.GOLD + info[0] + ChatColor.GREEN + " and " + ChatColor.GOLD + info[1] + ChatColor.GREEN + ". Is this correct? Type '" + ChatColor.AQUA + "/confirm" + ChatColor.GREEN +"' to accept or '" + ChatColor.AQUA + "/cancel" + ChatColor.GREEN + "' to try again. This will time out in 20 seconds.");
		final Player player = (Player) sender;
		plugin.emailHash.put(player, info[0]);
		plugin.passHash.put(player, info[1]);
		int taskID = Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			public void run() {
				sender.sendMessage(ChatColor.RED + "You have not confirmed your registration. Please try again.");
				plugin.emailHash.remove(player);
				plugin.passHash.remove(player);
				plugin.taskIDHash.remove(player);
				return;
			} //end void run()
		} //end bukkit start scheduler
		, 400L);
		plugin.taskIDHash.put(player, Integer.toString(taskID));
		return true;
	}

	public boolean duplicateCheck(String player, String email) throws SQLException { //Pulls player information from database to check for duplicates
		String sqlURL = "jdbc:mysql://box777.bluehost.com:3306/mythicac_forums";
		Connection conn = DriverManager.getConnection(sqlURL, "mythicac_admin2", "WArrior11");
		PreparedStatement emailStatement = conn.prepareStatement("SELECT * FROM phpbb_users WHERE user_email = '" + email + "'");
		PreparedStatement usernameStatement = conn.prepareStatement("SELECT * FROM phpbb_profile_fields_data WHERE pf_minecraft_user = '" + player + "'"); //Put your query in the quotes
		ResultSet emailSelect = emailStatement.executeQuery(); //Executes the query
		ResultSet userSelect = usernameStatement.executeQuery();

		if(!emailSelect.next()){
			if(!userSelect.next()){
				emailSelect.close();
				userSelect.close();
				conn.close();
				return true;
			}
			emailSelect.close();
			userSelect.close();
			conn.close();
			check = "user";
			return false;
		}
		emailSelect.close();
		userSelect.close();
		conn.close();
		check = "email";
		return false;
	}

}//End Class