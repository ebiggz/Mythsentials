package com.mythicacraft.plugins.mythsentials.pets;

import java.util.List;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;


public class CmdProperties {

	private Type type;
	private Player newOwner;
	private OfflinePlayer newOwnerO;
	private boolean useOfflineP;

	public CmdProperties(Type type) {
		this.type = type;
	}

	public CmdProperties(Type type, Player newOwner) {
		this.newOwner = newOwner;
		this.type = type;
	}

	public CmdProperties(Type type, OfflinePlayer newOwner) {
		this.newOwnerO = newOwner;
		this.type = type;
		useOfflineP = true;
	}

	public CmdProperties(Type type, List<String> guests) {
		this.type = type;
	}

	public enum Type {
		GIVE, INFO
	}

	//getter and setters
	public Type getType() {
		return type;
	}

	public Player getNewOwner() {
		return newOwner;
	}

	public OfflinePlayer getNewOwnerO() {
		return newOwnerO;
	}

	public boolean useOfflinePlayer(){
		return useOfflineP;
	}
}
