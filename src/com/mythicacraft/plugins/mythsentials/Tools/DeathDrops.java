package com.mythicacraft.plugins.mythsentials.Tools;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;


public class DeathDrops implements Comparable<Object>{

	private String player;
	private String deathTime;
	private List<ItemStack> drops;
	private List<ItemStack> armor;
	private String deathLoc;
	private String world;
	private String reason;

	@SuppressWarnings("unchecked")
	public DeathDrops(String playername, ConfigurationSection deathDropData) {
		this.player = playername;
		if(deathDropData.contains("Drops")) {
			this.drops = (List<ItemStack>) deathDropData.getList("Drops");
		}
		if(deathDropData.contains("Armor")) {
			System.out.println("has armor");
			this.armor = (List<ItemStack>) deathDropData.getList("Armor");
		}
		if(deathDropData.contains("Time")) {
			this.deathTime = deathDropData.getString("Time");
		}
		if(deathDropData.contains("Location")) {
			this.deathLoc = deathDropData.getString("Location");
		}
		if(deathDropData.contains("World")) {
			this.setWorld(deathDropData.getString("World"));
		}
		if(deathDropData.contains("Reason")) {
			this.reason = deathDropData.getString("Reason");
		}
	}

	public String getPlayer() {
		return player;
	}

	public void setPlayer(String player) {
		this.player = player;
	}

	public String getDeathTime() {
		return deathTime;
	}

	public String getReason() {
		return reason;
	}

	public void setDeathTime(String time) {
		this.deathTime = time;
	}

	public List<ItemStack> getDrops() {
		return drops;
	}

	public List<ItemStack> getDropsSanArmor() {
		List<ItemStack> dropsNA = drops;
		for(int i = 0; i < armor.size(); i++) {
			if(dropsNA.contains(armor.get(i))) {
				dropsNA.remove(armor.get(i));
			}
		}
		return dropsNA;
	}

	public void setDrops(List<ItemStack> drops) {
		this.drops = drops;
	}

	public String getDeathLoc() {
		return deathLoc;
	}

	public void setDeathLoc(String deathLoc) {
		this.deathLoc = deathLoc;
	}

	public String getWorld() {
		return world;
	}

	public void setWorld(String world) {
		this.world = world;
	}

	@Override
	public int compareTo(Object o) {
		DateFormat formatter;
		Date date1 = null;
		Date date2 = null;
		formatter = new SimpleDateFormat("M/d/yy h:mm a");
		try {
			date1 = (Date) formatter.parse(deathTime);
			DeathDrops other = (DeathDrops) o;
			date2 = (Date) formatter.parse(other.getDeathTime());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		catch(NullPointerException npe){
			System.out.println("Exception thrown "+npe.getMessage()+" date1 is "+date1+" date2 is "+date2);
		}
		return date2.compareTo(date1);
	}

	public List<ItemStack> getArmor() {
		return armor;
	}

	public void setArmor(List<ItemStack> armor) {
		this.armor = armor;
	}

	public boolean hasArmor() {
		if(armor == null || armor.isEmpty()) return false;
		return true;
	}

}
